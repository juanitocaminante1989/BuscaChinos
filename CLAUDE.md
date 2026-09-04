# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

BuscaChinos: a small single-screen Android app. Users long-press a point on a Google Map to tag
a "chino" (corner store) with a name; tap a marker to select it and delete it. Everything persists
locally via Room — there is no backend/network sync.

## Build & run

There is no system-wide JDK on this machine. Gradle needs `JAVA_HOME` pointed at Android Studio's
bundled JBR:

```bash
export JAVA_HOME=/home/juan/android-studio-quail3-patch1-linux/android-studio/jbr
export ANDROID_HOME=/home/juan/Android/Sdk
./gradlew :app:assembleDebug
```

Install/launch on a connected device via adb (`/home/juan/adb-fastboot/platform-tools/adb`):

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.example.juan.buscachinos/.presentation.map.MainActivity
```

`app/src/test` and `app/src/androidTest` only contain the default template tests
(`ExampleUnitTest`, `ExampleInstrumentedTest`) — there is no real test suite yet.

Requires a Google Maps API key in `app/src/main/res/values/google_maps_api_key.xml`
(`google_maps_key` string, referenced from `AndroidManifest.xml`).

## Architecture: MVVM + Clean Architecture

Package layout under `com.example.juan.buscachinos`:

- **`domain/`** — framework-free. `model/` (`Chino`, `GeoPoint` — no Maps SDK types here),
  `repository/` (interfaces `ChinoRepository`, `LocationRepository`), `usecase/` (one class per
  operation, `operator fun invoke()`: `ObserveChinosUseCase`, `AddChinoUseCase`,
  `DeleteChinoUseCase`, `GetLastKnownLocationUseCase`).
- **`data/`** — implements the domain interfaces. `local/` is Room (`ChinoEntity`, `ChinoDao`,
  `AppDatabase`); `repository/ChinoRepositoryImpl` maps `ChinoEntity` <-> domain `Chino`;
  `location/AndroidLocationTracker` wraps `LocationManager`.
- **`presentation/map/`** — `MainActivity` is a thin View (MapView lifecycle, permissions,
  edge-to-edge insets, the tag dialog). `MapViewModel` exposes a single `StateFlow<MapUiState>`
  built from the use cases; `MainActivity` collects it via `repeatOnLifecycle(STARTED)` and
  re-renders all markers on every emission (`map.clear()` + re-add) rather than diffing —
  acceptable given the expected marker count.
- **`BuscaChinosApplication` + `AppContainer`** — manual DI (no Hilt/Dagger). `AppContainer` wires
  `data` implementations into the `domain` interfaces and builds the use cases;
  `MapViewModelFactory` pulls them from `(application as BuscaChinosApplication).container`.

Data flow: a long-press on the map opens an `AlertDialog` with an `EditText` ("Taguear" button) →
`MapViewModel.tagChino()` → `AddChinoUseCase` → Room insert. Because `ChinoDao.observeAll()`
returns a `Flow`, the insert (and a delete) flows straight back through the `StateFlow` and
`MainActivity` redraws automatically — there's no manual `map.addMarker`/`marker.isVisible` call
after a write. Marker selection/deletion is tracked by putting the Room-generated id in
`Marker.tag` (not by comparing lat/lng, which floating-point equality made unreliable before this
was refactored).

Room notes: table name stays `"chino"` (`@ColumnInfo` maps `codChino`/`chino_name`/`longitud`/
`latitud`), `codChino` is now `autoGenerate = true`, DB version 2, `fallbackToDestructiveMigration`
— no formal migrations exist, an upgrade just drops and recreates.

`minSdk` is 23 (bumped from 21 because Room 2.8.x / Lifecycle 2.11.x require it). KSP version must
stay matched to the Kotlin version in the root `build.gradle` (currently `2.2.20-2.0.4` for Kotlin
`2.2.20`) — check `com.google.devtools.ksp.gradle.plugin` on Maven Central before bumping either.
