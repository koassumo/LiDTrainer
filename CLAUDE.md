# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

LiDTrainer is a Kotlin Multiplatform (KMP) application for preparing for the "Leben in Deutschland" (LiD) German citizenship test. It targets **Android** and **iOS** using Compose Multiplatform for shared UI.

Key feature: support for ~200 languages loaded on demand from server. Users choose their native language on first launch; if the language pack isn't bundled, it's fetched from Firebase.

## Build Commands

### Android
```shell
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:installDebug
```

### iOS
```shell
./gradlew :composeApp:compileKotlinIosSimulatorArm64
# Open ./iosApp directory in Xcode for full build/run
```

### Tests
```shell
./gradlew :composeApp:allTests
./gradlew :composeApp:testDebugUnitTest      # Android
./gradlew :composeApp:iosSimulatorArm64Test  # iOS Simulator
```

## Architecture

The project follows **Clean Architecture** with three layers:

```
UI Layer (Screens + ViewModels)
        ↓
Domain Layer (Use Cases + Interfaces)
        ↓
Data Layer (Repositories + API/DB)
```

### Source Structure
- `composeApp/src/commonMain/` - Shared code for all platforms
- `composeApp/src/androidMain/` - Android-specific implementations
- `composeApp/src/iosMain/` - iOS-specific implementations

### Key Directories (under `commonMain/kotlin/org/igo/lidtrainer/`)
- `core/` - Cross-cutting concerns (reserved for future use)
- `data/` - Repository implementations, API clients, mappers, DTOs
- `domain/` - Business models, repository interfaces, use cases
  - `domain/strings/` - AppStrings interface (localization contract)
- `di/` - Koin dependency injection modules
- `ui/` - Compose screens, ViewModels, theme, navigation
  - `ui/common/` - Reusable components (CommonButton, CommonCard, TopBarState, etc.)
  - `ui/theme/` - Color.kt, Theme.kt, AppStringsImpl, AppThemeConfig, AppLanguageConfig
  - `ui/navigation/` - Destinations
  - `ui/screen/main/` - MainScreen (Single Scaffold) + MainViewModel

### Platform-Specific Patterns (`expect/actual`)
- **BackHandler**: `AppBackHandler` — Android delegates to BackHandler, iOS is no-op
- **ExitApp**: `exitApp()` — Android kills process, iOS is no-op
- **Settings storage**: SharedPreferences (Android), NSUserDefaults (iOS) — via PlatformModule
- **HttpClient engines**: OkHttp (Android), Darwin (iOS) — via Ktor
- **SQLDelight drivers**: AndroidSqliteDriver, NativeSqliteDriver — via PlatformModule (not yet configured)

## Key Technologies

- **Kotlin**: 2.3.0
- **Compose Multiplatform**: 1.10.0
- **AGP**: 8.13.2 (not upgrading to 9.x — breaking changes)
- **DI**: Koin 4.1.1 — See `di/` directory for module organization
- **Database**: SQLDelight 2.2.1 — Local question storage (plugin not yet applied)
- **Networking**: Ktor Client 3.4.0 — Language pack downloads, Firebase API
- **Serialization**: kotlinx-serialization 1.10.0
- **Date/Time**: kotlinx-datetime 0.7.1
- **Coroutines**: kotlinx-coroutines 1.10.2
- **Settings**: multiplatform-settings 1.3.0 — User preferences (theme, language)
- **Build Config**: BuildKonfig 0.17.1 — API keys from local.properties (plugin not yet applied)
- **State Management**: StateFlow with `MutableStateFlow` + `.asStateFlow()` pattern

## Dependency Injection

Koin modules are organized in `di/`:
- `AppModule.kt` — Entry point with `initKoin()`, loads all modules
- `DataModule.kt` — Repositories, API clients, database (currently empty)
- `DomainModule.kt` — Use cases as `factory` (currently empty)
- `UiModule.kt` — ViewModels as `viewModelOf` (MainViewModel registered)
- `PlatformModule.kt` — `expect/actual` per platform (Settings registered)

**Initialization:**
- Android: `LiDTrainerApp` (Application class) → `initKoin { androidLogger(); androidContext() }`
- iOS: `KoinStarter.startKoinIos()` → `initKoin()` (called from iOSApp.swift `init()`)

