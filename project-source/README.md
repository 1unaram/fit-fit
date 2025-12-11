# FitFit – Weather-based Outfit Management App

FitFit is an Android application that helps users **record what they wore**, **plan what to wear**, and **connect outfits with real weather data**.  
It combines a **digital closet**, **outfit history**, and **weather integration** to support both retrospective tracking and future planning.

2025-2 Mobile Application Development Course Project

by 김하람 나정원 이가연

---

## 1. Project Overview

FitFit is designed around two core ideas:

- Not just “What did I wear today?”, but also **“What should I wear next?”**
- Outfits are not stored as simple notes: each outfit is tied to **time, location, and weather**.

Key user scenarios:

- Record today’s outfit right after coming home.
- Manage a digital closet (Outerwear / Tops / Bottoms).
- Plan an outfit for a future date (e.g., a wedding) based on weather forecasts.
- Automatically attach **actual past weather** to outfits after the day has passed.

---

## 2. Main Features

### 2.1 Authentication

- Sign up from the initial screen using the **Sign Up** button.
- Log in with ID and password.
- Authentication is handled via Firebase Authentication.

### 2.2 Digital Closet (Clothes)

The **Clothes** screen works like a digital wardrobe.

- **Read**
    - View all clothes you own.
    - Categorize items as **Outerwear**, **Tops**, or **Bottoms**.
    - Filter by category using chips at the top.
    - Tap a card to see detailed information.
- **Create**
    - Tap the **+ button** (bottom-right) and fill in details to register a new item.
- **Update**
    - If you forgot to enter the URL or other information, you can edit it later.
    - Tap the edit button in the top-right to update details.
- **Delete**
    - Remove clothes you no longer need.

### 2.3 Outfit Management (Today’s Outfit)

- On the **Outfit** screen, users can record what they wore today.
- Example: After coming home, **Alice** saves today’s outfit:
    - Tap the **+ button**, select clothes, set time and occasion.
    - Add a **comment** (e.g., “I was cold because I didn’t bring outerwear”) for future reference.

### 2.4 Future Outfit Planning

FitFit is also for **planning future outfits**:

- Users can create outfits for **future dates**.
- Example: Alice is attending a wedding three days later:
    1. On the **Home** screen, tap the **Weather Card** to view the **7-day forecast**.
    2. Return to Home and select the wedding date.
    3. Based on the predicted **temperature and weather** of that day, the app automatically **filters suitable outfits**.
    4. Alice further filters by the **“wedding” occasion** and chooses an outfit.
    5. She saves the outfit for that future day.
- Since the day hasn’t happened yet:
    - The weather field is shown as **“to be updated”**.
    - After that day passes, the app updates the outfit with the **actual weather** using OpenWeather’s historical data.

---

## 3. Mobile Development

From a mobile development perspective, FitFit uses a **modern Android stack** centered on **Kotlin** and **Jetpack Compose**.

- UI is composed of small **Composable** units.
- Reusable components are extracted to improve **collaboration** and **maintenance**.

**UI structure:**

- `ui/screen`
    - Full-screen composables (e.g., `HomeScreen`, `ClothesScreen`, `OutfitScreen`, `WeatherScreen`).
- `ui/screen/.../components`
    - Screen-specific UI components.
- `ui/components`
    - Shared, reusable components across multiple screens.

---

## 4. Design Pattern – MVVM

The project follows the Android-recommended **MVVM (Model–View–ViewModel)** architecture.

- Codebase is organized into three main layers:
    - `data`
    - `viewmodel`
    - `ui`

**Flow:**

1. The user performs an action in the **UI** (e.g., taps a button).
2. The corresponding **ViewModel** method is called.
3. The ViewModel:
    - Validates input.
    - Calls the appropriate **Repository** in the `data` layer.
    - Exposes results as **`StateFlow`**.
4. The **UI** collects this state and recomposes automatically when the value changes.

This design provides:

- Clear separation of concerns.
- Testable business logic in ViewModels.
- A unidirectional data flow: `data → viewmodel → ui`.

