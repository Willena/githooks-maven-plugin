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

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum HookType {

    /** Pre commit hook. */
    PRE_COMMIT("pre-commit"),

    /** Pre merge commit hook. */
    PRE_MERGE_COMMIT("pre-merge-commit"),

    /** Pre push hook. */
    PRE_PUSH("pre-push"),

    /** Pre rebase hook. */
    PRE_REBASE("pre-rebase"),

    /** Commit message hook. */
    COMMIT_MSG("commit-msg"),

    /** Prepare commit message. */
    PREPARE_COMMIT_MSG("prepare-commit-msg"),

    /** The update hook. */
    UPDATE("update"),

    /** The post update hook. */
    POST_UPDATE("post-update"),

    /** The apply patch message hook. */
    APPLYPATCH_MSG("applypatch-msg"),

    /** The pre apply patch hook. */
    PRE_APPLYPATCH("pre-applypatch"),

    /** The post merge hook. */
    POST_MERGE("post-merge"),

    /** Post apply patch hook. */
    POST_APPLYPATCH("post-applypatch"),

    /** Post commit hook. */
    POST_COMMIT("post-commit"),

    /** Post checkout hook. */
    POST_CHECKOUT("post-checkout"),

    /** Pre receive hook. */
    PRE_RECEIVE("pre-receive"),

    /** Proc receive hook. */
    PROC_RECEIVE("proc-receive"),

    /** Post receive hook. */
    POST_RECEIVE("post-receive"),

    /** Reference transaction hook. */
    REFERENCE_TRANSACTION("reference-transaction"),

    /** Push to checkout hook. */
    PUSH_TO_CHECKOUT("push-to-checkout"),

    /** Pre auto gc hook. */
    PRE_AUTO_GC("pre-auto-gc"),

    /** Post rewrite hook. */
    POST_REWRITE("post-rewrite"),

    /** Send email validate hook. */
    SENDEMAIL_VALIDATE("sendemail-validate"),

    /** FS monitor watchman hook. */
    FSMONITOR_WATCHMAN("fsmonitor-watchman"),

    /** P4 changelist hook. */
    P4_CHANGELIST("p4-changelist"),

    /** P4 prepare changelist hook. */
    P4_PREPARE_CHANGELIST("p4-prepare-changelist"),

    /** P4 post changelist hook. */
    P4_POST_CHANGELIST("p4-post-changelist"),

    /** P4 pre submit hook. */
    P4_PRE_SUBMIT("p4-pre-submit"),

    /** Post index change hook. */
    POST_INDEX_CHANGE("post-index-change");

    /** The pre-specified name of the file of this hook type. */
    private final String filename;

    /**
     * Constructor.
     *
     * @param filename the filename of the file for the hook of this type.
     */
    HookType(final String filename) {
        this.filename = filename;
    }

    /**
     * Gets the pre-specified name of the file for hooks of this type.
     *
     * @return the hook file name.
     */
    public String getFileName() {
        return filename;
    }

    public static final Set<String> ALL_HOOKS_FILENAMES =
            Arrays.stream(HookType.values()).map(HookType::getFileName).collect(Collectors.toSet());
}
