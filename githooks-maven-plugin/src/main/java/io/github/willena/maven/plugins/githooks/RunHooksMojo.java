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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Execute hook configured scripts and commands */
@Mojo(name = "run")
public class RunHooksMojo extends AbstractMojo {

    @Parameter(name = "hook", required = true)
    protected HookType hook;

    @Parameter(name = "args")
    protected List<String> args;

    @Parameter(name = "skipRuns")
    protected List<String> skipRuns;

    @Parameter(name = "hooks", required = true)
    protected List<HookConfig> hooks;

    @Parameter(name = "skip")
    protected boolean skip;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject mavenProject;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession mavenSession;

    @Component private BuildPluginManager pluginManager;

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
