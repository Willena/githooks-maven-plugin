package io.github.willena.maven.plugins.githooks;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public final class GitUtils {
    public static boolean isValidGitRepository(Path repositoryPath) {
        File gitFile = getRepositoryPath(repositoryPath.toFile());
        if (gitFile == null) {
            return false;
        }

        try (Git git = Git.open(gitFile)) {
            git.status().call();
        } catch (GitAPIException | IOException e) {
            return false;
        }
        return true;
    }

    public static void writeGitConfig(Path repositoryPath, Map<String, String> customConfig) {
        try (Git git = Git.open(getRepositoryPath(repositoryPath.toFile()))) {
            StoredConfig config = git.getRepository().getConfig();

            Optional.ofNullable(customConfig)
                    .orElse(Collections.emptyMap())
                    .entrySet()
                    .stream()
                    .map(e -> Map.entry(GitConfigKey.parse(e.getKey()), e.getValue()))
                    .forEach(configEntry -> {
                        GitConfigKey configKey = configEntry.getKey();
                        config.setString(configKey.getSection(), configKey.getSubSection(), configKey.getName(), configEntry.getValue());
                    });

            config.save();

        } catch (IOException e) {
            throw new IllegalStateException("Could not update git configuration", e);
        }
    }

    private static File getRepositoryPath(File projectDir) {
        final FileRepositoryBuilder repoBuilder = new FileRepositoryBuilder();
        repoBuilder.findGitDir(projectDir);
        return repoBuilder.getGitDir();
    }

    public static Path getHooksPath(Path repositoryPath) throws IOException {
        Path p = findHooksPath(repositoryPath);
        return Files.createDirectories(p);
    }

    private static Path findHooksPath(Path repositoryPath) {
        try (Git git = Git.open(getRepositoryPath(repositoryPath.toFile()))) {
            Config config = git.getRepository().getConfig();

            String hooksDir = config.getString(ConfigConstants.CONFIG_CORE_SECTION,
                    null, ConfigConstants.CONFIG_KEY_HOOKS_PATH);
            if (hooksDir != null) {
                return Path.of(hooksDir);
            }
            File dir = git.getRepository().getCommonDirectory();
            if (dir == null) {
                throw new IllegalStateException("Could not determine git hooks path");
            }
            return dir.toPath().resolve(Constants.HOOKS);
        } catch (IOException e) {
            throw new IllegalStateException("Could not update git configuration", e);
        }
    }

}
