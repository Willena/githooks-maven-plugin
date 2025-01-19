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

/**
 * Execute hook configured scripts and commands
 */
@Mojo(name = "run")
public class RunHooksMojo extends AbstractMojo {

    @Parameter(name = "hook")
    protected HookType hook;

    @Parameter(name = "args")
    protected List<String> args;

    @Parameter(name = "skipRuns")
    protected List<String> skipRuns;

    @Parameter(name = "hooks")
    protected List<Hook> hooks;

    @Parameter(name = "skip")
    protected boolean skip;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject mavenProject;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager pluginManager;


    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().warn("Hooks run skipped by configuration");
            return;
        }

        Map<HookType, List<HookDefinition>> hooksByType = hooks.stream().map(e -> Map.entry(e.getType(), e.getHookDefinitions())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        new HookRunner(hooksByType.getOrDefault(hook, Collections.emptyList()),
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
}