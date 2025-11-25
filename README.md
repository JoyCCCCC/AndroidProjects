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

- **LifeTracker**  
  `LifeTracker/app/src/main/java/com/example/lifetracker/MainActivity.kt`

- **CounterPP**  
  `CounterPP/app/src/main/java/com/example/counterpp/MainActivity.kt`

- **TempDashboard**  
  `TempDashboard/app/src/main/java/com/example/tempdashboard/MainActivity.kt`

- **altimeter**  
  `altimeter/app/src/main/java/com/example/altimeter/MainActivity.kt`

- **compass**  
  `compass/app/src/main/java/com/example/compass/MainActivity.kt`

- **soundmeter**  
  `soundmeter/app/src/main/java/com/example/soundmeter/MainActivity.kt`

- **gyroball**  
  `gyroball/app/src/main/java/com/example/gyroball/MainActivity.kt`

- **locationinfo**  
  `locationinfo/app/src/main/java/com/example/locationinfo/MainActivity.kt`

- **trailmap**  
  `trailmap/app/src/main/java/com/example/trailmap/MainActivity.kt`

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

### 8) LifeTracker  
- Builds an app called **LifeTracker** that logs and displays Android lifecycle events in real time.  
  - Captures events via a **LifecycleEventObserver** and logs each transition with a timestamp and color code.
  - Uses a **ViewModel** to store a list of lifecycle events (e.g., onCreate, onStart, onResume) in a `StateFlow<List<LifeEvent>>`.  
  - Displays real-time updates in a **LazyColumn**, showing event name, time, and colored indicator.  
  - Persists logs across configuration changes (e.g., rotation) using ViewModel state.  
  - Shows the current lifecycle state at the top with a colored status card.  
  - Includes a **Snackbar** notification for each lifecycle transition, which can be toggled on/off via a Switch in the top bar.

### 9) Counter++  
- Implements a reactive counter using **ViewModel** and **StateFlow** for unidirectional data flow.  
- All UI updates are driven by the ViewModel’s state, which manages manual actions (+1, -1, Reset) and a coroutine-based **auto-increment job** that runs every few seconds when Auto mode is on.  
- The app includes a **Settings screen** where users can adjust the auto-increment interval.
- The main screen collects state with `collectAsState()` and displays live updates.

### 10) Temperature Dashboard

- Uses **ViewModel** and **StateFlow** to manage and stream reactive temperature data updates.
- A coroutine generates random temperature readings every 2 seconds (65°F–85°F) and keeps only the latest 20 readings.  
- The ViewModel computes real-time **current**, **average**, **min**, and **max** values directly from the StateFlow state.
- The Compose UI collects the StateFlow with `collectAsState()`, showing a live **list**, **stat summary**, and a **Canvas-based line chart**.  
- Includes a **pause/resume switch** to control data streaming dynamically.

### 11) Altimeter
Builds a basic altimeter app using the **pressure sensor** to estimate altitude. Includes a **simulation mode** where pressure changes automatically, causing altitude and background color to update.
- Reads pressure using `Sensor.TYPE_PRESSURE`
- Converts pressure to altitude using barometric formula  
  *(P0 = 1013.25 hPa reference pressure)*
- Updates UI in real-time
- Background gets darker at higher altitude
- Simulated mode generates artificial pressure changes

### 12) Compass & Digital Level
Uses **magnetometer + accelerometer** to calculate compass heading, and **gyroscope** for tilt-based digital level (pitch/roll).
- Calculates heading using rotation matrix + orientation sensors
- Displays a real-time compass needle using Canvas rotation in Compose
- Computes pitch/roll using accelerometer + gyroscope
- Displays real-time tilt values and compass direction while tilting the device

### 13) Sound Meter (Decibel Detector)
Uses the **microphone** to record amplitude and convert to decibel (dB). Displays a visual sound meter with color-changing indicator.
- Uses `AudioRecord` for raw audio input
- Converts amplitude → dB
- Live UI updates (meter bar + numerical dB)
- Threshold alert when noise is too loud. If dB > threshold (e.g., 80 dB), UI turns red and shows a warning.
- Grant microphone permission when prompted
- Note: Enable microphone in the emulator:  
  **Extended controls → Microphone → Virtual microphone uses host audio input**

### 14) Gyroscope-Controlled Ball Game
Implements a gyroscope-controlled ball maze game where the user tilts their phone to navigate a red ball through obstacles to reach a green goal area.
- Uses Sensor.TYPE_GYROSCOPE to detect phone tilt and translate it into ball movement with physics simulation.
- Custom GameView extends View and uses Canvas to draw walls, obstacles, and the ball.
- Full-screen responsive maze with 8 obstacles positioned using screen percentages to adapt to all device sizes.
- Ball bounces off walls and obstacles with reduced velocity, and is constrained within screen bounds to prevent falling off.
- Victory condition displays "You Win!" when ball reaches the 15% screen-sized green goal area in the bottom-right corner.

### 15) Location Information
Displays a Google Map centered on the **current GPS location**, adds markers, shows address via `Geocoder`, and allows the user to place custom markers.
- Requests runtime location permission (`ACCESS_FINE_LOCATION`).
- Displays a Google Map centered on the user’s current GPS location.
- Uses `FusedLocationProviderClient` with `requestLocationUpdates()` to continuously track the user's movement.
- Adds a marker at the user’s current location (“You are here”).
- Allows the user to tap anywhere on the map to place custom markers.
- Displays human-readable address information using `Geocoder`, updating dynamically based on either the user's position or the selected custom marker.
- Requires the following dependencies in `app/build.gradle`:
  - Maps Compose
  - Play Services Maps
  - Play Services Location
  - AppCompat (permissions + theme)
- Requires a valid **Google Maps API Key** inside the AndroidManifest `<application>` section.
- Requires the app to use an AppCompat-compatible theme, such as: Theme.AppCompat.DayNight.NoActionBar.

### 16) Polyline & Polygon
Displays a Google Map with a hiking trail drawn as a polyline and a park area highlighted using a polygon.  
Users can interact with both overlays and customize their appearance through a simple control panel.

- Renders a hiking trail using a Polyline and a park region using a Polygon.
- Supports customization of polyline color and width, as well as polygon border thickness.
- Overlay interactions enabled via `clickable = true`, providing contextual info when users tap the trail or park area.
- Uses Compose state (`remember`, `mutableStateOf`) to dynamically update overlay properties such as color and stroke width without reloading the map.
- Requires the following dependencies in `app/build.gradle`:
  - Maps Compose
  - Play Services Maps
  - Play Services Location
- Requires a valid **Google Maps API Key** inside the AndroidManifest `<application>` section.

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
