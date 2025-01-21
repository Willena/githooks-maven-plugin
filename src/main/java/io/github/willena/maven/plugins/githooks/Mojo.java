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
import org.apache.maven.model.Plugin;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.configuration.PlexusConfiguration;

public class Mojo {
    @Parameter private Plugin plugin;
    @Parameter private String goal;
    @Parameter private PlexusConfiguration configuration;

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
        return "Mojo{"
                + "plugin="
                + plugin
                + ", goal='"
                + goal
                + '\''
                + ", configuration="
                + configuration
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Mojo mojo = (Mojo) o;
        return Objects.equals(plugin, mojo.plugin)
                && Objects.equals(goal, mojo.goal)
                && Objects.equals(configuration, mojo.configuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plugin, goal, configuration);
    }
}
