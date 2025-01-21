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

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import org.junit.Test;

public class HookTest {
    @Test
    public void getterSetter() {
        Hook hook =
                new Hook().setType(HookType.PRE_PUSH).setHookDefinitions(Collections.emptyList());

        assertEquals(HookType.PRE_PUSH, hook.getType());
        assertEquals(Collections.emptyList(), hook.getHookDefinitions());
    }

    @Test
    public void hascodeTest() {
        Hook hook =
                new Hook().setType(HookType.PRE_PUSH).setHookDefinitions(Collections.emptyList());
        Hook hook1 =
                new Hook().setType(HookType.PRE_PUSH).setHookDefinitions(Collections.emptyList());
        Hook hook2 =
                new Hook().setType(HookType.PRE_PUSH).setHookDefinitions(Collections.emptyList());
        assertNotEquals(hook2.hashCode(), hook);
        assertEquals(hook.hashCode(), hook1.hashCode());
    }
}
