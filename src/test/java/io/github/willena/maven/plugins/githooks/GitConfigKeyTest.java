package io.github.willena.maven.plugins.githooks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GitConfigKeyTest {

    @Test
    void parse() {
        assertThrows(IllegalArgumentException.class, () -> GitConfigKey.parse(""));
        assertThrows(IllegalArgumentException.class, () -> GitConfigKey.parse("name"));
        assertThrows(IllegalArgumentException.class, () -> GitConfigKey.parse("name.other.ooo.uuu"));

        GitConfigKey simple = assertDoesNotThrow(() -> GitConfigKey.parse("section.key"));
        assertEquals("section", simple.getSection());
        assertEquals("key", simple.getName());

        GitConfigKey subsect = assertDoesNotThrow(() -> GitConfigKey.parse("section.subsection.name"));
        assertEquals("section", subsect.getSection());
        assertEquals("subsection", subsect.getSubSection());
        assertEquals("name", subsect.getName());


        GitConfigKey simple2 = assertDoesNotThrow(() -> GitConfigKey.parse("section.key"));
    }

    @Test
    void equalsTest() {
        GitConfigKey simple1 = assertDoesNotThrow(() -> GitConfigKey.parse("section.key"));
        GitConfigKey simple2 = assertDoesNotThrow(() -> GitConfigKey.parse("section.key"));
        GitConfigKey simple3 = assertDoesNotThrow(() -> GitConfigKey.parse("section.name"));

        assertTrue(simple1.equals(simple1));
        assertTrue(simple1.equals(simple2));
        assertFalse(simple1.equals(simple3));
        assertFalse(simple1.equals(null));
        assertFalse(simple1.equals(new Object()));
    }

    @Test
    void hashCodeTest() {
        GitConfigKey simple1 = assertDoesNotThrow(() -> GitConfigKey.parse("section.key"));
        GitConfigKey simple2 = assertDoesNotThrow(() -> GitConfigKey.parse("section.key"));
        GitConfigKey simple3 = assertDoesNotThrow(() -> GitConfigKey.parse("section.key1"));

        assertEquals(simple2.hashCode(), simple1.hashCode());
        assertNotEquals(simple2.hashCode(), simple3.hashCode());

    }
}