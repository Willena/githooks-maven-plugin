package io.github.willena.maven.plugins.githooks;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.StoredConfig;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
            GitUtils.writeGitConfig(newRepo, Map.ofEntries(
                    Map.entry("demo.key", "value")
            ));
        }

        try (Git git = Git.open(newRepo.toFile())) {
            assertEquals("value", git.getRepository().getConfig().getString("demo", null, "key"));
        }
    }

    @Test
    void getHooksPath() throws IOException, GitAPIException {
        Path newRepo = Files.createTempDirectory(TARGET, "");
        try (Git ignored = Git.init().setDirectory(newRepo.toFile()).call()) {
            assertEquals(newRepo.resolve(".git\\hooks").toAbsolutePath(), GitUtils.getHooksPath(newRepo));
        }

        try (Git git = Git.open(newRepo.toFile())) {
            StoredConfig cfg = git.getRepository().getConfig();
            cfg.setString(ConfigConstants.CONFIG_CORE_SECTION, null, ConfigConstants.CONFIG_KEY_HOOKS_PATH, "something");
            cfg.save();
            String hooksDir = cfg.getString(ConfigConstants.CONFIG_CORE_SECTION,
                    null, ConfigConstants.CONFIG_KEY_HOOKS_PATH);

        }

        try (Git git = Git.open(newRepo.toFile())) {
            assertEquals(Path.of("something"), GitUtils.getHooksPath(newRepo));
        }
    }
}