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

import java.util.Objects;
import org.apache.maven.plugins.annotations.Parameter;

public class HookDefinitionConfig {
    @Parameter(name = "enabled")
    private boolean enabled = true;

    @Parameter(name = "name")
    private String name;

    @Parameter(name = "description")
    private String description;

    @Parameter(name = "runConfig")
    private RunConfig runConfig;

    @Parameter(name = "skipIf")
    private ConditionalRunConfig skipIf;

    @Parameter(name = "onlyIf")
    private ConditionalRunConfig onlyIf;

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

    public HookDefinitionConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public HookDefinitionConfig setName(String name) {
        this.name = name;
        return this;
    }

    public HookDefinitionConfig setDescription(String description) {
        this.description = description;
        return this;
    }

    public HookDefinitionConfig setRunConfig(RunConfig runConfig) {
        this.runConfig = runConfig;
        return this;
    }

    public ConditionalRunConfig getOnlyIf() {
        return onlyIf;
    }

    public ConditionalRunConfig getSkipIf() {
        return skipIf;
    }

    public HookDefinitionConfig setOnlyIf(ConditionalRunConfig onlyIf) {
        this.onlyIf = onlyIf;
        return this;
    }

    public HookDefinitionConfig setSkipIf(ConditionalRunConfig skipIf) {
        this.skipIf = skipIf;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HookDefinitionConfig that = (HookDefinitionConfig) o;
        return enabled == that.enabled
                && Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(runConfig, that.runConfig)
                && Objects.equals(skipIf, that.skipIf)
                && Objects.equals(onlyIf, that.onlyIf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, name, description, runConfig, skipIf, onlyIf);
    }

    @Override
    public String toString() {
        return "HookDefinition{"
                + "enabled="
                + enabled
                + ", name='"
                + name
                + '\''
                + ", description='"
                + description
                + '\''
                + ", runConfig="
                + runConfig
                + ", skipIf="
                + skipIf
                + ", onlyIf="
                + onlyIf
                + '}';
    }
}
