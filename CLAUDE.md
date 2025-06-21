# SSS Image Viewer - Project Memo

## Project Overview
A JavaFX-based image viewer application built with Java 23 and JavaFX 24.0.1. The application provides image viewing capabilities with navigation, rotation, slideshow features, and drag-and-drop functionality.

## Architecture
- **Main Application**: `App.java` - Entry point that handles file path arguments and launches appropriate windows
- **Core Components**:
  - `ImageViewerWindow.java` - Main image viewing interface
  - `ImageNavigator.java` - Handles navigation and slideshow logic
  - `DragAndDropWindow.java` - Handles drag-and-drop functionality when no file is specified
  - `ImageUtil.java` - Image processing utilities

## Key Features
- Image viewing with rotation support (with rotation memory)
- Navigation between images in a directory
- Slideshow functionality
- Drag-and-drop support
- Fullscreen mode
- File renaming capability

## Build Configuration
- **Build Tool**: Maven
- **Java Version**: 23
- **JavaFX Version**: 24.0.1
- **Main Class**: `com.sosuisha.imageviewer.App`

## Build Commands
- `mvn clean compile` - Compile the project
- `mvn javafx:run` - Run the application
- `mvn clean package` - Build JAR package

## Recent Changes (from git history)
- Fixed fullscreen check when navigating next/prev
- Refactored navigation and slideshow logic into `ImageNavigator` class
- Implemented image-based rotation with rotation memory
- Applied saved rotation when loading images

## File Structure
- `src/main/java/com/sosuisha/imageviewer/` - Main source code
- `src/main/java/com/sosuisha/imageviewer/jfxbuilder/` - JavaFX builder utilities
- `src/main/resources/` - Resources and native image configuration
- `agent-data/` - GraalVM native image metadata
- `src/windows/assets/` - Windows-specific assets (icon)

## Native Image Support
The project includes GraalVM native image configuration files for creating native executables.

### Windows Code Page Issue
When building native images on Windows, file names passed by the OS use the default code page (not UTF-8), causing Unicode character issues. To resolve this:

1. Before creating GraalVM Native Image: Run `utf_registry.bat` to change default code page to UTF-8
2. After creating the native image: Run `shiftjis_registry.bat` to return to localized code page
3. Embed manifest file into the .exe using `mt.exe` command

## Development Notes
- Uses custom JavaFX builder pattern for UI components
- Includes Windows manifest file for proper OS integration