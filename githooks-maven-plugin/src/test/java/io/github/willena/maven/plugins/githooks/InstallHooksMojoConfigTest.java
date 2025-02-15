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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

public class InstallHooksMojoConfigTest extends AbstractMojoTestCase {

    public void testMojoConfigPopulated() throws Exception {
        Path pom = Path.of(getClass().getResource("install-pom.xml").toURI());
        Path newProjectPom = createNewProject(pom).resolve("pom.xml");

        MavenProject project = readMavenProject(newProjectPom);
        InstallHooksMojo mojo = (InstallHooksMojo) lookupConfiguredMojo(project, "install");
        assertNotNull(mojo);

        Map<String, String> gitConfig = Map.ofEntries(Map.entry("config.section.test", "value"));

        Plugin plugin = new Plugin();
        plugin.setArtifactId("id");
        plugin.setGroupId("group");
        plugin.setVersion("1.2.3");

        XmlPlexusConfiguration config = new XmlPlexusConfiguration("configuration");
        config.addChild("config", "value");

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
                                                                        .setMojo(
                                                                                new MojoConfig()
                                                                                        .setGoal(
                                                                                                "run")
                                                                                        .setPlugin(
                                                                                                plugin)
                                                                                        .setConfiguration(
                                                                                                config))))),
                        new HookConfig()
                                .setType(HookType.PRE_COMMIT)
                                .setHookDefinitions(
                                        List.of(
                                                new HookDefinitionConfig()
                                                        .setName("name")
                                                        .setDescription("This is a simple hook")
                                                        .setRunConfig(
                                                                new RunConfig()
                                                                        .setCommand(
                                                                                List.of(
                                                                                        "value",
                                                                                        "value",
                                                                                        "value"))),
                                                new HookDefinitionConfig()
                                                        .setName("other")
                                                        .setEnabled(false)
                                                        .setRunConfig(
                                                                new RunConfig()
                                                                        .setClassName(
                                                                                String.class
                                                                                        .getName())
                                                                        .setArgs(
                                                                                List.of(
                                                                                        "--arg1",
                                                                                        "--value"))))));

        assertEquals(gitConfig, mojo.getGitConfig());
        assertEquals(2, mojo.getHooks().size());
        assertEquals(hookConfigs.get(1), mojo.getHooks().get(1));
        assertEquals(HookType.PRE_PUSH, mojo.getHooks().get(0).getType());

        assertTrue(mojo.isSkip());
        assertEquals("script", mojo.getHookScriptTemplate());
    }

    public void testMojoSkip() throws Exception {
        Path pom = Path.of(getClass().getResource("install-simple-skip.pom").toURI());
        Path newProjectPom = createNewProject(pom).resolve("pom.xml");

        MavenProject project = readMavenProject(newProjectPom);
        InstallHooksMojo mojo = (InstallHooksMojo) lookupConfiguredMojo(project, "install");
        assertNotNull(mojo);

        assertDoesNotThrow(mojo::execute);
    }

    public void testMojoInstalledHooks() throws Exception {
        Path pom = Path.of(getClass().getResource("install-simple-execute.pom").toURI());
        Path newProjectDir = createNewProject(pom);
        Path newProjectPom = newProjectDir.resolve("pom.xml");

        MavenProject project = readMavenProject(newProjectPom);
        InstallHooksMojo mojo = (InstallHooksMojo) lookupConfiguredMojo(project, "install");
        assertNotNull(mojo);

        assertDoesNotThrow(mojo::execute);

        Path hooksPath = GitUtils.getHooksPath(newProjectDir);

        List<Path> allHooksFile = Files.list(hooksPath).toList();
        Path preCommitFile = hooksPath.resolve(HookType.PRE_COMMIT.getFileName());
        assertEquals(
                Stream.of(preCommitFile, hooksPath.resolve(HookType.PRE_PUSH.getFileName()))
                        .sorted()
                        .toList(),
                allHooksFile.stream().sorted().toList());

        assertEquals(
                "#!/bin/sh\n"
                        + "args=$(IFS=, ; echo \"$*\");\n"
                        + "export PATH=\"${javaBin}:${mavenBin}:$PATH\";\n"
                        + "export JAVA_HOME=\"${javaHome}\";\n"
                        + "export MAVEN_HOME=\"${mavenHome}\";\n"
                        + "mvn githooks:run \"-Dhook.name=PRE_COMMIT\" \"-Dhook.args=${args}\";",
                Files.readString(preCommitFile));
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
