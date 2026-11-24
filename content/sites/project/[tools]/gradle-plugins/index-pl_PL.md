---
kind: "Wtyczki Gradle"
summary: "Kolekcja autorskich wtyczek Gradle do tworzenia oprogramowania wbudowanego, rozwoju platform oraz automatyzacji budowania w moich projektach monorepo opartych na Gradle."
downloadUrls:
  - name: "Plugin ESP-IDF (JAR)"
    url: "https://m2.miloszgilga.pl/#/releases/pl/miloszgilga/gradle/plugin-esp-idf"
  - name: "Plugin WebEmbed (JAR)"
    url: "https://m2.miloszgilga.pl/#/releases/pl/miloszgilga/gradle/plugin-web-embed"
docsUrls:
  - name: "Plugin ESP-IDF (Javadoc)"
    url: "https://m2.miloszgilga.pl/javadoc/releases/pl/miloszgilga/gradle/plugin-esp-idf/latest"
  - name: "Plugin WebEmbed (Javadoc)"
    url: "https://m2.miloszgilga.pl/javadoc/releases/pl/miloszgilga/gradle/plugin-web-embed/latest"
---

## Dostępne wtyczki

| Moduł              | ID pluginu                                                                    | Opis                                                                                     | Dokumentacja                                                                                        | Repozytorium                                                                              |
|:-------------------|:------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------|:----------------------------------------------------------------------------------------------------|:------------------------------------------------------------------------------------------|
| `plugin-esp-idf`   | `pl.miloszgilga.gradle.esp-idf`<br>`pl.miloszgilga.gradle.esp-idf-components` | Wieloplatformowa nakładka Gradle dla projektów firmware ESP-IDF (Windows, Linux, macOS). | [Javadoc](https://m2.miloszgilga.pl/javadoc/releases/pl/miloszgilga/gradle/plugin-esp-idf/latest)   | [Reposilite](https://m2.miloszgilga.pl/#/releases/pl/miloszgilga/gradle/plugin-esp-idf)   |
| `plugin-web-embed` | `pl.miloszgilga.gradle.frontend-builder`<br>`pl.miloszgilga.gradle.web-embed` | Buduje zasoby frontendowe przez Node/Yarn i osadza je w zasobach serwera JVM.            | [Javadoc](https://m2.miloszgilga.pl/javadoc/releases/pl/miloszgilga/gradle/plugin-web-embed/latest) | [Reposilite](https://m2.miloszgilga.pl/#/releases/pl/miloszgilga/gradle/plugin-web-embed) |

## Użycie

### Dodanie repozytoriów

```kotlin
pluginManagement {
  repositories {
    maven {
      name = "ReposiliteReleases"
      url = uri("https://m2.miloszgilga.pl/releases")
    }
    // opcjonalnie: wymagane tylko jeśli wykorzystywane są wersje snapshot
    maven {
      name = "ReposiliteSnapshots"
      url = uri("https://m2.miloszgilga.pl/snapshots")
    }
    gradlePluginPortal()
  }
}
```

### Użycie `plugin-esp-idf`

Moduł ten udostępnia dwie uzupełniające się wtyczki dla projektów monorepo opartych o ESP-IDF:

- `pl.miloszgilga.gradle.esp-idf` - opakowuje przepływy pracy `idf.py` i Doxygen, zarządza przyrostowym budowaniem
  (cache) oraz eksportuje skompilowany plik binarny firmware'u poprzez konfigurację `firmwareBinary`,
- `pl.miloszgilga.gradle.esp-idf-components` - udostępnia katalog podprojektu Gradle jako artefakt współdzielonych
  komponentów C/C++ poprzez konfigurację `espComponents`, automatycznie przekazując flagę `-DEXTRA_COMPONENT_DIRS` do
  CMake.

> [!NOTE]
> Aby korzystać z tej wtyczki, nadal trzeba mieć zainstalowane i skonfigurowane środowisko **ESP-IDF** w swoim systemie.
> Wtyczka służy jako wygodna, wieloplatformowa nakładka dla projektów monorepo korzystających z Gradle Kotlin DSL.

#### Struktura projektu

```text
monorepo-root/
├── firmware/
│   ├── common-components/   # aplikuje pl.miloszgilga.gradle.esp-idf-components
│   └── device-app/          # aplikuje pl.miloszgilga.gradle.esp-idf
└── settings.gradle.kts
```

#### Konfiguracja współdzielonych komponentów

W module współdzielonych komponentów (`firmware/common-components/build.gradle.kts`):

```kotlin
plugins {
  id("pl.miloszgilga.gradle.esp-idf-components") version "X.Y.Z"
}
```

#### Konfiguracja firmware'u

Wtyczkę `pl.miloszgilga.gradle.esp-idf` należy zaaplikować, dostosować rozszerzenie `espIdf` oraz podpiąć moduły
współdzielonych komponentów poprzez `espComponents` w pliku `build.gradle.kts` projektu firmware:

```kotlin
plugins {
  id("pl.miloszgilga.gradle.esp-idf") version "X.Y.Z"
}

espIdf {
  chip.set("esp32c3") // domyślnie: "esp32"
  port.set("/dev/ttyACM0") // domyślnie: port szeregowy zależny od systemu (COM3, /dev/ttyACM0, /dev/cu.usbmodem101)
  baud.set("921600") // domyślnie: "921600"
  appVersion.set("1.0.0") // domyślnie: project.version (lub "0.0.1-local-dev", jeśli nie określono)
  extraComponentDirs.from(rootProject.file("vendor/external-components")) // opcjonalnie: dodatkowe lokalne katalogi komponentów
}

dependencies {
  espComponents(project(":firmware:common-components")) // opcjonalnie: współdzielone moduły komponentów Gradle
}
```

#### Dostępne zadania (grupa `firmware`)

| Zadanie           | Opis                                                                                      |
|:------------------|:------------------------------------------------------------------------------------------|
| `checkEnv`        | Weryfikuje, czy środowisko ESP-IDF oraz `idf.py` są poprawnie skonfigurowane.             |
| `setChip`         | Ustawia docelowy układ mikrokontrolera do kompilacji (np. `esp32c3`, `esp32s3`, `esp32`). |
| `buildFirmware`   | Buduje pełny firmware (bootloader, tablicę partycji oraz aplikację).                      |
| `buildAppOnly`    | Buduje wyłącznie plik binarny aplikacji (pomija obrazy bootloadera i partycji).           |
| `rebuildFirmware` | Wymusza rekonfigurację CMake i wykonuje pełną przebudowę firmware'u.                      |
| `flashFirmware`   | Buduje i wgrywa pełny firmware na płytkę.                                                 |
| `flashAppOnly`    | Buduje i wgrywa wyłącznie partycję aplikacji na płytkę.                                   |
| `monitorFirmware` | Otwiera monitor portu szeregowego z dekodowaniem śladów stosu (backtrace).                |
| `runFirmware`     | Buduje, wgrywa pełny firmware na płytkę i natychmiast uruchamia monitor szeregowy.        |
| `eraseFlash`      | Czyści całą pamięć flash układu (w tym NVS oraz pozostałe partycje).                      |
| `cleanFirmware`   | Czyści pliki wyjściowe budowania (`idf.py fullclean`).                                    |
| `generateDocs`    | Generuje dokumentację projektu przy użyciu narzędzia Doxygen.                             |

### Użycie `plugin-web-embed`

Moduł ten dostarcza dwie uzupełniające się wtyczki zaprojektowane z myślą o projektach monorepo, w których backend JVM
osadza skompilowaną aplikację frontendową opartą o Node/Yarn (np. Svelte, React lub Vite):

- `pl.miloszgilga.gradle.frontend-builder` - zarządza środowiskiem Node.js/Yarn, uruchamia skrypty deweloperskie i
  budujące oraz udostępnia skompilowany katalog wyjściowy poprzez konfigurację Gradle `webResources`,
- `pl.miloszgilga.gradle.web-embed` - rozwiązuje artefakt `webResources` z modułu frontendowego i pakuje go do zasobów
  classpath JVM podczas wykonywania zadania `processResources`.

> [!TIP]
> Ponieważ wtyczka `web-embed` podpina się bezpośrednio pod standardowe zadanie Gradle `processResources`, wtyczki
> pakujące takie jak `shadowJar` czy `jib` (`jibDockerBuild`) automatycznie wyzwalają budowanie frontendu
> (`yarnInstall` -> `buildWeb`) i dołączają skompilowane zasoby do docelowego katalogu w classpath bez konieczności
> ręcznego definiowania zależności między zadaniami. Dzięki przyrostowemu budowaniu w Gradle (cache), zmiana wyłącznie w
> kodzie backendu pomija ponowne budowanie frontendu (`UP-TO-DATE`).

#### Struktura projektu

Domyślnie wtyczka `web-embed` automatycznie wykrywa i podpina siostrzany podprojekt `:ui` znajdujący się w tym samym
module nadrzędnym:

```text
monorepo-root/
├── my-service/
│   ├── server/   # aplikuje pl.miloszgilga.gradle.web-embed
│   └── ui/       # aplikuje pl.miloszgilga.gradle.frontend-builder
└── settings.gradle.kts
```

#### Konfiguracja frontendu

Należy zaaplikować `pl.miloszgilga.gradle.frontend-builder` oraz opcjonalnie dostosować rozszerzenie `frontendBuilder`:

```kotlin
plugins {
  id("pl.miloszgilga.gradle.frontend-builder") version "X.Y.Z"
}

frontendBuilder {
  nodeVersion.set("26.13.1") // domyślnie: "26.13.1"
  yarnVersion.set("1.22.22") // domyślnie: "1.22.22"
  downloadNode.set(true) // domyślnie: true
  buildDirName.set("dist") // domyślnie: "dist" (dla standardowej konfiguracji Vite)
  devScript.set("dev") // domyślnie: "dev"
  buildScript.set("build") // domyślnie: "build"
  inputFiles.set(listOf("package.json", "tsconfig.json", "vite.config.ts", "yarn.lock"))
  additionalInputs.from("public") // opcjonalne dodatkowe pliki lub katalogi śledzone przez buildWeb
  additionalDeletes.from(".vite") // opcjonalne dodatkowe pliki lub katalogi usuwane podczas clean
}
```

#### Konfiguracja backendu

Wtyczkę `pl.miloszgilga.gradle.web-embed` należy zaaplikować obok dowolnej wtyczki JVM (`java`, `application`,
`org.jetbrains.kotlin.jvm`, itp.). Jeśli istnieje siostrzany projekt `:ui`, żadna dodatkowa konfiguracja nie jest
wymagana:

```kotlin
plugins {
  application
  id("pl.miloszgilga.gradle.web-embed") version "X.Y.Z"
}

// Opcjonalna konfiguracja:
webEmbed {
  uiProject.set(":my-service:ui") // opcjonalnie: nadpisuje automatyczne wyszukiwanie siostrzanego modułu :ui
  targetDir.set("webapp") // domyślnie: "webapp" (mapuje do src/main/resources/webapp)
  autoWireSiblingUi.set(true) // domyślnie: true
  skip.set(false) // domyślnie: false (można też przełączyć flagą -PskipWebEmbed)
}
```

#### Pomijanie budowania frontendu

Podczas lokalnego developmentu, gdy serwer backendowy oraz serwer deweloperski UI (`yarnDev`) uruchamiane są osobno,
można pominąć budowanie i pakowanie zasobów frontendowych, przekazując właściwość projektu `-PskipWebEmbed`:

```bash
./gradlew :my-service:server:run -PskipWebEmbed
```

Gdy flaga jest aktywna, konfiguracja `uiResources` jest wykluczana z grafu wykonania zadań, dzięki czemu `yarnInstall`
oraz `buildWeb` nie są uruchamiane.

#### Dostępne zadania

| Zadanie       | Plugin             | Grupa         | Opis                                                                          |
|:--------------|:-------------------|:--------------|:------------------------------------------------------------------------------|
| `yarnInstall` | `frontend-builder` | `node`        | Instaluje zależności frontendowe za pomocą Yarn.                              |
| `yarnDev`     | `frontend-builder` | `application` | Uruchamia lokalny serwer deweloperski frontendu (`yarn run dev`).             |
| `buildWeb`    | `frontend-builder` | `build`       | Kompiluje produkcyjną paczkę frontendową i publikuje artefakt `webResources`. |
| `clean`       | `frontend-builder` | `build`       | Usuwa wygenerowany katalog budowania oraz elementy z `additionalDeletes`.     |
