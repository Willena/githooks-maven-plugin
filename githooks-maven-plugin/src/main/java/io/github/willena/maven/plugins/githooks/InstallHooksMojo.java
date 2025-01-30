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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static io.github.willena.maven.plugins.githooks.HookType.ALL_HOOKS_FILENAMES;

@Mojo(name = "install", defaultPhase = LifecyclePhase.VALIDATE)
public class InstallHooksMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject mavenProject;

    @Parameter(defaultValue = "${maven.home}", readonly = true, required = true)
    private String mavenHome;

    @Parameter(defaultValue = "${java.home}", readonly = true, required = true)
    private String javaHome;

    @Parameter(name = "gitConfig")
    private Map<String, String> gitConfig;

    @Parameter(name = "hooks")
    private List<HookConfig> hooks;

    @Parameter(name = "hookScriptTemplate")
    private String hookScriptTemplate;

    @Parameter(name = "skip")
    private boolean skip;

    public boolean isSkip() {
        return skip;
    }

    public String getHookScriptTemplate() {
        return hookScriptTemplate;
    }

    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("Skipping install of GitHooks maven plugin");
            return;
        }

        Path currentProjectPath = mavenProject.getBasedir().toPath();

        applyGitConfiguration(currentProjectPath);
        installHooks(currentProjectPath);
    }

    protected void installHooks(Path currentProjectPath) throws MojoExecutionException {
        try {
            Path hooksPaths = GitUtils.getHooksPath(currentProjectPath);

            getLog().info(String.format("Cleaning existing hooks from %s", currentProjectPath));
            ALL_HOOKS_FILENAMES.stream()
                    .map(n -> Path.of(currentProjectPath.toString(), n))
                    .map(Path::toFile)
                    .forEach(File::delete);

            getLog().info(String.format("Installing hooks into %s", hooksPaths));
            HookScriptWriter hookWriter =
                    new HookScriptWriter(hookScriptTemplate, mavenHome, javaHome);

            for (HookConfig hookConfig : hooks) {
                getLog().debug(String.format("Installing %s", hookConfig.getType().getFileName()));
                Path p = hookWriter.writeHook(hookConfig.getType(), hooksPaths);
                getLog().debug(String.format("Installed %s", p));
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Could not write hooks", e);
        }
    }

    protected void applyGitConfiguration(Path currentProjectPath) throws MojoExecutionException {
        if (!GitUtils.isValidGitRepository(currentProjectPath)) {
            throw new MojoExecutionException(
                    "This project is not in a valid git repository ! Consider creating one or disable the plugin");
        }

        getLog().info("Will apply git configuration to repository");
        GitUtils.writeGitConfig(currentProjectPath, gitConfig);
        getLog().info("Applied git configuration to repository");
    }

    public List<HookConfig> getHooks() {
        return hooks;
    }

    public Map<String, String> getGitConfig() {
        return gitConfig;
    }
}
