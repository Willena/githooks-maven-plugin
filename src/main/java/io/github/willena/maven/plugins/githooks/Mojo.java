package io.github.willena.maven.plugins.githooks;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.util.Objects;

public class Mojo {
    @Parameter
    private Plugin plugin;
    @Parameter
    private String goal;
    @Parameter
    private PlexusConfiguration configuration;

    public Plugin getPlugin() {
        return plugin;
    }

    public Mojo setPlugin(Plugin plugin) {
        this.plugin = plugin;
        return this;
    }

    public String getGoal() {
        return goal;
    }

    public Mojo setGoal(String goal) {
        this.goal = goal;
        return this;
    }

    public PlexusConfiguration getConfiguration() {
        return configuration;
    }

    public Mojo setConfiguration(PlexusConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    @Override
    public String toString() {
        return "Mojo{" +
                "plugin=" + plugin +
                ", goal='" + goal + '\'' +
                ", configuration=" + configuration +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Mojo mojo = (Mojo) o;
        return Objects.equals(plugin, mojo.plugin) && Objects.equals(goal, mojo.goal) && Objects.equals(configuration, mojo.configuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plugin, goal, configuration);
    }
}
