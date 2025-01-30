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

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Pattern;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.StoredConfig;
import org.junit.jupiter.api.Test;

class GitUtilsTest {

    private static final Path TARGET = Path.of("target");

    @Test
    void isValidGitRepository_invalidRepo() throws IOException {
        Path newRepo = Files.createTempDirectory(TARGET, "");
        assertFalse(GitUtils.isValidGitRepository(newRepo));
    }

    @Test
    void isValidGitRepository_validRepo() throws IOException, GitAPIException {
        Path newRepo = Files.createTempDirectory(TARGET, "");
        try (Git git = Git.init().setDirectory(newRepo.toFile()).call()) {
            assertTrue(GitUtils.isValidGitRepository(newRepo));
        }
    }

    @Test
    void writeGitConfig() throws IOException, GitAPIException {
        Path newRepo = Files.createTempDirectory(TARGET, "");
        try (Git ignored = Git.init().setDirectory(newRepo.toFile()).call()) {
            GitUtils.writeGitConfig(newRepo, Map.ofEntries(Map.entry("demo.key", "value")));
        }

        try (Git git = Git.open(newRepo.toFile())) {
            assertEquals("value", git.getRepository().getConfig().getString("demo", null, "key"));
        }
    }

    @Test
    void getHooksPath() throws IOException, GitAPIException {
        Path newRepo = Files.createTempDirectory(TARGET, "");
        try (Git ignored = Git.init().setDirectory(newRepo.toFile()).call()) {
            assertEquals(
                    newRepo.resolve(".git").resolve("hooks").toAbsolutePath(),
                    GitUtils.getHooksPath(newRepo));
        }

        try (Git git = Git.open(newRepo.toFile())) {
            StoredConfig cfg = git.getRepository().getConfig();
            cfg.setString(
                    ConfigConstants.CONFIG_CORE_SECTION,
                    null,
                    ConfigConstants.CONFIG_KEY_HOOKS_PATH,
                    "something");
            cfg.save();
            String hooksDir =
                    cfg.getString(
                            ConfigConstants.CONFIG_CORE_SECTION,
                            null,
                            ConfigConstants.CONFIG_KEY_HOOKS_PATH);
        }

        try (Git git = Git.open(newRepo.toFile())) {
            assertEquals(Path.of("something"), GitUtils.getHooksPath(newRepo));
        }
    }

    @Test
    void getRefBranchMaster() throws IOException, GitAPIException {
        Path newRepo = Files.createTempDirectory(TARGET, "");
        try (Git git = Git.init().setDirectory(newRepo.toFile()).call()) {
            Path p = Files.createTempFile(newRepo, "", "");
            git.add().addFilepattern("*").call();
            git.commit().setMessage("msg").call();
            git.checkout().setCreateBranch(true).setName("newBranch").call();
        }

        assertTrue(
                GitUtils.headMatchesRefPattern(newRepo, Pattern.compile("refs/heads/newBranch")));
        assertTrue(GitUtils.headMatchesRefPattern(newRepo, Pattern.compile("refs/heads/master")));
        assertTrue(GitUtils.headMatchesRefPattern(newRepo, Pattern.compile("newBranch")));
        assertTrue(GitUtils.headMatchesRefPattern(newRepo, Pattern.compile("refs/heads/.*")));
        assertFalse(GitUtils.headMatchesRefPattern(newRepo, Pattern.compile("toto")));
    }

    @Test
    void getRefTag() throws GitAPIException, IOException {
        Path newRepo = Files.createTempDirectory(TARGET, "");
        try (Git git = Git.init().setDirectory(newRepo.toFile()).call()) {
            Path p = Files.createTempFile(newRepo, "", "");
            git.add().addFilepattern("*").call();
            git.commit().setMessage("msg").call();
            git.tag().setName("1.0.0").call();
        }

        assertTrue(GitUtils.headMatchesRefPattern(newRepo, Pattern.compile("refs/tags/1\\.0\\.0")));
        assertTrue(GitUtils.headMatchesRefPattern(newRepo, Pattern.compile("refs/tags/1.0.0")));
        assertTrue(GitUtils.headMatchesRefPattern(newRepo, Pattern.compile("refs/tags/1.*")));
        assertTrue(GitUtils.headMatchesRefPattern(newRepo, Pattern.compile("1.0.0")));
        assertTrue(GitUtils.headMatchesRefPattern(newRepo, Pattern.compile("1\\.0\\.0")));
        assertTrue(GitUtils.headMatchesRefPattern(newRepo, Pattern.compile("refs/heads/master")));
        assertTrue(GitUtils.headMatchesRefPattern(newRepo, Pattern.compile("master")));
        assertFalse(
                GitUtils.headMatchesRefPattern(newRepo, Pattern.compile("refs/heads/newBranch")));
        assertFalse(GitUtils.headMatchesRefPattern(newRepo, Pattern.compile("fss")));
    }
}
