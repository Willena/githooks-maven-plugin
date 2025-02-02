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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class RunnableHookRegistryTest {

    private static final Set<String> EXPECTED_KEYS =
            Set.of(
                    "io.github.willena.maven.plugins.githooks.AllPossibleHookFound",
                    "io.github.willena.maven.plugins.githooks.AllPossibleHookFound.run",
                    "io.github.willena.maven.plugins.githooks.AllPossibleHookFound.staticRunNoArgs",
                    "io.github.willena.maven.plugins.githooks.AllPossibleHookFound.staticRunStringArgs",
                    "io.github.willena.maven.plugins.githooks.AllPossibleHookFound.staticRunAllArgs",
                    "io.github.willena.maven.plugins.githooks.AllPossibleHookFound.runNoArgs",
                    "io.github.willena.maven.plugins.githooks.AllPossibleHookFound.runStringArgs",
                    "io.github.willena.maven.plugins.githooks.AllPossibleHookFound.runAllArgs",
                    "AllPossibleHookFound",
                    "AllPossibleHookFound.run",
                    "AllPossibleHookFound.staticRunNoArgs",
                    "AllPossibleHookFound.staticRunStringArgs",
                    "AllPossibleHookFound.staticRunAllArgs",
                    "AllPossibleHookFound.runNoArgs",
                    "AllPossibleHookFound.runStringArgs",
                    "AllPossibleHookFound.runAllArgs",
                    "class-name",
                    "run-default",
                    "static-run-no-args",
                    "static-run-string-args",
                    "static-run-all-args",
                    "run-no-args",
                    "run-string-args",
                    "run-all-args");

    @Test
    void findHooksFromClassString() {
        List<Map.Entry<String, RunnableGitHook>> hooks =
                RunnableHookRegistry.findHooksFromClass(AllPossibleHookFound.class.getName());
        Set<String> keys = hooks.stream().map(Map.Entry::getKey).collect(Collectors.toSet());
        assertEquals(EXPECTED_KEYS, keys);
    }

    @Test
    void findHooksFromClass() {
        List<Map.Entry<String, RunnableGitHook>> hooks =
                RunnableHookRegistry.findHooksFromClass(AllPossibleHookFound.class);
        Set<String> keys = hooks.stream().map(Map.Entry::getKey).collect(Collectors.toSet());
        assertEquals(EXPECTED_KEYS, keys);
    }

    @Test
    void findAndRegisterHooksFromClassString() {
        RunnableHookRegistry registry = new RunnableHookRegistry();
        registry.findAndRegisterHooksFromClass(AllPossibleHookFound.class.getName());

        Set<String> keys =
                registry.getAllRegistryEntry().stream()
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet());
        assertEquals(EXPECTED_KEYS, keys);
    }

    @Test
    void FindAndRegisterHooksFromClass() {
        RunnableHookRegistry registry = new RunnableHookRegistry();
        registry.findAndRegisterHooksFromClass(AllPossibleHookFound.class);

        Set<String> keys =
                registry.getAllRegistryEntry().stream()
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet());
        assertEquals(EXPECTED_KEYS, keys);
    }

    @Test
    @Disabled
    void findAndRegisterAllHooks() {
        RunnableHookRegistry registry = new RunnableHookRegistry();
        assertDoesNotThrow(registry::findAndRegisterAllHooks);

        Set<String> keys =
                registry.getAllRegistryEntry().stream()
                        .map(Map.Entry::getKey)
                        .filter(EXPECTED_KEYS::contains)
                        .collect(Collectors.toSet());
        assertEquals(EXPECTED_KEYS, keys);
    }
}
