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

package io.github.willena.maven.plugins.githooks.example;

import io.github.willena.maven.plugins.githooks.HookContext;
import io.github.willena.maven.plugins.githooks.RunnableGitHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Singleton;

@Named("example")
@Singleton
public class ExampleHook implements RunnableGitHook {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleHook.class);

    @Override
    public void run(HookContext context, String[] args) {
        LOGGER.debug("Hello args = {}", (Object[]) args);
    }
}
