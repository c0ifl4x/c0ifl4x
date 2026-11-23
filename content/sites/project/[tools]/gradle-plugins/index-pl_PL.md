---
kind: "Wtyczki Gradle"
summary: "Kolekcja autorskich wtyczek Gradle do tworzenia oprogramowania wbudowanego, rozwoju platform oraz automatyzacji budowania w moich projektach monorepo opartych na Gradle."
downloadUrls:
  - name: "Plugin ESP-IDF (JAR)"
    url: "https://m2.miloszgilga.pl/#/releases/pl/miloszgilga/gradle/plugin-esp-idf"
docsUrls:
  - name: "Plugin ESP-IDF (Javadoc)"
    url: "https://m2.miloszgilga.pl/javadoc/releases/pl/miloszgilga/gradle/plugin-esp-idf/latest"
---

## Dostępne wtyczki

| Moduł            | ID pluginu                     | Opis                                                                                     | Dokumentacja                                                                                      | Repozytorium                                                                            |
|:-----------------|:-------------------------------|:-----------------------------------------------------------------------------------------|:--------------------------------------------------------------------------------------------------|:----------------------------------------------------------------------------------------|
| `plugin-esp-idf` | `pl.miloszgilga.gradle.espidf` | Wieloplatformowa nakładka Gradle dla projektów firmware ESP-IDF (Windows, Linux, macOS). | [Javadoc](https://m2.miloszgilga.pl/javadoc/releases/pl/miloszgilga/gradle/plugin-esp-idf/latest) | [Reposilite](https://m2.miloszgilga.pl/#/releases/pl/miloszgilga/gradle/plugin-esp-idf) |

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

> [!NOTE]
> Aby korzystać z tej wtyczki, nadal trzeba mieć zainstalowane i skonfigurowane środowisko **ESP-IDF** w swoim systemie.
> Wtyczka służy jako wygodna, wieloplatformowa nakładka dla projektów monorepo korzystających z Gradle Kotlin DSL.

#### Konfiguracja

Wtyczkę należy zaaplikować, a rozszerzenie `espIdf` skonfigurować w pliku `build.gradle.kts` projektu firmware:

```kotlin
plugins {
  id("pl.miloszgilga.gradle.espidf") version "X.X.X"
}

espIdf {
  chip.set("esp32c3") // domyślnie: "esp32"
  port.set("/dev/ttyACM0") // domyślnie: port szeregowy zależny od systemu (COM3, /dev/ttyACM0, /dev/cu.usbmodem101)
  baud.set("921600") // domyślnie: "921600"
  appVersion.set("1.0.0") // domyślnie: project.version (lub "0.0.1-local-dev", jeśli nie określono)
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
