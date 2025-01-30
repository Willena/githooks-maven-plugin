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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class HookConfigTest {
    @Test
    public void getterSetter() {
        HookConfig hookConfig =
                new HookConfig().setType(HookType.PRE_PUSH).setHookDefinitions(Collections.emptyList());

        assertEquals(HookType.PRE_PUSH, hookConfig.getType());
        assertEquals(Collections.emptyList(), hookConfig.getHookDefinitions());
    }

    @Test
    public void hascodeTest() {
        HookConfig hookConfig =
                new HookConfig().setType(HookType.PRE_PUSH).setHookDefinitions(Collections.emptyList());
        HookConfig hookConfig1 =
                new HookConfig().setType(HookType.PRE_PUSH).setHookDefinitions(Collections.emptyList());
        HookConfig hookConfig2 =
                new HookConfig().setType(HookType.PRE_PUSH).setHookDefinitions(Collections.emptyList());
        assertNotEquals(hookConfig2.hashCode(), hookConfig);
        assertEquals(hookConfig.hashCode(), hookConfig1.hashCode());
    }
}
