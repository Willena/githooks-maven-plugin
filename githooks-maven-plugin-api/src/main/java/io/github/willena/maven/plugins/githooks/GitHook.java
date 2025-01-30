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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to discover Hooks.
 * Hooks libraries can be used in the "class" RunConfig.
 * To avoid using a FQDN class name or if the name of the class is not beautiful
 * enough you can specify it using the annotation
 * <p>
 * If attached on constructor or class, it expects an empty constructor
 * If attached on a static method with only string array as parameter (main method or equivalent) it will run the method
 * If attached on a class implementing {@link RunnableGitHook} it will try the {@link RunnableGitHook#run(HookContext, String[])} method
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface GitHook {
    String description() default "";

    String name() default "";
}
