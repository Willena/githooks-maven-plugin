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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class HookConfigScriptWriterTest {

    private static final Path TARGET = Path.of("target");

    @Test
    void writeHook() throws IOException {
        HookScriptWriter writer =
                new HookScriptWriter("bash -c echo ${mavenHome} ${javaHome}", "a", "b", false);
        Path repo = Files.createTempDirectory(TARGET, "");
        Path r = writer.writeHook(HookType.COMMIT_MSG, repo);
        String content = Files.readString(r);
        assertTrue(r.toString().contains(HookType.COMMIT_MSG.getFileName()));
        assertEquals("bash -c echo a b", content);
    }

    @Test
    void writeHookDefault() throws IOException {
        HookScriptWriter writer = new HookScriptWriter(null, "a", "b", false);
        Path repo = Files.createTempDirectory(TARGET, "");
        Path r = writer.writeHook(HookType.COMMIT_MSG, repo);
        String content = Files.readString(r);
        assertTrue(r.toString().contains(HookType.COMMIT_MSG.getFileName()));

        assertEquals(
                "#!/bin/sh\n"
                        + "args=$(IFS=, ; echo \"$*\");\n"
                        + "export PATH=\""
                        + Path.of("b").resolve("bin")
                        + ":"
                        + Path.of("a").resolve("bin")
                        + ":$PATH\";\n"
                        + "export JAVA_HOME=\"b\";\n"
                        + "export MAVEN_HOME=\"a\";\n"
                        + "mvn githooks:run \"-Dhook.name=COMMIT_MSG\" \"-Dhook.args=${args}\";",
                content);
    }
}
