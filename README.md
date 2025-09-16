# Individual Assignment

## MainActivity.kt Locations

- **ColorCard**  
  `ColorCard/app/src/main/java/com/example/colorcard/MainActivity.kt`

- **KotlinPracticeScreen**  
  `KotlinPracticeScreen/app/src/main/java/com/example/kotlinpracticescreen/MainActivity.kt`

- **ToggleCard**  
  `ToggleCard/app/src/main/java/com/example/togglecard/MainActivity.kt`  
  *(package name may differ; open the project in Android Studio to confirm the exact package path.)*

---

## Project Overviews

### 1) ColorCard
- Implements a composable `ColorCard(color, label)` that shows a colored card with centered text.
- The screen displays **three cards** using **three different colors**.
- Demonstrates multiple modifier combinations: `padding`, `background`, `border`, `size` (at least two different combinations).

### 2) ToggleCard
- Implements a composable `ToggleCard()` that shows a tappable card whose message **toggles on click**  
  e.g., *“Tap to see a fun fact!” → “Kotlin was created by JetBrains!”*
- Uses `rememberSaveable { mutableStateOf(false) }` to persist toggle state across configuration changes.
- Simple UI built with `Box`, `Text`, `clickable`, and basic modifiers (`background`, `border`, `size`).

### 3) KotlinPracticeScreen
- Implements a composable `KotlinPracticeScreen()` that demonstrates three Kotlin/Compose patterns:
  1. **`when` expression** on an input string (e.g., `"cat"`, `"dog"`, `"fish"`) and shows the result.
  2. Shows a message **only if** a nullable string is not null (`?.let { ... }`).
  3. A **counter** with a `Button` that increments **only while value < 5**.

---

## Requirements

- Android Studio Narwhal 3   
- Minimum SDK: API 24 (Android 7.0, Nougat) 

---
