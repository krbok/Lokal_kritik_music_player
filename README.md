🎵 Lokal Music Player
Project Overview
Lokal Music Player is a modern Android music streaming application built using Jetpack Compose and Kotlin. The app consumes the JioSaavn public API to fetch real-time music data and focuses on providing a seamless playback experience with a persistent Mini Player and a dedicated Full Player screen that remain fully synchronized across navigation.
Tech Stack
Language: Kotlin
UI Framework: Jetpack Compose (100%)
Architecture: MVVM (Model–View–ViewModel)
State Management: StateFlow / MutableStateFlow
Dependency Injection: Hilt
Media Playback: Media3 ExoPlayer & MediaSession
Networking: Retrofit & Gson
Image Loading: Coil
Asynchronous Programming: Kotlin Coroutines & Flow
Architecture & State Management
The application follows a clean MVVM architecture with a clear separation of concerns between UI, state, and playback logic.
Single Source of Truth (SSOT):
PlayerViewModel acts as the central state holder for playback-related UI state. It exposes immutable StateFlow objects such as currentSong, isPlaying, and progress, ensuring consistent state across the app.
UI Synchronization:
Both the Mini Player (persistent bottom component) and the Full Player screen observe the same PlayerViewModel instance. By sharing the ViewModel using Hilt (activity-scoped) or Navigation, any playback state change is immediately reflected across all UI components, ensuring both players always show the same song, play/pause state, and progress.
Unidirectional Data Flow:
User actions (play, pause, seek, next/previous) are dispatched from the UI to the ViewModel, which then communicates with the MusicService. This keeps UI logic simple and predictable while isolating playback concerns.
Media Playback
Media3 ExoPlayer:
Used as the core audio playback engine for reliable and efficient streaming of remote audio URLs provided by the JioSaavn API.
Background Playback:
Playback is implemented using a MediaSessionService, allowing audio to continue when the app is minimized, the screen is locked, or the user navigates between screens. The app also integrates with system-level media controls.
Service Communication:
The PlayerViewModel sends playback commands to the MusicService via intents, maintaining a clean separation between the UI lifecycle and the playback lifecycle.
Assumptions & Trade-offs
•
API Limitations: The app uses a public community API for JioSaavn. To ensure stability during review, basic error handling is implemented for API rate limits or downtime.
•
Audio Quality: Playback is set to a fixed bitrate (typically 160kbps) to balance audio quality and network bandwidth usage for a smoother streaming experience.
•
Queue Simplicity: The current implementation uses a List-based queue in memory. Persistent queue storage (e.g., Room) was omitted to keep the logic focused on real-time synchronization and playback correctness.
Demo & APK
•
Video Demo: [Link to Video/Drive]
•
Debug APK: [Link to APK File]
