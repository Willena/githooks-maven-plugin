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

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.DefaultBuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystemSession;
import org.junit.jupiter.api.Test;

class HookConfigRunnerTest {

    @Test
    void runOnlyOneEnabled()
            throws IOException,
                    InvocationTargetException,
                    InstantiationException,
                    IllegalAccessException {
        DemoMain.receivedArgs = null;
        List<HookDefinitionConfig> definitions =
                List.of(
                        new HookDefinitionConfig()
                                .setName("first")
                                .setEnabled(true)
                                .setRunConfig(
                                        new RunConfig()
                                                .setArgs(List.of("A"))
                                                .setClassName(DemoMain.class.getName())),
                        new HookDefinitionConfig()
                                .setName("second")
                                .setEnabled(false)
                                .setRunConfig(
                                        new RunConfig()
                                                .setClassName(DemoMain.class.getSimpleName())
                                                .setArgs(List.of("B"))),
                        new HookDefinitionConfig()
                                .setName("thrid")
                                .setEnabled(true)
                                .setRunConfig(
                                        new RunConfig()
                                                .setClassName(DemoMain.class.getSimpleName())
                                                .setArgs(List.of("C"))));

        HookRunner runner =
                new HookRunner(
                        definitions,
                        new FakeLogger(),
                        new HookRunner.HookRunnerConfig.Builder()
                                .skipRuns(List.of("thrid"))
                                .build());

        assertDoesNotThrow(() -> runner.run());
        assertEquals(List.of("A"), Arrays.asList(DemoMain.receivedArgs));
    }

    @Test
    void runClass()
            throws IOException,
                    InvocationTargetException,
                    InstantiationException,
                    IllegalAccessException {
        HookRunner runner =
                new HookRunner(
                        List.of(),
                        new FakeLogger(),
                        new HookRunner.HookRunnerConfig.Builder().build());
        assertDoesNotThrow(
                () ->
                        runner.runClass(
                                new RunConfig()
                                        .setClassName(DemoMain.class.getName())
                                        .setArgs(List.of("1", "Z"))));
        assertEquals(List.of("1", "Z"), Arrays.asList(DemoMain.receivedArgs));
    }

    @Test
    void runClassFail()
            throws IOException,
                    InvocationTargetException,
                    InstantiationException,
                    IllegalAccessException {
        HookRunner runner =
                new HookRunner(
                        List.of(),
                        new FakeLogger(),
                        new HookRunner.HookRunnerConfig.Builder().build());
        assertThrows(
                MojoExecutionException.class,
                () ->
                        runner.runClass(
                                new RunConfig()
                                        .setClassName("a.class.that.does.not.Exists")
                                        .setArgs(List.of("1", "Z"))));
    }

    @Test
    void runCommand()
            throws IOException,
                    InvocationTargetException,
                    InstantiationException,
                    IllegalAccessException {
        HookRunner runner =
                new HookRunner(
                        List.of(),
                        new FakeLogger(),
                        new HookRunner.HookRunnerConfig.Builder().build());
        assertThrows(
                MojoExecutionException.class,
                () ->
                        runner.runCommand(
                                new RunConfig()
                                        .setCommand(List.of("aCommandThatDoesNotExists"))
                                        .setArgs(List.of("tt"))));
    }

    @Test
    void runMojo()
            throws IOException,
                    InvocationTargetException,
                    InstantiationException,
                    IllegalAccessException {
        HookRunner runner =
                new HookRunner(
                        List.of(),
                        new FakeLogger(),
                        new HookRunner.HookRunnerConfig.Builder()
                                .mavenSession(
                                        new MavenSession(
                                                null,
                                                (RepositorySystemSession) null,
                                                new DefaultMavenExecutionRequest(),
                                                null))
                                .mavenProject(new MavenProject())
                                .pluginManager(new DefaultBuildPluginManager())
                                .build());
        assertThrows(
                MojoExecutionException.class,
                () ->
                        runner.runMojo(
                                new RunConfig()
                                        .setMojo(
                                                new MojoConfig()
                                                        .setGoal("goal")
                                                        .setPlugin(new Plugin()))));
    }

    @Test
    void runConfigInvalid()
            throws IOException,
                    InvocationTargetException,
                    InstantiationException,
                    IllegalAccessException {
        RunConfig config =
                new RunConfig()
                        .setCommand(Collections.emptyList())
                        .setMojo(new MojoConfig())
                        .setClassName("");
        HookRunner runner =
                new HookRunner(
                        List.of(),
                        new FakeLogger(),
                        new HookRunner.HookRunnerConfig.Builder().build());

        assertThrows(IllegalArgumentException.class, () -> runner.run(config));

        RunConfig config2 = new RunConfig();
        assertThrows(IllegalArgumentException.class, () -> runner.run(config2));
    }
}
