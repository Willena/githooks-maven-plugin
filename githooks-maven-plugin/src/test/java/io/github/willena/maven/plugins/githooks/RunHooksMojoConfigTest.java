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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

public class RunHooksMojoConfigTest extends AbstractMojoTestCase {

    public void testMojoConfigPopulated() throws Exception {
        Path pom = Path.of(getClass().getResource("run-pom.xml").toURI());
        Path newProjectPom = createNewProject(pom).resolve("pom.xml");

        MavenProject project = readMavenProject(newProjectPom);
        RunHooksMojo mojo = (RunHooksMojo) lookupConfiguredMojo(project, "run");
        assertNotNull(mojo);

        List<HookConfig> hookConfigs =
                List.of(
                        new HookConfig()
                                .setType(HookType.PRE_PUSH)
                                .setHookDefinitions(
                                        List.of(
                                                new HookDefinitionConfig()
                                                        .setName("mojo")
                                                        .setRunConfig(
                                                                new RunConfig()
                                                                        .setArgs(List.of("--v1"))
                                                                        .setClassName(
                                                                                DemoMain.class
                                                                                        .getName())),
                                                new HookDefinitionConfig()
                                                        .setName("other")
                                                        .setRunConfig(
                                                                new RunConfig()
                                                                        .setArgs(List.of("--v2"))
                                                                        .setClassName(
                                                                                DemoMain.class
                                                                                        .getName())))));

        assertEquals(hookConfigs, mojo.getHooks());
        assertTrue(mojo.isSkip());
        assertEquals(List.of("other"), mojo.getSkipRuns());
        assertEquals(List.of("toto"), mojo.getArgs());
        assertEquals(HookType.PRE_PUSH, mojo.getHook());
    }

    public void testSkippedExecution() throws Exception {
        Path pom = Path.of(getClass().getResource("run-pom.xml").toURI());
        Path newProjectPom = createNewProject(pom).resolve("pom.xml");

        MavenProject project = readMavenProject(newProjectPom);
        RunHooksMojo mojo = (RunHooksMojo) lookupConfiguredMojo(project, "run");
        assertNotNull(mojo);

        DemoMain.receivedArgs = null;
        assertDoesNotThrow(mojo::execute);
        assertNull(DemoMain.receivedArgs);
    }

    public void testNoHookExecution() throws Exception {
        Path pom = Path.of(getClass().getResource("run-execute-nohook-pom.xml").toURI());
        Path newProjectPom = createNewProject(pom).resolve("pom.xml");

        MavenProject project = readMavenProject(newProjectPom);
        RunHooksMojo mojo = (RunHooksMojo) lookupConfiguredMojo(project, "run");
        assertNotNull(mojo);

        assertThrows(MojoExecutionException.class, mojo::execute);
    }

    public void testExecution() throws Exception {
        Path pom = Path.of(getClass().getResource("run-execute-pom.xml").toURI());
        Path newProjectPom = createNewProject(pom).resolve("pom.xml");

        MavenProject project = readMavenProject(newProjectPom);
        RunHooksMojo mojo = (RunHooksMojo) lookupConfiguredMojo(project, "run");
        assertNotNull(mojo);

        DemoMain.receivedArgs = null;
        assertDoesNotThrow(mojo::execute);

        assertEquals(List.of("--v1", "toto"), Arrays.asList(DemoMain.receivedArgs));
    }

    protected Path createNewProject(Path pomToTest) throws IOException, GitAPIException {
        Path newProjectDir = Files.createTempDirectory("");
        Git.init().setDirectory(newProjectDir.toFile()).call();

        Files.copy(pomToTest, newProjectDir.resolve("pom.xml"));

        return newProjectDir;
    }

    protected MavenProject readMavenProject(Path pom) throws Exception {
        MavenExecutionRequest request = new DefaultMavenExecutionRequest();
        request.setBaseDirectory(pom.getParent().toFile());
        ProjectBuildingRequest configuration = request.getProjectBuildingRequest();
        configuration.setRepositorySession(new DefaultRepositorySystemSession());
        MavenProject project =
                lookup(ProjectBuilder.class).build(pom.toFile(), configuration).getProject();
        assertNotNull(project);
        return project;
    }
}
