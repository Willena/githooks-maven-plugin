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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ConditionalRunConfigTest {

    @Test
    void getterSetter() {
        ConditionalRunConfig r = new ConditionalRunConfig();
        r.setRef("ref");

        assertEquals("ref", r.getRef());
    }

    @Test
    void equalsTests() {
        ConditionalRunConfig r = new ConditionalRunConfig();
        r.setRef("ref");

        ConditionalRunConfig r2 = new ConditionalRunConfig();
        ConditionalRunConfig r3 = new ConditionalRunConfig();
        r3.setRef("ref");
        assertNotEquals(r, r2);
        assertEquals(r, r3);
        assertNotEquals(r, new Object());

        assertEquals(r3.hashCode(), r.hashCode());
        assertNotEquals(r3.hashCode(), r2.hashCode());

    }

}