## Collaborative Development Principles

### Workflow & Permissions
- **NEVER run the application** without explicit user request
- **NEVER create git commits** unless explicitly asked by the user
- **NEVER push to remote** unless explicitly asked by the user
- The user handles app execution, commits, and pushes manually — focus on code changes only

### Code Quality & Context
- **Meaningful Preservation**: Do not delete comments unless the refactored code makes them strictly redundant.
- **KMP Best Practices**: Prioritize shared code in `commonMain`. When using platform-specific APIs, ensure they follow the project's established `expect/actual` patterns.
- **Minimize Color Hardcoding**: Avoid using explicit `Color()` values in components. All colors should be managed through `Color.kt` → `Theme.kt` → `MaterialTheme.colorScheme`.
- **Use Component Templates**: Always use `CommonButton`/`CommonOutlinedButton` instead of Material3 `Button`/`OutlinedButton`. Always use `CommonCard` for cards. These ensure design consistency.

### Technical Guardrails
- **Sync DI**: Ensure any new components are registered in the appropriate Koin module (`di/`).
- **Domain First**: Keep business logic in Use Cases. Avoid putting complex logic directly into ViewModels or Data Mappers.
- **Import conflicts**: The `ui/` package can conflict with `org.jetbrains.compose.ui.tooling.preview`. Avoid `@Preview` annotation in `commonMain` files; use it only in `androidMain` with `androidx.compose.ui.tooling.preview.Preview`.

## UI Architecture & Patterns

### Single TopBar Architecture

The app uses a **single Scaffold in MainScreen** that owns TopBar, Snackbar, and ExitDialog. **No BottomBar, no FAB**. Child screens publish their TopBar configuration through `TopBarState` via CompositionLocal.

**Key components:**
- **`TopBarState`** (`ui/common/TopBarState.kt`) — Reactive state holder with `mutableStateOf` properties
- **`LocalTopBarState`** — `staticCompositionLocalOf` for distributing state down the composition tree
- **`CommonTopBar`** (`ui/common/CommonTopBar.kt`) — CenterAlignedTopAppBar with back button + divider
- **Single Scaffold** — MainScreen owns the only Scaffold, child screens are content-only

**Example usage in child screens:**
```kotlin
@Composable
fun DashboardScreen() {
    val topBar = LocalTopBarState.current
    val strings = LocalAppStrings.current

    topBar.title = strings.dashboardTitle
    topBar.canNavigateBack = false

    Box(Modifier.fillMaxSize()) { ... }
}
```

**Rules:**
- ✅ Single source of truth for TopBar state
- ✅ No nested Scaffolds (avoids window insets issues)
- ✅ Centralized back navigation handling
- ❌ No BottomNavigationBar in this project
- ❌ No FAB in this project

### Navigation & BackHandler

**MainViewModel** manages navigation with a stack:
- `navigateTo(route)` — main tabs clear stack; sub-screens push to stack
- `navigateBack()` — pops from stack or returns to Dashboard
- `currentRoute: StateFlow<String>` — observed by MainScreen

**AppBackHandler** — Cross-platform back button handling:
- Dashboard → show ExitDialog
- Sub-screens → `viewModel.navigateBack()` (pops from navigation stack)
- iOS: every sub-screen must have a back button in TopBar (no physical back button)

**Destinations** (`ui/navigation/Destinations.kt`):
- `LANGUAGE_SELECT`, `DASHBOARD`, `LESSON`, `SETTINGS`

### Centralized Dimensions (Dimens.kt)

All UI dimensions are centralized in `ui/common/Dimens.kt`. **Never hardcode `.dp` values** in screens or components.

```kotlin
// ❌ Wrong
Text("Title", modifier = Modifier.padding(16.dp))

// ✅ Correct
Text("Title", modifier = Modifier.padding(Dimens.SpaceMedium))
```

### Reusable Components

**CommonButton & CommonOutlinedButton** (`ui/common/CommonButton.kt`):

**CRITICAL: ALL buttons MUST use these components. NEVER use Material3 `Button` or `OutlinedButton` directly.**

```kotlin
// ✅ Correct
CommonButton(text = strings.startTest, onClick = { viewModel.start() })

// ❌ WRONG
Button(onClick = { ... }) { Text("Start") }
```

