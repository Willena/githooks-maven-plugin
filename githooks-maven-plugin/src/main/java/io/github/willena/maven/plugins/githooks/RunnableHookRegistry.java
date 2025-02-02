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

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

/**
 * Discovers hooks from the classpath and allow getting a hook given a class name, hook name.
 *
 * <p>Note: for now it only used to provide a simple interface
 */
public class RunnableHookRegistry {

    private final Map<String, RunnableGitHook> discoveredHooks;

    /** Create a new Registry */
    public RunnableHookRegistry() {
        this.discoveredHooks = new HashMap<>();
    }

    /**
     * Get a hook given a name. The name can be a fully qualified class name, simple name, or hook
     * name
     *
     * @param name name to look for
     * @return a unable hook if any.
     * @throws IllegalStateException if the hook cannot be found
     */
    public RunnableGitHook get(String name) {
        RunnableGitHook hook = discoveredHooks.get(name);
        if (hook == null) {
            throw new IllegalStateException("Could not find requested Hook name " + name);
        }
        return hook;
    }

    /**
     * Register a hook given its names.
     *
     * @param name name
     * @param hook non-null hook to register
     * @throws IllegalArgumentException if hook is null.
     */
    private void register(String name, RunnableGitHook hook) {
        discoveredHooks.put(name, hook);
    }

    /**
     * Gets All registered hook entries. A single Hook can be registered multiple times with
     * different names
     *
     * @return a set containing name to hook entries
     */
    public Set<Map.Entry<String, RunnableGitHook>> getAllRegistryEntry() {
        return discoveredHooks.entrySet();
    }

    /**
     * Find available hooks
     *
     * @param name class name
     * @return list containing hooks found the class
     */
    public static List<Map.Entry<String, RunnableGitHook>> findHooksFromClass(String name) {
        try {
            return findHooksFromClass(Class.forName(name));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Could not find class" + name, e);
        }
    }

