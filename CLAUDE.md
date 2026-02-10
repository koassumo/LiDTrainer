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
- `core/` - Cross-cutting concerns (TimeProvider, error handling)
- `data/` - Repository implementations, API clients, mappers, DTOs
- `domain/` - Business models, repository interfaces, use cases
- `di/` - Koin dependency injection modules
- `ui/` - Compose screens, ViewModels, theme, navigation

### Platform-Specific Patterns
The project uses `expect/actual` declarations for platform differences:
- **HttpClient engines**: OkHttp (Android), Darwin (iOS)
- **SQLDelight drivers**: AndroidSqliteDriver, NativeSqliteDriver
- **Settings storage**: SharedPreferences (Android), NSUserDefaults (iOS)

## Key Technologies

- **DI**: Koin 4.1.1 - See `di/` directory for module organization
- **Database**: SQLDelight 2.2.1 - Local question storage
- **Networking**: Ktor Client 3.4.0 - Language pack downloads, Firebase API
- **Serialization**: kotlinx-serialization 1.10.0
- **Date/Time**: kotlinx-datetime 0.7.1
- **Settings**: multiplatform-settings 1.3.0 - User preferences (theme, language)
- **State Management**: StateFlow with `MutableStateFlow` + `.asStateFlow()` pattern

## Dependency Injection

Koin modules are organized in `di/`:
- `AppModule.kt` - Entry point, initializes all modules
- `DataModule.kt` - Repositories, API clients, database
- `DomainModule.kt` - Use cases (all as `factory`)
- `UiModule.kt` - ViewModels (all as `viewModelOf`)
- `PlatformModule.kt` - Platform-specific implementations (`expect/actual`)

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

## UI Architecture & Patterns

### Single TopBar Architecture

The app uses a **single Scaffold in MainScreen** that owns TopBar and Snackbar. **No BottomBar** (unlike MyCorc). Child screens publish their TopBar configuration through `TopBarState` via CompositionLocal.

**Key components:**
- **`TopBarState`** (`ui/common/TopBarState.kt`) - Reactive state holder with `mutableStateOf` properties
- **`LocalTopBarState`** - `staticCompositionLocalOf` for distributing state down the composition tree
- **Single Scaffold** - MainScreen owns the only Scaffold, child screens are content-only

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

### Navigation & BackHandler

**AppBackHandler** - Cross-platform back button handling:
- Sub-screens → previous screen (from navigation stack)
- Main screen → exit dialog
- iOS: every sub-screen must have a back button in TopBar (no physical back button)

### Centralized Dimensions (Dimens.kt)

All UI dimensions are centralized in `ui/common/Dimens.kt`. **Never hardcode `.dp` values** in screens or components.

```kotlin
// ❌ Wrong
Text("Title", modifier = Modifier.padding(16.dp))

// ✅ Correct
Text("Title", modifier = Modifier.padding(Dimens.SpaceMedium))
```

### Reusable Components

**CommonButton & CommonOutlinedButton** — unified button components:

**CRITICAL: ALL buttons MUST use these components. NEVER use Material3 `Button` or `OutlinedButton` directly.**

```kotlin
// ✅ Correct
CommonButton(text = strings.startTest, onClick = { viewModel.start() })

// ❌ WRONG
Button(onClick = { ... }) { Text("Start") }
```

**CommonCard** — standard card component:
```kotlin
CommonCard(modifier = Modifier.fillMaxWidth(), onClick = { }) {
    Text("Question card content")
}
```

### Color System & Theming

**Architecture:**
```
Color.kt (color definitions) → Theme.kt (Material3 role binding) → MaterialTheme.colorScheme → Component
```

**Rules:**
1. Never set colors in component code. All via `Color.kt` → `Theme.kt` → `MaterialTheme`.
2. To change a color — modify only `Color.kt`.
3. Dynamic Color (Material You) is disabled.
4. Light + Dark themes supported.
5. Theme switching: System / Light / Dark (via Settings).

### Localization

All user-facing strings are managed through **Jetpack Compose Resources** (multiplatform string resources).

**Supported UI languages:** English (default), Russian, German.

**Adding new strings:**
1. Add to interface: `domain/strings/AppStrings.kt`
2. Add to data class: `ui/theme/AppStringsImpl.kt`
3. Add resource mapping: `rememberAppStrings()` in `AppStringsImpl.kt`
4. Add XML entries to all 3 language files: `composeResources/values*/strings.xml`

**Usage in screens:**
```kotlin
val strings = LocalAppStrings.current
Text(strings.myStringKey)  // ✅ Correct
Text("Hardcoded text")      // ❌ Wrong
```

**Note:** UI localization (EN/RU/DE) is separate from quiz content localization (~200 languages loaded from server).

## Screens

1. **LanguageSelectScreen** — First launch: choose native language for quiz content
2. **DashboardScreen** — Statistics, "Study questions" / "Practice test" buttons
3. **LearnScreen** — Main screen with question cards
4. **SettingsScreen** — Theme, UI language

## Data Model

### Question Entity (SQLDelight)
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

## Reference Project

Architecture patterns, UI components, theme system, localization approach, and DI structure are based on **MyCorc** (`/Users/koassumo/StudioProjects/MyCorc`). Consult its `CLAUDE.md` for detailed patterns.
