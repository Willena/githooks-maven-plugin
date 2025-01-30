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

import java.util.List;
import java.util.Objects;
import org.apache.maven.plugins.annotations.Parameter;

public class RunConfig {
    @Parameter(name = "className")
    private String className;

    @Parameter(name = "command")
    private List<String> command;

    @Parameter(name = "args")
    private List<String> args;

    @Parameter(name = "mojo")
    private MojoConfig mojoConfig;

    public String getClassName() {
        return className;
    }

    public List<String> getCommand() {
        return command;
    }

    public List<String> getArgs() {
        return args;
    }

    public RunConfig setClassName(String className) {
        this.className = className;
        return this;
    }

    public RunConfig setCommand(List<String> command) {
        this.command = command;
        return this;
    }

    public RunConfig setArgs(List<String> args) {
        this.args = args;
        return this;
    }

    public MojoConfig getMojo() {
        return mojoConfig;
    }

    public RunConfig setMojo(MojoConfig plugin) {
        this.mojoConfig = plugin;
        return this;
    }

    @Override
    public String toString() {
        return "RunConfig{"
                + "className='"
                + className
                + '\''
                + ", command="
                + command
                + ", args="
                + args
                + ", mojo="
                + mojoConfig
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RunConfig runConfig = (RunConfig) o;
        return Objects.equals(className, runConfig.className)
                && Objects.equals(command, runConfig.command)
                && Objects.equals(args, runConfig.args)
                && Objects.equals(mojoConfig, runConfig.mojoConfig);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, command, args, mojoConfig);
    }
}
