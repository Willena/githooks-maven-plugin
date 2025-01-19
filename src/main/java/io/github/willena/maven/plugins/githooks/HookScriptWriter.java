package io.github.willena.maven.plugins.githooks;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Optional;
import java.util.Set;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

/**
 * Simple generator of hooks scripts.
 * It generates them in a sh compatible format.
 * Also works on windows since git for windows comes with a port of bash.
 */
public class HookScriptWriter {
    private static final Set<PosixFilePermission> SCRIPT_PERMISSIONS = Set.of(PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.GROUP_EXECUTE);
    private static final String DEFAULT_HOOK_SCRIPT_TEMPLATE = "#!/bin/sh\nargs=$(IFS=, ; echo \"$*\");\nexport PATH=${javaHome}:${mavenHome}:$PATH;\nmvn githooks:run \"-Dhook=${hookName}\" \"-Dhook.args=${args}\";";
    private final String template;
    private final String mavenHome;
    private final String javaHome;

    protected HookScriptWriter(String hookTemplate, String mavenHome, String javaHome) {
        this.template = Optional.ofNullable(hookTemplate).orElse(DEFAULT_HOOK_SCRIPT_TEMPLATE);
        this.mavenHome = mavenHome;
        this.javaHome = javaHome;
    }

    public Path writeHook(HookType hookType, Path repositoryHooksPath) throws IOException {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("mavenHome", mavenHome);
        velocityContext.put("javaHome", javaHome);
        velocityContext.put("hookName", hookType.name());

        Path hookScriptPath = Path.of(repositoryHooksPath.toString(), hookType.getFileName());

        try (BufferedWriter writer = Files.newBufferedWriter(hookScriptPath, TRUNCATE_EXISTING, CREATE)) {
            if (!Velocity.evaluate(velocityContext, writer, "script-template", template)) {
                throw new IllegalStateException("Could not generate script");
            }
        } catch (ParseErrorException e) {
            throw new IllegalArgumentException("Could not parse template string", e);
        }

        if (repositoryHooksPath.getFileSystem().supportedFileAttributeViews().contains("posix")) {
            Set<PosixFilePermission> permissionModel = Files.getPosixFilePermissions(repositoryHooksPath);
            permissionModel.addAll(SCRIPT_PERMISSIONS);
            Files.setPosixFilePermissions(repositoryHooksPath, permissionModel);
        }


        return hookScriptPath;
    }

}
