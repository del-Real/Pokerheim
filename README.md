# Pokerheim

## How to run the game
The game can be installed on a smartphone (Android version 5 or higher) or on an emulator through Android Studio. In order to compile the game you can:

1. Open the project in Android Studio.
2. Let gradle build the app.
3. Choose the device (emulator or android smartphone connected via USB cable)
4. Press the run button

Now the game should be installed and should run without issues.
Otherwise its possible to build the .apk and install it on the phone. To do this:

1. Open the project in Android Studio.
2. Click the Build tab at the top of the window.  
3. Then click Build Bundle(s) / APK(s) → Build APK(s)  
4. Find the compiled .apk file in Pokerheim/android/build/outputs/apk/debug.
5. Move the .apk file to the phone and install it.

Now the game should run without issues.

## Project Structure

- **`android/`**  
  Contains the `AndroidFirestoreListener` class, which listens to Firestore updates.  
  This setup is required because Firebase integration through Gradle expects certain components to be located in the Android module.

- **`core/`**  
  Holds the client-side logic and follows the Model-View-Controller (MVC) architecture:
    - **Model**: Data models and business logic
    - **View**: UI components. Includes a `ScreenStates/` subfolder with the various screen definitions
    - **Controller**: Handles interaction between the View and Model

- **`backend/`**  
  Contains the server-side Firebase Cloud Functions that power the game logic and interactions.


For a more detailed description of each class in the project look up the implementation report




YouTube video: https://www.youtube.com/watch?v=npO53ToXruA

---

# Pokerheim

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a template including simple application launchers and an `ApplicationAdapter` extension that draws libGDX logo.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `android`: Android mobile platform. Needs Android SDK.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `android:lint`: performs Android project validation.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.
