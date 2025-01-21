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

import java.util.Objects;
import java.util.regex.Pattern;

public class GitConfigKey {

    private static final int REQUIRED_SECTIONS = 3;
    private static final Pattern SPLITTER = Pattern.compile("\\.");

    private final String section;
    private final String subSection;
    private final String name;

    public GitConfigKey(String section, String subSection, String name) {
        this.section = section;
        this.subSection = subSection;
        this.name = name;
    }

    public String getSection() {
        return section;
    }

    public String getSubSection() {
        return subSection;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GitConfigKey that = (GitConfigKey) o;
        return Objects.equals(section, that.section)
                && Objects.equals(subSection, that.subSection)
                && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(section, subSection, name);
    }

    public static GitConfigKey parse(String configKey) {
        final String[] sections = SPLITTER.split(configKey);

        if (sections.length > REQUIRED_SECTIONS || sections.length < 2) {
            throw new IllegalArgumentException(
                    "Git configuration key '"
                            + configKey
                            + "' must include 1-2 sections separated by dots. ");
        }
        return new GitConfigKey(
                sections[0],
                sections.length == REQUIRED_SECTIONS ? sections[1] : null,
                sections[sections.length - 1]);
    }
}
