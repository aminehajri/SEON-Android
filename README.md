This SDK provides the following features:

1. **startCamera** - Initializes the camera and starts a live preview for capturing images.
2. **takePhoto** - Captures a photo and saves it to the device storage.
3. **accessPhoto** - Retrieves the captured photo from storage.
4. **authenticateUser** - Uses the device's built-in biometric authentication to verify the user.

> **Note:** The `.aar` file is included in the same folder of this repository. You can directly add it to your project.

## Project Structure

The project is divided into two parts:

1. **CameraLibrary** - The core SDK providing camera and biometric functionalities.
2. **Demo App** - A sample application demonstrating how to integrate and use the `CameraLibrary` in an Android app.

### Demo App

The demo app is a simple Android application that showcases how to use the CameraLibrary features:

- Start and display the camera preview using `startCamera`.
- Capture and save a photo using `takePhoto`.
- Retrieve and display the app saved photos on the device `accessPhotos`.
- Authenticate a user using the device's biometrics with `authenticateUser`.






