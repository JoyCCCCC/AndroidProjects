# Individual Assignment

## MainActivity.kt Locations

- **ColorCard**  
  `ColorCard/app/src/main/java/com/example/colorcard/MainActivity.kt`

- **KotlinPracticeScreen**  
  `KotlinPracticeScreen/app/src/main/java/com/example/kotlinpracticescreen/MainActivity.kt`

- **ToggleCard**  
  `ToggleCard/app/src/main/java/com/example/togglecard/MainActivity.kt`

- **RowColumn**  
  `RowColumn/app/src/main/java/com/example/rowcolumn/MainActivity.kt`

- **BoxOverlay**  
  `BoxOverlay/app/src/main/java/com/example/boxoverlay/MainActivity.kt`

- **LazyColumn**  
  `LazyColumn/app/src/main/java/com/example/lazycolumn/MainActivity.kt`

- **Scaffold**  
  `Scaffold/app/src/main/java/com/example/scaffold/MainActivity.kt`

- **ThemedForm**  
  `ThemedForm/app/src/main/java/com/example/themedform/MainActivity.kt`

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

### 4) RowColumnLayout
- Builds a screen with a `Row` split into **25%** (left) and **75%** (right).
- Inside the right section, a `Column` has **3 weighted children** in ratio **2:3:5**.
- Each block is given a **color and label** to clearly visualize space usage.

### 5) BoxOverlay
- Implements a profile picture inside a `Box`.
- Adds a **notification badge** aligned to the bottom-end corner using `Modifier.align()`.
- Includes a toggle `Button` to **show/hide** the badge.

### 6) LazyColumnContacts
- Displays a **contact list** grouped alphabetically (A–J).
- Each section uses `stickyHeader` to keep the current letter visible while scrolling.
- At least **50 contacts** (5 per letter, 50 total).
- A **Floating Action Button (FAB)** appears after scrolling past item 10.
- FAB triggers `animateScrollToItem(0)` to smoothly scroll to the top.

### 7) ScaffoldDemo
- Uses a `Scaffold` layout with:
  - **topBar** showing the app title.
  - **bottomBar** with 3 items: *Home, Settings, Profile*.
  - **floatingActionButton** that shows a **Snackbar** when clicked.
- Applies `innerPadding` so the content avoids overlap with bars and FAB.

### 8) ThemedLoginForm
- Implements a simple login form with two `OutlinedTextField`s: **Username** and **Password**.
- Fields are styled using **Material 3 colors and typography**.
- Adds basic **validation**:
  - If either field is empty on submit, an **error message** is shown below the field.

---

## Requirements

- Android Studio Narwhal 3   
- Minimum SDK: API 24 (Android 7.0, Nougat) 
- Running devices: Medium Phone API 36.0

---

## How to Run

1. Clone the repo.
2. Open the project you want in **Android Studio**.
3. Select the proper device and click **Run ▶️**.
