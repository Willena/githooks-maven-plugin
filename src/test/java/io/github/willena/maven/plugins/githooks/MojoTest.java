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

import org.apache.maven.model.Plugin;
import org.codehaus.plexus.configuration.DefaultPlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MojoTest {
    @Test
    void getterAndSetter() {
        Plugin p = new Plugin();
        PlexusConfiguration c = new DefaultPlexusConfiguration("");

        Mojo mojo = new Mojo();
        mojo.setPlugin(p);
        mojo.setConfiguration(c);
        mojo.setGoal("test");

        assertEquals(p, mojo.getPlugin());
        assertEquals(c, mojo.getConfiguration());
        assertEquals("test", mojo.getGoal());
    }
}