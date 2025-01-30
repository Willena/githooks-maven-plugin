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


import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RunConfigTest {
    @Test
    void getterAndSetter() {
        RunConfig config = new RunConfig();
        config.setCommand(List.of("bash", "-c", "echo"));
        config.setArgs(List.of("abc"));
        config.setClassName(String.class.getName());
        MojoConfig p = new MojoConfig();
        config.setMojo(p);

        assertEquals(List.of("abc"), config.getArgs());
        assertEquals(List.of("bash", "-c", "echo"), config.getCommand());
        assertEquals(String.class.getName(), config.getClassName());
        assertEquals(p, config.getMojo());
    }
}
