---
pageName: "${title}"
pageTitle: "${title} | ${globals.name}"
pageDescription: "${summary}"
listable: true
#
title: "Gradle plugins"
kind: "Plugins library"
summary: "Collection of custom Gradle plugins for embedded firmware development, platform development and build automation in my Gradle monorepo projects."
tech:
  - "Kotlin"
techDetails:
  - "Java 21"
  - "Kotlin DSL"
status: "maintained"
pinned: true
weight: 0
repoUrls:
  - url: "https://github.com/c0ifl4x/gradle-plugins"
downloadUrls:
  - name: "ESP-IDF plugin (JAR)"
    url: "https://m2.miloszgilga.pl/#/releases/pl/miloszgilga/gradle/plugin-esp-idf"
  - name: "WebEmbed plugin (JAR)"
    url: "https://m2.miloszgilga.pl/#/releases/pl/miloszgilga/gradle/plugin-web-embed"
docsUrls:
  - name: "ESP-IDF plugin (Javadoc)"
    url: "https://m2.miloszgilga.pl/javadoc/releases/pl/miloszgilga/gradle/plugin-esp-idf/latest"
  - name: "WebEmbed plugin (Javadoc)"
    url: "https://m2.miloszgilga.pl/javadoc/releases/pl/miloszgilga/gradle/plugin-web-embed/latest"
---

## Available plugins

