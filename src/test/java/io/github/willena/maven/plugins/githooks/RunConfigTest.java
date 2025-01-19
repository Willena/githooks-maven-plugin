package io.github.willena.maven.plugins.githooks;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RunConfigTest {
    @Test
    void getterAndSetter() {
        RunConfig config = new RunConfig();
        config.setCommand(List.of("bash", "-c", "echo"));
        config.setArgs(List.of("abc"));
        config.setClassName(String.class.getName());
        Mojo p = new Mojo();
        config.setMojo(p);

        assertEquals(List.of("abc"), config.getArgs());
        assertEquals(List.of("bash", "-c", "echo"), config.getCommand());
        assertEquals(String.class.getName(), config.getClassName());
        assertEquals(p, config.getMojo());
    }
}