package io.github.willena.maven.plugins.githooks;

import org.apache.maven.model.Plugin;
import org.codehaus.plexus.configuration.DefaultPlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MojoTest {
    @Test
    void getterAndSetter() {
        Plugin p = new Plugin();
        PlexusConfiguration c = new DefaultPlexusConfiguration("");

        Mojo mojo = new Mojo();
        mojo.setPlugin(p);
        mojo.setConfiguration(c);
        mojo.setGoal("test");

        assertEquals(p, mojo.getPlugin());
        assertEquals(c, mojo.getConfiguration());
        assertEquals("test", mojo.getGoal());
    }
}