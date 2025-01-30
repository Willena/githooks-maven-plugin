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

import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;
import java.util.Objects;

public class HookConfig {
    @Parameter(name = "type") private HookType type;
    @Parameter(name = "hookDefinitions") private List<HookDefinitionConfig> hookDefinitionConfigs;

    public HookType getType() {
        return type;
    }

    public HookConfig setType(HookType type) {
        this.type = type;
        return this;
    }

    public List<HookDefinitionConfig> getHookDefinitions() {
        return hookDefinitionConfigs;
    }

    public HookConfig setHookDefinitions(List<HookDefinitionConfig> hookDefinitionConfigs) {
        this.hookDefinitionConfigs = hookDefinitionConfigs;
        return this;
    }

    @Override
    public String toString() {
        return "Hook{" + "type=" + type + ", hookDefinitions=" + hookDefinitionConfigs + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HookConfig hookConfig = (HookConfig) o;
        return type == hookConfig.type && Objects.equals(hookDefinitionConfigs, hookConfig.hookDefinitionConfigs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, hookDefinitionConfigs);
    }
}
