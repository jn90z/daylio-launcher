# Daylio Command Launcher (prototype)

This is a separate Android prototype; the GitHub Pages launcher at repository root is unchanged.

## Current capabilities
- Import a UTF-8 text file containing a single allowlisted `am start` command.
- Validate that the requested component is Daylio's OverviewActivity.
- Try to launch the installed app through a normal Android Intent.
- Display success/error feedback.

## Not yet implemented
- **ADB transport/authentication**. Importing a shell command does not grant the app ADB privileges. This prototype parses the command and issues an ordinary Android activity intent, which Amazon Kids may still block.
- Amazon Appstore publication/approval. Sideloaded builds still may not appear on the child dashboard.

## Upstream research
[visnkmr/ADB-Remote-Keyboard-Shell](https://github.com/visnkmr/ADB-Remote-Keyboard-Shell) (Apache-2.0) uses the `com.cgutman.adblib` ADB library and `DeviceConnection.queueCommand`. The upstream code is not copied into this prototype. A future ADB backend requires an authorized ADB connection, a reliable secure transport, and testing on Fire OS 8.3.3.8.

## Build
Open `android-launcher` in Android Studio and build the `app` module with JDK 17 / Android Gradle Plugin 8.5.2. No APK has been validated yet.