---

## 5. Data Management

FitFit uses an **offline-first + cloud sync** strategy.

### 5.1 Outfit Storage & Sync

When a user saves an outfit:

1. The outfit is first saved in the **local Room database**.
2. It is marked with `isSynced = false`.
3. A background process attempts to sync it to **Firebase Realtime Database**.
4. If sync succeeds:
    - The record is updated to `isSynced = true`.
5. If sync fails:
    - `isSynced` remains `false`, and the app can retry later (e.g., when network is available).

This approach ensures:

- The app works reliably **offline** (local DB is always the primary source).
- Firebase is kept up-to-date **asynchronously**.
- Sync status is explicitly tracked with `isSynced`.

---

## 6. RESTful Weather API Integration

FitFit integrates weather data using **Retrofit** and **OpenWeather’s REST API**.

### 6.1 Current & Forecast Weather

- `OpenWeatherRepository` wraps Retrofit calls to OpenWeather.
- The app uses the **One Call API** to retrieve:
    - Current weather
    - Hourly forecast (today)
    - Daily forecast (7 days)
- `WeatherViewModel`:
    - Fetches data based on the user’s current location.
    - Produces UI-friendly models:
        - Hourly weather list (for today).
        - Daily weather list (for a week).
    - Used in:
        - Home **Weather Card**.
        - Detailed **WeatherScreen** (hourly + weekly).

### 6.2 Historical Weather & Outfit Aggregation

- The app uses OpenWeather’s **Time Machine API** to fetch **past weather** for specific timestamps.
- When an outfit has a start/end time and location:
    - The app samples hourly weather for that period.
    - Aggregates metrics (min/max/average temperature, wind, precipitation).
    - Stores a summarized weather description with the outfit.

This allows the app to later answer:
- “What was the weather like when I wore this outfit?”

---

## 7. Technologies & Tools

### 7.1 Core Technologies

- **Language**: Kotlin
- **UI**: Jetpack Compose, Material Design 3
- **Architecture**: MVVM, Repository pattern
- **Async**: Kotlin Coroutines, Flow / StateFlow
- **Local Storage**: Room Database, DataStore
- **Backend / Cloud**:
    - Firebase Realtime Database
- **Networking**:
    - Retrofit
    - OkHttp
- **Weather API**:
    - OpenWeather One Call API (current, hourly, daily)
    - OpenWeather Time Machine API (historical)

### 7.2 Collaboration & Project Management Tools

- **Figma** – UI/UX design, wireframes, component design.
- **GitHub** – Version control, branch-based development, pull requests, code review.
- **Notion** – Requirements, task tracking, documentation, meeting notes.

---

## 8. Firebase Configuration (`google-services.json`)

This project uses Firebase for authentication and Realtime Database.

The actual `google-services.json` file is **not** included in this repository for security reasons.  
Instead, there is a template file:

- `app/google-services.json.example`

### How to set up Firebase locally

1. Create your own Firebase project in the Firebase console.
2. Add an Android app with your package name.
3. Download the generated `google-services.json`.
4. Place it in:

    ```
    app/google-services.json
    ```

5. Make sure `app/google-services.json` is **gitignored** (it should not be committed).

After this, you can build and run the app with your own Firebase configuration.

---

## 9. High-level Architecture

- **UI Layer (`ui`)**
- Jetpack Compose screens and components.
- Observes `StateFlow` from ViewModels.
- Responsible for rendering and user interaction.

- **ViewModel Layer (`viewmodel`)**
- Screen-specific ViewModels (e.g., `ClothesViewModel`, `OutfitViewModel`, `WeatherViewModel`).
- Holds UI state and handles screen-level business logic.
- Communicates with repositories.

- **Data Layer (`data`)**
- **Local**: Room entities, DAOs, database.
- **Remote**: Firebase, OpenWeather API (Retrofit interfaces).
- **Repositories**: Abstract data sources and expose clean APIs to ViewModels.
