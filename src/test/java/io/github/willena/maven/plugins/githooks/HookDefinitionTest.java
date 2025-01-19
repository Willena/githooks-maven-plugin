package io.github.willena.maven.plugins.githooks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HookDefinitionTest {
    @Test
    void getterSetter() {
        HookDefinition def = new HookDefinition();
        def.setDescription("Desc");
        def.setEnabled(true);
        def.setRunConfig(new RunConfig());
        def.setName("name");

        assertEquals("name", def.getName());
        assertEquals("Desc", def.getDescription());
        assertEquals(true, def.isEnabled());
        assertNotNull(def.getRunConfig());

    }
}