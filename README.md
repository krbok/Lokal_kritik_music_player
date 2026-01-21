# 🎵 Lokal Music Player

## Project Overview
Lokal Music Player is a modern Android music streaming application built using **Jetpack Compose** and **Kotlin**. The app consumes the **JioSaavn public API** to fetch real-time music data and focuses on delivering a seamless playback experience with a **persistent Mini Player** and a dedicated **Full Player** screen that remain fully synchronized across navigation.

---

## Tech Stack
- **Language:** Kotlin  
- **UI Framework:** Jetpack Compose (100%)  
- **Architecture:** MVVM (Model–View–ViewModel)  
- **State Management:** StateFlow / MutableStateFlow  
- **Dependency Injection:** Hilt  
- **Media Playback:** Media3 ExoPlayer & MediaSession  
- **Networking:** Retrofit & Gson  
- **Image Loading:** Coil  
- **Asynchronous Programming:** Kotlin Coroutines & Flow  

---

## Architecture & State Management
The application follows a clean **MVVM architecture** with a clear separation of concerns between UI, state, and playback logic.

### Single Source of Truth (SSOT)
`PlayerViewModel` acts as the central state holder for all playback-related UI state. It exposes immutable `StateFlow` objects such as:
- `currentSong`
- `isPlaying`
- `progress`

This ensures consistent and predictable state across the entire application.

### UI Synchronization
Both the **Mini Player** (persistent bottom component) and the **Full Player screen** observe the same `PlayerViewModel` instance. By sharing the ViewModel using Hilt (activity-scoped) or Navigation, any playback state change is immediately reflected across all UI components, ensuring both players always display:
- The same song  
- The same play/pause state  
- The same playback progress  

### Unidirectional Data Flow
User actions such as **play, pause, seek, next, and previous** are dispatched from the UI to the ViewModel, which then communicates with the `MusicService`. This keeps UI logic simple and predictable while isolating playback concerns.

---

## Media Playback
- **Media3 ExoPlayer:**  
  Used as the core audio playback engine for reliable and efficient streaming of remote audio URLs provided by the JioSaavn API.

- **Background Playback:**  
  Playback is implemented using a `MediaSessionService`, allowing audio to continue when the app is minimized, the screen is locked, or the user navigates between screens. The app integrates with system-level media controls.

- **Service Communication:**  
  The `PlayerViewModel` sends playback commands to the `MusicService` via intents, maintaining a clean separation between the UI lifecycle and the playback lifecycle.

---

## Setup Instructions
- **IDE:** Android Studio Hedgehog (or newer)  
- **Minimum SDK:** 24 (Android 7.0)

### Steps to Run
1. Clone the repository.
2. Open the project in Android Studio.
3. Allow Gradle to sync and download dependencies.
4. Run the `:app` module on an emulator or physical device with an active internet connection.

---

## Assumptions & Trade-offs
- **API Limitations:**  
  The app uses a public community API for JioSaavn. Basic error handling is implemented to account for possible downtime, rate limits, or inconsistent responses.

- **Audio Quality:**  
  Playback is set to a fixed bitrate (typically **160 kbps**) to balance audio quality and network bandwidth usage for a smoother streaming experience.

- **Queue Simplicity:**  
  The current implementation uses a list-based in-memory queue. Persistent queue storage (e.g., Room) was intentionally omitted to keep the logic focused on real-time synchronization and playback correctness.

---

## Demo & APK
- **Video Demo:** https://drive.google.com/file/d/1mn5iexK3WuxjASeSqfsFRzDUZezcQ4Hb/view?usp=drivesdk  
- **Debug APK:** https://drive.google.com/file/d/1B25WdVxgRsdUM5NsYjGkV78aN2zbf5d1/view?usp=sharing
