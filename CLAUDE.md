# About Journal

Always write and update your development journal in Japanese. The file should be:
journal/yyyy-mm-dd_hhmm.md

The content should be as follows:
```
# yyyy-mm-dd hh:mm

# 作業内容
* What you did
* What problems occurred
* How you solved them

# 学んだこと
  Write what you learned

# 現在の課題
  Write about any current problems you're facing

# 感想
  Other thoughts and current feelings
```
# SSS Image Viewer - Project Memo

## Project Overview

A JavaFX-based image viewer application.

This app aims to leverage JavaFX's execution speed.

One of the advantages of JavaFX is that it allows for the creation of fast-starting applications by generating native images. This feature makes it particularly suitable for developing image viewers.

## Architecture
- **Main Application**: `App.java` - Entry point that handles file path arguments and launches appropriate windows
- **Core Components**:
  - `ImageViewerWindow.java` - Main image viewing interface
  - `ImageNavigator.java` - Handles navigation and slideshow logic
  - `DragAndDropWindow.java` - Handles drag-and-drop functionality when no file is specified
  - `ImageUtil.java` - Image processing utilities

## Usage

- **Left/Right Arrow Keys** - Navigate between images
- **Up/Down Arrow Keys** - Rotate image (90° increments)
- **Space** - Mark/unmark image for slideshow
- **S** - Toggle slideshow mode
- **Enter** - Toggle window frame
- **D** - Duplicate window
- **F11** - Toggle fullscreen
- **Ctrl+C** - Copy image to clipboard
- **Esc** - Exit fullscreen or close window
- **Double-click** - Toggle fullscreen
- **Ctrl+C** - Copy image to clipboard

- **Click and drag** - Move window (in windowed mode) or pan image (in fullscreen)
- **Scroll wheel** - Zoom in/out
- **Double-click** - Toggle fullscreen

## Minor convenience features
- The image display size is maintained individually by Rotation (Landscape or Portrait).
- When there is a problem reading Unicode characters in filenames, the system attempts to reload by having the user input the filename. If the load is successful, a dialog is also displayed that allows changing the unreadable Unicode filename to a different name.


## Build Configurationhttps://bsky.app/notifications
- **Build Tool**: Maven
- **Java Version**: 23
- **JavaFX Version**: 24.0.1
- **Main Class**: `com.sosuisha.imageviewer.App`

## Build Commands
- `mvn clean compile` - Compile the project
- `mvn javafx:run` - Run the application
- `mvn clean package` - Build JAR package

## File Structure
- `src/main/java/com/sosuisha/imageviewer/` - Main source code
- `src/main/java/com/sosuisha/imageviewer/jfxbuilder/` - JavaFX builder utilities
- `src/main/resources/` - Resources and native image configuration
- `agent-data/` - GraalVM native image metadata
- `src/windows/assets/` - Windows-specific assets (icon)

## Native Image Support
The project includes GraalVM native image configuration files for creating native executables.

## Development Notes
- Uses custom JavaFX builder pattern for UI components
- Includes Windows manifest file for proper OS integration


## Current issues

### ImageIO support

- GraalVM's native image support for ImageIO appears to still be insufficient.
- As a result, even when creating native images with JavaFX 24 in GraalVM, it cannot support TwelveMonkeys.

### Windows Code Page Issue
When building native images on Windows, file names passed by the OS use the default code page (not UTF-8), causing Unicode character issues. To resolve this:

1. Before creating GraalVM Native Image: Run `utf_registry.bat` to change default code page to UTF-8
2. After creating the native image: Run `shiftjis_registry.bat` to return to localized code page. This file is designed for the Japanese Windows codepage, so if you are using Windows in other languages, you need to replace 932 with another number.
3. Embed manifest file into the .exe using `mt.exe` command

### Property Subscribe Method May Not Be Supported
GraalVM Native Image may not support JavaFX property `subscribe()` method. This method provides easier subscription/unsubscription management compared to `addListener()`/`removeListener()`, but should be avoided in native image builds to ensure compatibility. Use the traditional listener pattern instead, ensuring proper cleanup by storing listener references for removal to prevent memory leaks.

