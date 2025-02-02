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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GitHook(name = "class-name")
public class AllPossibleHookFound implements RunnableGitHook {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllPossibleHookFound.class);

    @GitHook(name = "run-default")
    @Override
    public void run(HookContext context, String[] args) throws Exception {
        LOGGER.info("Call run with {}, {}", context, args);
    }

    @GitHook(name = "static-run-no-args")
    public static void staticRunNoArgs() {
        LOGGER.info("Call staticRunNoArgs");
    }

    @GitHook(name = "static-run-string-args")
    public static void staticRunStringArgs(String[] args) {
        LOGGER.info("Call staticRunStringArgs with {}", (Object) args);
    }

    @GitHook(name = "static-run-all-args")
    public static void staticRunAllArgs(HookContext context, String[] args) {
        LOGGER.info("Call staticRunAllArgs with {}, {}", context, args);
    }

    @GitHook(name = "run-no-args")
    public void runNoArgs() {
        LOGGER.info("Call runNoArgs");
    }

    @GitHook(name = "run-string-args")
    public void runStringArgs(String[] args) {
        LOGGER.info("Call runStringArgs with {}", (Object) args);
    }

    @GitHook(name = "run-all-args")
    public void runAllArgs(HookContext context, String[] args) {
        LOGGER.info("Call runAllArgs with {}, {}", context, args);
    }
}
