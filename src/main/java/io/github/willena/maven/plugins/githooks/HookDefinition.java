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

import java.util.Objects;

public class HookDefinition {
    @Parameter
    private boolean enabled = true;

    @Parameter
    private String name;

    @Parameter
    private String description;

    @Parameter
    private RunConfig runConfig;

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public RunConfig getRunConfig() {
        return runConfig;
    }

    public HookDefinition setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public HookDefinition setName(String name) {
        this.name = name;
        return this;
    }

    public HookDefinition setDescription(String description) {
        this.description = description;
        return this;
    }

    public HookDefinition setRunConfig(RunConfig runConfig) {
        this.runConfig = runConfig;
        return this;
    }

    @Override
    public String toString() {
        return "HookDefinition{" +
                "enabled=" + enabled +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", runConfig=" + runConfig +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HookDefinition that = (HookDefinition) o;
        return enabled == that.enabled && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(runConfig, that.runConfig);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, name, description, runConfig);
    }
}
