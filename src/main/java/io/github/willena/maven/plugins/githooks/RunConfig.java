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
