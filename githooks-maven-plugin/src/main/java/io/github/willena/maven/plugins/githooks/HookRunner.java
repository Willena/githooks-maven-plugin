/*
 * Copyright 2025 Willena (Guillaume VILLENA)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.willena.maven.plugins.githooks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.twdata.maven.mojoexecutor.MojoExecutor;

public class HookRunner {
    private final List<HookDefinitionConfig> hooksToRun;
    private final Log log;
    private final HookRunnerConfig config;
    private final ExecutorService executor;

    public HookRunner(List<HookDefinitionConfig> hooksToRun, Log log, HookRunnerConfig config)
            throws IOException,
                    InvocationTargetException,
                    InstantiationException,
                    IllegalAccessException {
        this.hooksToRun = hooksToRun;
        this.log = log;
        this.config = config;
        this.executor = Executors.newSingleThreadExecutor();
        // TODO: Could not call this, because not performant. Need to improve and find a solution.
        // this.hookClassRegistry = new RunnableHookRegistry();
        // this.hookClassRegistry.findAllHooks();
    }

    public void run() throws MojoExecutionException {
        List<HookDefinitionConfig> toRun = hooksToRun.stream().filter(this::keepRun).toList();

        log.debug(String.format("Runs: %s", toRun));

        for (HookDefinitionConfig hookDefinitionConfig : toRun) {
            log.info("Running " + hookDefinitionConfig.getName());
            this.run(hookDefinitionConfig.getRunConfig());
        }
    }

    private boolean keepRun(HookDefinitionConfig h) {
        if (!h.isEnabled()) {
            return false;
        }

        if (config.getSkipRuns().contains(h.getName())) {
            return false;
        }

        if (h.getSkipIf() != null && h.getSkipIf().getRef() != null) {
            return !GitUtils.headMatchesRefPattern(
                    config.getMavenProject().getBasedir().toPath(),
                    Pattern.compile(h.getSkipIf().getRef()));
        }

        if (h.getOnlyIf() != null && h.getOnlyIf().getRef() != null) {
            return GitUtils.headMatchesRefPattern(
                    config.getMavenProject().getBasedir().toPath(),
                    Pattern.compile(h.getOnlyIf().getRef()));
        }

        return true;
    }

    public void run(RunConfig runConfig) throws MojoExecutionException {
        if (Stream.of(runConfig.getCommand(), runConfig.getMojo(), runConfig.getClassName())
                        .filter(Objects::nonNull)
                        .toList()
                        .size()
                > 1) {
            throw new IllegalArgumentException(
                    "Command, Mojo and ClassName are mutually exclusive");
        }

        if (runConfig.getCommand() != null && !runConfig.getCommand().isEmpty()) {
            this.runCommand(runConfig);
        } else if (runConfig.getClassName() != null) {
            this.runClass(runConfig);
        } else if (runConfig.getMojo() != null) {
            this.runMojo(runConfig);
        } else {
            throw new IllegalArgumentException(
                    "Invalid run config: Must specify at least an action");
        }
    }

    protected void runMojo(RunConfig runConfig) throws MojoExecutionException {
        MojoExecutor.executeMojo(
                runConfig.getMojo().getPlugin(),
                runConfig.getMojo().getGoal(),
                Optional.ofNullable(runConfig.getMojo().getConfiguration())
                        .map(HookRunner::toXpp3Dom)
                        .orElse(MojoExecutor.configuration()),
                MojoExecutor.executionEnvironment(
                        config.getMavenProject(),
                        config.getMavenSession(),
                        config.getPluginManager()));
    }

    /**
     * Converts PlexusConfiguration to a Xpp3Dom.
     *
     * @param config the PlexusConfiguration. Must not be {@code null}.
     * @return the Xpp3Dom representation of the PlexusConfiguration
     */
    private static Xpp3Dom toXpp3Dom(PlexusConfiguration config) {
        Xpp3Dom result = new Xpp3Dom(config.getName());
        result.setValue(config.getValue(null));
        for (String name : config.getAttributeNames()) {
            result.setAttribute(name, config.getAttribute(name));
        }
        for (PlexusConfiguration child : config.getChildren()) {
            result.addChild(toXpp3Dom(child));
        }
        return result;
    }

    private List<String> computeArgs(RunConfig runConfig) {
        List<String> allArgs =
                new LinkedList<>(
                        Optional.ofNullable(runConfig.getArgs()).orElse(Collections.emptyList()));
        List<String> otherArgs =
                Optional.ofNullable(config.getArgs()).orElse(Collections.emptyList());
        allArgs.addAll(otherArgs);
        return allArgs;
    }

    protected void runClass(RunConfig runConfig) throws MojoExecutionException {
        try {

            // Get the hook
            // TODO: Until the registry is able to automatically discover classes efficiently,
            // disable registry lookup;
            //  Replaced with manual lookup of a single class.

            // RunnableGitHook hook = hookClassRegistry.get(runConfig.getClassName());
            List<Map.Entry<String, RunnableGitHook>> hooks =
                    RunnableHookRegistry.findHooksFromClass(runConfig.getClassName());
            if (hooks.isEmpty()) {
                throw new MojoExecutionException(
                        "Could not find any code to run in provided class"
                                + runConfig.getClassName());
            }
            RunnableGitHook hook = hooks.get(0).getValue();

            // Run the hook
            String[] args = computeArgs(runConfig).toArray(new String[0]);
            hook.run(
                    new HookContext(config.getMavenProject(), config.getMavenSession(), log), args);
        } catch (Exception e) {
            throw new MojoExecutionException("Error while running hook", e);
        }
    }

    protected void runCommand(RunConfig runConfig) throws MojoExecutionException {
        List<String> allArgs = new LinkedList<>(runConfig.getCommand());
        allArgs.addAll(computeArgs(runConfig));

        try {
            log.info("Executing hook command `" + runConfig.getCommand() + "` ");
            Process process = Runtime.getRuntime().exec(allArgs.toArray(new String[0]));
            executor.submit(
                    () ->
                            new BufferedReader(new InputStreamReader(process.getInputStream()))
                                    .lines()
                                    .forEach(log::info));

            int exitCode = process.waitFor();
            log.info("Exit code is " + exitCode);
            log.info(
                    " The command was finished with the status "
                            + (exitCode == 0 ? "SUCCESS" : "ERROR"));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MojoExecutionException("Could not run command: " + allArgs, e);
        } catch (IOException e) {
            throw new MojoExecutionException("Could not run command: " + allArgs, e);
        }
    }

    public static class HookRunnerConfig {
        private final List<String> args;
        private final List<String> skipRuns;
        private final MavenProject mavenProject;
        private final MavenSession mavenSession;
        private final BuildPluginManager pluginManager;

        private HookRunnerConfig(Builder builder) {
            args = builder.args;
            skipRuns = builder.skipRuns;
            mavenProject = builder.mavenProject;
            mavenSession = builder.mavenSession;
            pluginManager = builder.pluginManager;
        }

        public List<String> getArgs() {
            return args;
        }

        public List<String> getSkipRuns() {
            return skipRuns;
        }

        public MavenProject getMavenProject() {
            return mavenProject;
        }

        public MavenSession getMavenSession() {
            return mavenSession;
        }

        public BuildPluginManager getPluginManager() {
            return pluginManager;
        }

        public static final class Builder {
            private List<String> args;
            private List<String> skipRuns;
            private MavenProject mavenProject;
            private MavenSession mavenSession;
            private BuildPluginManager pluginManager;

            public Builder() {}

            public Builder args(List<String> val) {
                args = val;
                return this;
            }

            public Builder skipRuns(List<String> val) {
                skipRuns = val;
                return this;
            }

            public Builder mavenProject(MavenProject project) {
                this.mavenProject = project;
                return this;
            }

            public Builder mavenSession(MavenSession mavenSession) {
                this.mavenSession = mavenSession;
                return this;
            }

            public Builder pluginManager(BuildPluginManager manager) {
                this.pluginManager = manager;
                return this;
            }

            public HookRunnerConfig build() {
                return new HookRunnerConfig(this);
            }
        }
    }
}
