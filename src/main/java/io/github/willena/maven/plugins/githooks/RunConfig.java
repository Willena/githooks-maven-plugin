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

public class RunConfig {
    private String className;
    private List<String> command;
    private List<String> args;
    private Mojo mojo;

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

    public Mojo getMojo() {
        return mojo;
    }

    public RunConfig setMojo(Mojo plugin) {
        this.mojo = plugin;
        return this;
    }

    @Override
    public String toString() {
        return "RunConfig{" +
                "className='" + className + '\'' +
                ", command=" + command +
                ", args=" + args +
                ", mojo=" + mojo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RunConfig runConfig = (RunConfig) o;
        return Objects.equals(className, runConfig.className) && Objects.equals(command, runConfig.command) && Objects.equals(args, runConfig.args) && Objects.equals(mojo, runConfig.mojo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, command, args, mojo);
    }
}
