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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import javax.inject.Inject;

/**
 * Run all defined hooks scripts for a git hook type
 */
@Mojo(name = "run")
public class RunHooksMojo extends AbstractMojo {

    private final Map<String, RunnableGitHook> runnableHooks;

    @Parameter(name = "hook", property = "hook.name", required = true)
    protected HookType hook;

    @Parameter(name = "args", property = "hook.args")
    protected List<String> args;

    @Parameter(name = "skipRuns", property = "hook.skipRuns")
    protected List<String> skipRuns;

    @Parameter(name = "hooks", required = true)
    protected List<HookConfig> hooks;

    @Parameter(name = "skip", property = "hook.skip")
    protected boolean skip;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject mavenProject;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager pluginManager;

    @Inject
    public RunHooksMojo(Map<String, RunnableGitHook> availableCodeHooks) {
        this.runnableHooks = availableCodeHooks;
        getLog().debug("Registered hook classes and names: " + availableCodeHooks.toString());
    }

    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().warn("Hooks run skipped by configuration");
            return;
        }

        if (hook == null) {
            throw new MojoExecutionException("Please specify a hook name");
        }

        Map<HookType, List<HookDefinitionConfig>> hooksByType =
                hooks.stream()
                        .map(e -> Map.entry(e.getType(), e.getHookDefinitions()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        new HookRunner(
                hooksByType.getOrDefault(hook, Collections.emptyList()),
                getLog(),
                new HookRunner.HookRunnerConfig.Builder()
                        .skipRuns(skipRuns)
                        .args(args)
                        .pluginManager(pluginManager)
                        .mavenProject(mavenProject)
                        .mavenSession(mavenSession)
                        .runnableHooks(this.runnableHooks)
                        .build())
                .run();
    }

    public boolean isSkip() {
        return skip;
    }

    public List<String> getArgs() {
        return args;
    }

    public List<String> getSkipRuns() {
        return skipRuns;
    }

    public HookType getHook() {
        return hook;
    }

    public List<HookConfig> getHooks() {
        return hooks;
    }
}
