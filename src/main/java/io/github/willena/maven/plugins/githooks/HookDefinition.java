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
