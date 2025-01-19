package io.github.willena.maven.plugins.githooks;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class HookScriptWriterTest {

    private static final Path TARGET = Path.of("target");

    @Test
    void writeHook() throws IOException {
        HookScriptWriter writer = new HookScriptWriter("bash -c echo ${mavenHome} ${javaHome}", "a", "b");
        Path repo = Files.createTempDirectory(TARGET, "");
        Path r = writer.writeHook(HookType.COMMIT_MSG, repo);
        String content = Files.readString(r);
        assertTrue(r.toString().contains(HookType.COMMIT_MSG.getFileName()));
        assertEquals("bash -c echo a b", content);
    }

    @Test
    void writeHookDefault() throws IOException {
        HookScriptWriter writer = new HookScriptWriter(null, "a", "b");
        Path repo = Files.createTempDirectory(TARGET, "");
        Path r = writer.writeHook(HookType.COMMIT_MSG, repo);
        String content = Files.readString(r);
        assertTrue(r.toString().contains(HookType.COMMIT_MSG.getFileName()));
        assertEquals("#!/bin/sh\n" +
                "args=$(IFS=, ; echo \"$*\");\n" +
                "export PATH=b:a:$PATH;\n" +
                "mvn githooks:run \"-Dhook=COMMIT_MSG\" \"-Dhook.args=${args}\";", content);
    }
}