**CommonCard** (`ui/common/CommonCard.kt`):
```kotlin
CommonCard(modifier = Modifier.fillMaxWidth(), onClick = { }) {
    Text("Question card content")
}
```

**LoadingContent** (`ui/common/LoadingContent.kt`) — semi-transparent overlay with spinner.

**ExitDialog** (`ui/common/ExitDialog.kt`) — localized exit confirmation dialog.

### Color System & Theming

**Architecture:**
```
Color.kt (color definitions) → Theme.kt (Material3 role binding) → MaterialTheme.colorScheme → Component
```

**Current palette:**
- Light theme: **Indigo** primary, **Teal** secondary
- Dark theme: **Indigo 300** primary, **Teal 300** secondary
- Custom colors: `myCardBorder`, `myBarDivider`, answer status colors (correct/incorrect)

**Rules:**
1. Never set colors in component code. All via `Color.kt` → `Theme.kt` → `MaterialTheme`.
2. To change a color — modify only `Color.kt`.
3. Dynamic Color (Material You) is disabled.
4. Light + Dark themes supported.
5. Theme switching: System / Light / Dark (via Settings, to be implemented).

### Localization

All user-facing strings are managed through **Jetpack Compose Resources** (multiplatform string resources).

**Supported UI languages:** English (default), Russian, German.

**Key files:**
- `domain/strings/AppStrings.kt` — interface (35 string properties)
- `ui/theme/AppStringsImpl.kt` — data class + `LocalAppStrings` + `rememberAppStrings()`
- `composeResources/values/strings.xml` — English
- `composeResources/values-ru/strings.xml` — Russian
- `composeResources/values-de/strings.xml` — German

**Adding new strings:**
1. Add to interface: `domain/strings/AppStrings.kt`
2. Add to data class constructor: `ui/theme/AppStringsImpl.kt`
3. Add resource mapping in `rememberAppStrings()`: `ui/theme/AppStringsImpl.kt`
4. Add XML entries to all 3 language files: `composeResources/values*/strings.xml`

**Usage in screens:**
```kotlin
val strings = LocalAppStrings.current
Text(strings.myStringKey)  // ✅ Correct
Text("Hardcoded text")      // ❌ Wrong
```

**Note:** UI localization (EN/RU/DE) is separate from quiz content localization (~200 languages loaded from server).

## Screens

1. **LanguageSelectScreen** — First launch: choose native language for quiz content (not yet implemented)
2. **DashboardScreen** — Statistics, "Study questions" / "Practice test" buttons (placeholder)
3. **LessonScreen** — Main screen with question cards
4. **SettingsScreen** — Theme, UI language (placeholder)

## Data Model

### Question Entity (SQLDelight — not yet created)
```
- questionNumber: Int
- questionText: String
- answer1: String
- answer2: String
- answer3: String
- answer4: String
- correctAnswerIndex: Int
- userStatus: enum (NOT_ANSWERED / CORRECT / INCORRECT)
```

## What's Done vs What's Remaining

### Done
- [x] Project setup with all dependencies (Gradle sync passes)
- [x] Koin DI infrastructure (5 modules + platform init)
- [x] Theme system (Color.kt + Theme.kt + Dimens.kt + Light/Dark)
- [x] Localization system (AppStrings + 3 languages + CompositionLocal)
- [x] Common UI components (CommonButton, CommonCard, CommonTopBar, LoadingContent, ExitDialog)
- [x] Single Scaffold architecture (MainScreen + TopBarState)
- [x] Navigation with stack (MainViewModel + Destinations)
- [x] Cross-platform BackHandler + ExitApp (expect/actual)

### Remaining
- [ ] SettingsScreen (theme + language switching)
- [ ] LanguageSelectScreen (first launch flow)
- [ ] DashboardScreen (statistics + navigation)
- [x] LessonScreen (question cards)
- [ ] SQLDelight database setup (Question entity)
- [ ] Ktor API client (language pack loading from Firebase)
- [ ] BuildKonfig setup (API keys)
- [ ] SettingsRepository (persist theme/language choices)

## Reference Project

Architecture patterns, UI components, theme system, localization approach, and DI structure are based on **MyCorc** (`/Users/koassumo/StudioProjects/MyCorc`). Consult its `CLAUDE.md` for detailed patterns.