| Module             | Plugin ID                                                                     | Description                                                                          | Docs                                                                                                | Repository                                                                                |
|:-------------------|:------------------------------------------------------------------------------|:-------------------------------------------------------------------------------------|:----------------------------------------------------------------------------------------------------|:------------------------------------------------------------------------------------------|
| `plugin-esp-idf`   | `pl.miloszgilga.gradle.esp-idf`                                               | Cross-platform Gradle wrapper for ESP-IDF firmware projects (Windows, Linux, macOS). | [Javadoc](https://m2.miloszgilga.pl/javadoc/releases/pl/miloszgilga/gradle/plugin-esp-idf/latest)   | [Reposilite](https://m2.miloszgilga.pl/#/releases/pl/miloszgilga/gradle/plugin-esp-idf)   |
| `plugin-web-embed` | `pl.miloszgilga.gradle.frontend-builder`<br>`pl.miloszgilga.gradle.web-embed` | Builds frontend assets via Node/Yarn and embeds them into JVM server resources.      | [Javadoc](https://m2.miloszgilga.pl/javadoc/releases/pl/miloszgilga/gradle/plugin-web-embed/latest) | [Reposilite](https://m2.miloszgilga.pl/#/releases/pl/miloszgilga/gradle/plugin-web-embed) |

## Usage

### Add plugins repository

```kotlin
pluginManagement {
  repositories {
    maven {
      name = "ReposiliteReleases"
      url = uri("https://m2.miloszgilga.pl/releases")
    }
    // optional: required only if using snapshot versions
    maven {
      name = "ReposiliteSnapshots"
      url = uri("https://m2.miloszgilga.pl/snapshots")
    }
    gradlePluginPortal()
  }
}
```

### Usage of `plugin-esp-idf`

> [!NOTE]
> To use this plugin, you must still have the **ESP-IDF** framework installed and configured on your system. This plugin
> serves as a convenient cross-platform wrapper for monorepo projects using Gradle Kotlin DSL.

#### Configuration

Apply the plugin and customize the `espIdf` extension in your firmware project's `build.gradle.kts`:

```kotlin
plugins {
  id("pl.miloszgilga.gradle.esp-idf") version "X.Y.Z"
}

espIdf {
  chip.set("esp32c3") // default: "esp32"
  port.set("/dev/ttyACM0") // default: OS-specific serial port (COM3, /dev/ttyACM0, /dev/cu.usbmodem101)
  baud.set("921600") // default: "921600"
  appVersion.set("1.0.0") // default: project.version (or "0.0.1-local-dev" if unspecified)
}
```

#### Available tasks (`firmware` group)

| Task              | Description                                                                      |
|:------------------|:---------------------------------------------------------------------------------|
| `checkEnv`        | Verifies that the ESP-IDF environment and `idf.py` are properly configured.      |
| `setChip`         | Sets the compilation target chip (e.g., `esp32c3`, `esp32s3`, `esp32`).          |
| `buildFirmware`   | Builds the full firmware (bootloader, partition table, and application).         |
| `buildAppOnly`    | Builds only the application binary (skips bootloader and partition images).      |
| `rebuildFirmware` | Forces CMake reconfiguration and performs a full firmware rebuild.               |
| `flashFirmware`   | Builds and flashes the full firmware to the board.                               |
| `flashAppOnly`    | Builds and flashes only the application partition to the board.                  |
| `monitorFirmware` | Opens the serial monitor with address-to-line backtrace decoding.                |
| `runFirmware`     | Builds, flashes the full firmware, and immediately starts the serial monitor.    |
| `eraseFlash`      | Erases the entire flash memory on the chip (including NVS and other partitions). |
| `cleanFirmware`   | Cleans build output files (`idf.py fullclean`).                                  |
| `generateDocs`    | Generates project documentation using Doxygen.                                   |

### Usage of `plugin-web-embed`

This module provides two complementary plugins designed for monorepo setups where a JVM backend embeds a compiled
Node/Yarn frontend application (such as Svelte, React, or Vite):

- `pl.miloszgilga.gradle.frontend-builder` - manages Node.js/Yarn toolchains, runs dev/build scripts, and exposes the
  compiled output directory via the `webResources` Gradle configuration,
- `pl.miloszgilga.gradle.web-embed` - resolves the `webResources` artifact from a frontend module and packages it into
  the JVM classpath resources during `processResources`.

> [!TIP]
> Because `web-embed` hooks directly into Gradle's standard `processResources` task, packaging plugins such as
> `shadowJar` or `jib` (`jibDockerBuild`) automatically trigger the frontend build (`yarnInstall` -> `buildWeb`) and
> bundle the compiled assets into the target classpath directory without requiring manual task dependencies. Thanks to
> Gradle's incremental build caching, modifying only backend code skips rebuilding the frontend (`UP-TO-DATE`).

#### Project structure

By default, `web-embed` automatically detects and wires a sibling `:ui` subproject under the same parent module:

```text
monorepo-root/
├── my-service/
│   ├── server/   # applies pl.miloszgilga.gradle.web-embed
│   └── ui/       # applies pl.miloszgilga.gradle.frontend-builder
└── settings.gradle.kts
```

#### Frontend configuration

Apply `pl.miloszgilga.gradle.frontend-builder` and optionally customize the `frontendBuilder` extension:

```kotlin
plugins {
  id("pl.miloszgilga.gradle.frontend-builder") version "X.Y.Z"
}

frontendBuilder {
  nodeVersion.set("26.13.1") // default: "26.13.1"
  yarnVersion.set("1.22.22") // default: "1.22.22"
  downloadNode.set(true) // default: true
  buildDirName.set("dist") // default: "dist" (for standard Vite setups)
  devScript.set("dev") // default: "dev"
  buildScript.set("build") // default: "build"
  inputFiles.set(listOf("package.json", "tsconfig.json", "vite.config.ts", "yarn.lock"))
  additionalInputs.from("public") // optional extra files or directories tracked by buildWeb
  additionalDeletes.from(".vite") // optional extra files or directories removed during clean
}
```

#### Backend configuration

Apply `pl.miloszgilga.gradle.web-embed` alongside any JVM plugin (`java`, `application`, `org.jetbrains.kotlin.jvm`,
etc.). If a sibling `:ui` project exists, zero configuration is required:

```kotlin
plugins {
  application
  id("pl.miloszgilga.gradle.web-embed") version "X.Y.Z"
}

// Optional customization:
webEmbed {
  uiProject.set(":my-service:ui") // optional: overrides automatic sibling :ui resolution
  targetDir.set("webapp") // default: "webapp" (maps to src/main/resources/webapp)
  autoWireSiblingUi.set(true) // default: true
  skip.set(false) // default: false (can also be toggled via -PskipWebEmbed)
}
```

#### Skipping frontend build

During local development when running the backend and UI dev server separately (`yarnDev`), you can skip building and
bundling frontend assets by passing the `-PskipWebEmbed` project property:

```bash
./gradlew :my-service:server:run -PskipWebEmbed
```

When active, `uiResources` is excluded from the task execution graph, so `yarnInstall` and `buildWeb` are not triggered.

#### Available tasks

| Task          | Plugin             | Group         | Description                                                            |
|:--------------|:-------------------|:--------------|:-----------------------------------------------------------------------|
| `yarnInstall` | `frontend-builder` | `node`        | Installs frontend dependencies via Yarn.                               |
| `yarnDev`     | `frontend-builder` | `application` | Starts the local frontend development server (`yarn run dev`).         |
| `buildWeb`    | `frontend-builder` | `build`       | Compiles the production frontend bundle and publishes `webResources`.  |
| `clean`       | `frontend-builder` | `build`       | Removes the generated build directory and `additionalDeletes` entries. |
