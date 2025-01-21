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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HookDefinitionTest {
    @Test
    void getterSetter() {
        HookDefinition def = new HookDefinition();
        def.setDescription("Desc");
        def.setEnabled(true);
        def.setRunConfig(new RunConfig());
        def.setName("name");

        assertEquals("name", def.getName());
        assertEquals("Desc", def.getDescription());
        assertEquals(true, def.isEnabled());
        assertNotNull(def.getRunConfig());

    }
}