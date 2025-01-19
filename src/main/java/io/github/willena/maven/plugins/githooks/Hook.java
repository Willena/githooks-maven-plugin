package io.github.willena.maven.plugins.githooks;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;
import java.util.Objects;

public class Hook {
    @Parameter
    private HookType type;
    @Parameter
    private List<HookDefinition> hookDefinitions;

    public HookType getType() {
        return type;
    }

    public Hook setType(HookType type) {
        this.type = type;
        return this;
    }

    public List<HookDefinition> getHookDefinitions() {
        return hookDefinitions;
    }

    public Hook setHookDefinitions(List<HookDefinition> hookDefinitions) {
        this.hookDefinitions = hookDefinitions;
        return this;
    }

    @Override
    public String toString() {
        return "Hook{" +
                "type=" + type +
                ", hookDefinitions=" + hookDefinitions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Hook hook = (Hook) o;
        return type == hook.type && Objects.equals(hookDefinitions, hook.hookDefinitions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, hookDefinitions);
    }
}