    /**
     * Find available hooks in a given class
     *
     * @param clazz class to inspect
     * @return list containing hooks found the class
     */
    public static List<Map.Entry<String, RunnableGitHook>> findHooksFromClass(Class<?> clazz) {

        List<Map.Entry<String, RunnableGitHook>> foundHooks = new LinkedList<>();

        List<Method> annotatedMethods =
                Arrays.stream(clazz.getMethods())
                        .filter(m -> m.isAnnotationPresent(GitHook.class))
                        .toList();
        List<Method> emptyParamsStaticMethods =
                annotatedMethods.stream()
                        .filter(
                                m ->
                                        m.getParameterCount() == 0
                                                && Modifier.isStatic(m.getModifiers()))
                        .toList();
        List<Method> argsPramsStaticMethods =
                annotatedMethods.stream()
                        .filter(
                                m ->
                                        Arrays.equals(
                                                        m.getParameterTypes(),
                                                        new Class[] {String[].class})
                                                && Modifier.isStatic(m.getModifiers()))
                        .toList();
        List<Method> fullParamsStaticMethods =
                annotatedMethods.stream()
                        .filter(
                                m ->
                                        Arrays.equals(
                                                        m.getParameterTypes(),
                                                        new Class[] {
                                                            HookContext.class, String[].class
                                                        })
                                                && Modifier.isStatic(m.getModifiers()))
                        .toList();

        // Find static methods
        foundHooks.addAll(
                emptyParamsStaticMethods.stream()
                        .flatMap(
                                (m) ->
                                        Stream.of(
                                                        clazz.getName() + "." + m.getName(),
                                                        clazz.getSimpleName() + "." + m.getName(),
                                                        m.getAnnotation(GitHook.class).name())
                                                .map(
                                                        n ->
                                                                Map.entry(
                                                                        n,
                                                                        ((RunnableGitHook)
                                                                                (context, args) ->
                                                                                        m.invoke(
                                                                                                null)))))
                        .toList());

        foundHooks.addAll(
                argsPramsStaticMethods.stream()
                        .flatMap(
                                m ->
                                        Stream.of(
                                                        clazz.getName() + "." + m.getName(),
                                                        clazz.getSimpleName() + "." + m.getName(),
                                                        m.getAnnotation(GitHook.class).name())
                                                .map(
                                                        n ->
                                                                Map.entry(
                                                                        n,
                                                                        ((RunnableGitHook)
                                                                                (context, args) ->
                                                                                        m.invoke(
                                                                                                null,
                                                                                                (Object)
                                                                                                        args)))))
                        .toList());

        foundHooks.addAll(
                fullParamsStaticMethods.stream()
                        .flatMap(
                                m ->
                                        Stream.of(
                                                        clazz.getName() + "." + m.getName(),
                                                        clazz.getSimpleName() + "." + m.getName(),
                                                        m.getAnnotation(GitHook.class).name())
                                                .map(
                                                        n ->
                                                                Map.entry(
                                                                        n,
                                                                        ((RunnableGitHook)
                                                                                (context, args) ->
                                                                                        m.invoke(
                                                                                                null,
                                                                                                context,
                                                                                                args)))))
                        .toList());

        // Find non-static methods
        Optional<Constructor<?>> emptyConstructor =
                Arrays.stream(clazz.getConstructors())
                        .filter(c -> c.getParameterCount() == 0)
                        .findFirst();
        if (emptyConstructor.isPresent()) {
            if (RunnableGitHook.class.isAssignableFrom(clazz)) {
                foundHooks.addAll(
                        Stream.of(
                                        clazz.getName(),
                                        clazz.getSimpleName(),
                                        Optional.ofNullable(clazz.getAnnotation(GitHook.class))
                                                .map(GitHook::name)
                                                .orElse(null))
                                .filter(Objects::nonNull)
                                .map(
                                        n ->
                                                Map.entry(
                                                        n,
                                                        ((RunnableGitHook)
                                                                (context, args) ->
                                                                        ((RunnableGitHook)
                                                                                        emptyConstructor
                                                                                                .get()
                                                                                                .newInstance())
                                                                                .run(
                                                                                        context,
                                                                                        args))))
                                .toList());
            }

            List<Method> emptyParamsMethods =
                    annotatedMethods.stream()
                            .filter(
                                    m ->
                                            m.getParameterCount() == 0
                                                    && !Modifier.isStatic(m.getModifiers()))
                            .toList();
            List<Method> argsPramsMethods =
                    annotatedMethods.stream()
                            .filter(
                                    m ->
                                            Arrays.equals(
                                                            m.getParameterTypes(),
                                                            new Class[] {String[].class})
                                                    && !Modifier.isStatic(m.getModifiers()))
                            .toList();
            List<Method> fullParamsMethods =
                    annotatedMethods.stream()
                            .filter(
                                    m ->
                                            Arrays.equals(
                                                            m.getParameterTypes(),
                                                            new Class[] {
                                                                HookContext.class, String[].class
                                                            })
                                                    && !Modifier.isStatic(m.getModifiers()))
                            .toList();

            foundHooks.addAll(
                    emptyParamsMethods.stream()
                            .flatMap(
                                    (m) ->
                                            Stream.of(
                                                            clazz.getName() + "." + m.getName(),
                                                            clazz.getSimpleName()
                                                                    + "."
                                                                    + m.getName(),
                                                            m.getAnnotation(GitHook.class).name())
                                                    .map(
                                                            n ->
                                                                    Map.entry(
                                                                            n,
                                                                            ((RunnableGitHook)
                                                                                    (context,
                                                                                            args) ->
                                                                                            m
                                                                                                    .invoke(
                                                                                                            emptyConstructor
                                                                                                                    .get()
                                                                                                                    .newInstance())))))
                            .toList());

            foundHooks.addAll(
                    argsPramsMethods.stream()
                            .flatMap(
                                    m ->
                                            Stream.of(
                                                            clazz.getName() + "." + m.getName(),
                                                            clazz.getSimpleName()
                                                                    + "."
                                                                    + m.getName(),
                                                            m.getAnnotation(GitHook.class).name())
                                                    .map(
                                                            n ->
                                                                    Map.entry(
                                                                            n,
                                                                            ((RunnableGitHook)
                                                                                    (context,
                                                                                            args) ->
                                                                                            m
                                                                                                    .invoke(
                                                                                                            emptyConstructor
                                                                                                                    .get()
                                                                                                                    .newInstance(),
                                                                                                            (Object)
                                                                                                                    args)))))
                            .toList());

            foundHooks.addAll(
                    fullParamsMethods.stream()
                            .flatMap(
                                    m ->
                                            Stream.of(
                                                            clazz.getName() + "." + m.getName(),
                                                            clazz.getSimpleName()
                                                                    + "."
                                                                    + m.getName(),
                                                            m.getAnnotation(GitHook.class).name())
                                                    .map(
                                                            n ->
                                                                    Map.entry(
                                                                            n,
                                                                            ((RunnableGitHook)
                                                                                    (context,
                                                                                            args) ->
                                                                                            m
                                                                                                    .invoke(
                                                                                                            emptyConstructor
                                                                                                                    .get()
                                                                                                                    .newInstance(),
                                                                                                            context,
                                                                                                            args)))))
                            .toList());
        }

        return foundHooks;
    }

    public void findAndRegisterHooksFromClass(String className) {
        try {
            findAndRegisterHooksFromClass(Class.forName(className));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Could not find class" + className, e);
        }
    }

    /**
     * Discover and register a new Class
     *
     * @param clazz the class to discover
     */
    public void findAndRegisterHooksFromClass(Class<?> clazz) {
        findHooksFromClass(clazz).forEach(r -> discoveredHooks.put(r.getKey(), r.getValue()));
    }

    /**
     * Discover all classes in the CP and try to register each of them. TODO: A lot of things to
     * improve here. - Lookup of classes and annotations takes too much time. - Some weird stuff
     * happens when loading some classes. - But quite powerfull and permissive. - Try to load given
     * a package or file (maven dependencies) ?
     */
    public void findAndRegisterAllHooks() throws IOException {

        ClassPath cp = ClassPath.from(Thread.currentThread().getContextClassLoader());
        ImmutableSet<ClassPath.ClassInfo> allClasses = cp.getTopLevelClasses();

        for (ClassPath.ClassInfo allClass : allClasses) {

            if (allClass.getSimpleName().equals("module-info")) {
                continue;
            }

            try {
                Class<?> clasz = allClass.load();
                findAndRegisterHooksFromClass(clasz);
            } catch (NoClassDefFoundError | IncompatibleClassChangeError e) {
                // Ignore unloadable classes
            }
        }
    }
}
