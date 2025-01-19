package io.github.willena.maven.plugins.githooks;

import org.junit.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class HookTest {
    @Test
    public void getterSetter() {
        Hook hook = new Hook().setType(HookType.PRE_PUSH).setHookDefinitions(Collections.emptyList());

        assertEquals(HookType.PRE_PUSH, hook.getType());
        assertEquals(Collections.emptyList(), hook.getHookDefinitions());
    }

    @Test
    public void hascodeTest() {
        Hook hook = new Hook().setType(HookType.PRE_PUSH).setHookDefinitions(Collections.emptyList());
        Hook hook1 = new Hook().setType(HookType.PRE_PUSH).setHookDefinitions(Collections.emptyList());
        Hook hook2 = new Hook().setType(HookType.PRE_PUSH).setHookDefinitions(Collections.emptyList());
        assertNotEquals(hook2.hashCode(), hook);
        assertEquals(hook.hashCode(), hook1.hashCode());
    }
}