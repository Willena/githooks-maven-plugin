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
import org.apache.maven.plugins.annotations.Parameter;

public class ConditionalRunConfig {
    @Parameter(name = "ref")
    private String ref;

    public String getRef() {
        return ref;
    }

    public ConditionalRunConfig setRef(String ref) {
        this.ref = ref;
        return this;
    }

    @Override
    public String toString() {
        return "ConditionalRun{" + "ref='" + ref + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ConditionalRunConfig that = (ConditionalRunConfig) o;
        return Objects.equals(ref, that.ref);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ref);
    }
}
