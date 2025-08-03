# Git Hooks Maven Plugin

A maven plugin to manage, install and run git hooks in your project.

The main goal is to keep everything into a maven/java world without requiring external software.

This plugin was inspired by other software :

- https://github.com/rudikershaw/git-build-hook (maven)
- https://typicode.github.io/husky/ (node)
- https://github.com/pre-commit/pre-commit (python)
- https://github.com/evilmartians/lefthook (go)

## Setup

Add the following plugin definition to your maven project

```xml

<plugin>
    <groupId>io.github.willena.maven</groupId>
    <artifactId>githooks-maven-plugin</artifactId>
    <version>${plugin-version}</version>
</plugin>
```

Hook libraries can be provided and used if defined as plugin dependencies.
For an example, see [Git hook collection](https://github.com/Willena/git-hooks-collection).

## Goals descriptions

### Install

The `install` goal setup configuration, installs and link hook in the git project.
Please note that it will overwrite existing hooks.
The installation of hooks mostly consists of putting correctly named files in the hook directory.
The githook plugin install a small script that will trigger the `run` goal of the plugin.

This goal must be tied to an early phase in the maven lifecycle such as `validate` to ensure hooks are installed from
the start.

```xml

<executions>
    <execution>
        <id>install-hooks</id>
        <phase>validate</phase>
        <goals>
            <goal>install</goal>
        </goals>
    </execution>
</executions>
```

#### Goal configuration

| Key                    | Property     | Type                  | Description                                                                                                                                      | Default                                                                                                                                                                                                                                                                                                                       |
|------------------------|--------------|-----------------------|--------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `<debug>`              | `hook.debug` | `boolean`             | Enable debug mode. This will print more messages and display more information when running hook scripts.                                         | false                                                                                                                                                                                                                                                                                                                         |
| `<skip>`               | `hook.skip`  | `boolean`             | Skip the execution                                                                                                                               | false                                                                                                                                                                                                                                                                                                                         |
| `<gitConfig>`          |              | `Map<String, String>` | Map of configuration key that will be applied to the git repository. Can be useful to set the hook directory, or enforce some git configuration. | {}                                                                                                                                                                                                                                                                                                                            |
| `<hooks>`              |              | `List<HookConfig>`    | Definition of hooks that will be installed                                                                                                       | []                                                                                                                                                                                                                                                                                                                            |
| `<hookScriptTemplate>` |              | `String`              | Velocity based template that will be used as the git hook script initializer;                                                                    | `args=$(IFS=, ; echo "$*"); export PATH="${javaBin}:${mavenBin}:$PATH"; export JAVA_HOME="${javaHome}"; export MAVEN_HOME="${mavenHome}"; alias type='type -p'; shPath=$($(command -v where \|\| command -v type) sh); unalias type; mvn githooks:run "-Dsh.path=${shPath}" "-Dhook.name=${hookName}" "-Dhook.args=${args}";` |

### Run

The `run` goal is intended to be called from the command line. It will trigger the execution of one or many hooks
depending on the configuration.
Scripts installed during the `install` goal directly call to this goal.

#### Goal configuration

| Key          | Property        | Type               | Description                                                                                                                   | Default   |
|--------------|-----------------|--------------------|-------------------------------------------------------------------------------------------------------------------------------|-----------|
| `<hook>`     | `hook.name`     | `String`           | Required. Git hook name to run for example `COMMIT_MSG`.                                                                      |           |
| `<skip>`     | `hook.skip`     | `boolean`          | Skip the execution                                                                                                            | false     |
| `<skipRuns>` | `hook.skipRuns` | `List<String>`     | List of run names to skip                                                                                                     | false     |
| `<args>`     | `hook.args`     | `List<String>`     | List of arguments that git is providing                                                                                       | {}        |
| `<hooks>`    |                 | `List<HookConfig>` | Required. Definition of hooks that will be installed                                                                          | []        |
|              | `sh.path`       | `String`           | This property is auto defined by the git hook script (via the default template). The value is adapted automatically to the OS | `/bin/sh` |

## Configuration details

### `<gitConfig>`

Map of configuration key that will be applied to the git repository.
Can be useful to set the hook directory, or enforce some git configuration.

Ex:

```xml

<gitConfig>
    <core.autocrlf>true</core.autocrlf>
</gitConfig>
```

### `<hooks>`

List of hooks definition. For each hook type from git, you can define multiple hook runs.
Example:

```xml

<hooks>
    <hook>
        ...
    </hook>
    <hook>
        ...
    </hook>
</hooks>
```

#### `<hook>`

Each `<hook>` has the following properties

| Key                 | Type                         | Description                                                |
|---------------------|------------------------------|------------------------------------------------------------|
| `<type>`            | `String`                     | Git hook name, as defined by git; For example `COMMIT_MSG` |
| `<hookDefinitions>` | `List<HookDefinitionConfig>` | List of runs for specified git hook                        |

#### `<hookDefinitions>`

Each `<hookDefinitions>` can have the following properties

| Key             | Type                   | Description                                                | Default |
|-----------------|------------------------|------------------------------------------------------------|---------|
| `<enabled>`     | `boolean`              | Enable or disable the run                                  | true    |
| `<description>` | `String`               | A description of the run. Only for logs and documentation. |         |
| `<runConfig>`   | `RunConfig`            | Required. Configuration of the run                         |         |
| `<skipIf>`      | `ConditionalRunConfig` | Skip this run based on some conditionals                   |         |
| `<onlyIf>`      | `ConditionalRunConfig` | Run only if it meets some conditionals                     |         |

#### `<runConfig>`

Each `<runConfig>` describe what to run when triggered.
Multiple type of calls are available:

- Java class (the class must be of `RunnableGitHook` type; see the [githooks-example](githooks-example)` project)
- Maven goal
- Shell command

Each `<runConfig>` must define one and only one type of call.

Each `<runConfig>` can have the following properties

| Key           | Type           | Description                                                                                                                             |
|---------------|----------------|-----------------------------------------------------------------------------------------------------------------------------------------|
| `<className>` | `String`       | FQDN or Java hook name                                                                                                                  |
| `<command>`   | `String`       | Command to run. If the command start with `classpath:` the script will be copied from the classpath to the temp folder before execution |
| `<args>`      | `List<String>` | For class or command type, static args to be propagated. One argument equals one item in the list                                       |
| `<mojo>`      | `MojoConfig`   | A mojo execution definition                                                                                                             |

#### `MojoConfig`

A `MojoConfig` is a classical Maven mojo configuration object.

It has the following structure:

```xml

<mojo>
    <plugin>
        <groupId>org.example</groupId>
        <artifactId>plugin-name</artifactId>
        <version>1.0.0</version>
    </plugin>
    <goal>goalName</goal>
    <configuration>
        <pluginSpecificConfig>...</pluginSpecificConfig>
    </configuration>
</mojo>
```

#### `ConditionalRunConfig`

The ConditionalRunConfig structure allow to specify condition for the hookDefinition to run or not.
For now the only possible condition is based on current git reference (branch/commit/tag/...)

`ConditionalRunConfig` has the following properties

| Key     | Type      | Description                                                  |
|---------|-----------|--------------------------------------------------------------|
| `<ref>` | `Pattern` | Required. A regex pattern to match against the git reference |


## License

```
Copyright 2025 Willena (Guillaume VILLENA)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```