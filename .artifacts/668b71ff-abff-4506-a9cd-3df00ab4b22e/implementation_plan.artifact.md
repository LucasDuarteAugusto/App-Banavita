# Implementation Plan - Fixing IDE Inspection Results

This plan addresses the errors and warnings identified in the IDE inspection report. The goal is to eliminate critical Android and Internationalization errors, resolve Kotlin warnings, and reduce the overall "noise" in the project's health report.

## User Review Required

> [!IMPORTANT]
> **Proofreading & Typos (370k+ items)**: Most of these are due to the IDE not recognizing Portuguese or project-specific terms (e.g., "Banavita"). I will **not** attempt to "fix" these by changing variable or class names, as they are correct in context. I recommend adding "Banavita" and other common terms to the IDE dictionary manually.

> [!NOTE]
> **C/C++ Errors (12k+ items)**: These appear to be related to the indexing of the external `whisper.cpp` library downloaded via `FetchContent`. Since the build is successful and the native library functions correctly, these are likely tooling artifacts. I will leave them as-is to avoid interfering with the upstream library structure.

## Proposed Changes

### 1. Internationalization & Android Errors (Priority: High)
Most of the 90 Android errors and 26 Internationalization errors stem from hardcoded strings in ViewModels and UI components, and missing accessibility metadata.

#### [MODIFY] [strings.xml](file:///C:/projetos-Lucas/App-Banavita/app/src/main/res/values/strings.xml)
- Add missing string resources for error messages, labels, and content descriptions currently hardcoded in Kotlin files.
- Fix the "EN dash" warning in the telephone hint.

#### [MODIFY] ViewModels (e.g., [ActivationViewModel.kt](file:///C:/projetos-Lucas/App-Banavita/app/src/main/java/br/com/banavita/app/ui/activation/ActivationViewModel.kt), [ProdutoFormViewModel.kt](file:///C:/projetos-Lucas/App-Banavita/app/src/main/java/br/com/banavita/app/ui/cadastros/produtos/ProdutoFormViewModel.kt))
- Replace hardcoded error strings with string resource references (using context-wrapped resources or a UI event mechanism if preferred, but for now, I'll focus on extraction).

#### [MODIFY] UI Screens (e.g., [ExtractionReviewSheet.kt](file:///C:/projetos-Lucas/App-Banavita/app/src/main/java/br/com/banavita/app/ui/voice/ExtractionReviewSheet.kt), [HomeScreen.kt](file:///C:/projetos-Lucas/App-Banavita/app/src/main/java/br/com/banavita/app/ui/home/HomeScreen.kt))
- Replace hardcoded `Text("...")` with `stringResource(id = R.string.xxx)`.
- Provide meaningful `contentDescription` for all icons and interactive elements currently using `""` or hardcoded strings.

---

### 2. Kotlin & General Clean-up (Priority: Medium)
Addressing warnings that affect code quality and maintainability.

#### [MODIFY] Kotlin Files (Project-wide)
- Remove unused imports (e.g., in `VendaFormScreen.kt`).
- Add parameter names to boolean literal arguments (e.g., in `ProdutoFormScreen.kt`) to improve readability and satisfy Lint.
- Fix potential "Replace with ifBlank" warnings (as already done in `GroqConfig.kt`).

---

### 3. XML & Resource Warnings (Priority: Low)
#### [MODIFY] [themes.xml](file:///C:/projetos-Lucas/App-Banavita/app/src/main/res/values/themes.xml)
- Check for any missing parent themes or redundant attributes contributing to the 8 XML errors.

## Verification Plan

### Automated Tests
- Run `./gradlew lint` (or `./gradlew check`) to verify that the number of errors and warnings has decreased significantly.
- Run `./gradlew test` to ensure that string resource extraction didn't break any logic (though it shouldn't).

### Manual Verification
- Inspect the modified screens in the IDE preview or on a device to ensure text is still displayed correctly.
- Re-run the IDE "Inspect Code" on the specific files modified to confirm the warnings are gone.
