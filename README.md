# SSS Image Viewer

A JavaFX-based image viewer application built with Java 23 and JavaFX 24.0.1.

## Features

- Image viewing with rotation support (with rotation memory)
- Navigation between images in a directory
- Slideshow functionality
- Drag-and-drop support
- Fullscreen mode
- File renaming capability
- Copy image to clipboard (Ctrl+C)

## Supported Image Formats

The application supports the following image formats through JavaFX native image loading:

- **GIF** - Full support
- **PNG** - Full support
- **JPEG/JPG** - Full support
- **JFIF** - Full support
- **BMP** - ⚠️ **Limited support** - Some BMP variants may not display correctly

### BMP Format Limitation

**Important:** JavaFX's native image loading has limited BMP support. Some BMP files may fail to load or display as 0x0 dimensions due to:

- Unsupported BMP compression methods
- Unusual color depths or bit formats
- Specific BMP variants not supported by JavaFX

If you encounter BMP files that don't display, this is a known limitation of JavaFX's Image class.

## Build Requirements

- Java 23
- Maven
- JavaFX 24.0.1

## Building and Running

```bash
# Compile
mvn clean compile

# Run
mvn javafx:run

# Build JAR
mvn clean package
```

## Usage

### Keyboard Shortcuts

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

### Mouse Controls

- **Click and drag** - Move window (in windowed mode) or pan image (in fullscreen)
- **Scroll wheel** - Zoom in/out
- **Double-click** - Toggle fullscreen

## Native Image Support

The project includes GraalVM native image configuration for creating native executables.

### Windows Code Page Handling

When building native images on Windows, special handling is required for Unicode file names:

1. Before creating GraalVM Native Image: Run `utf_registry.bat`
2. After creating the native image: Run `shiftjis_registry.bat`
3. Embed manifest file into the .exe using `mt.exe` command

## Development

For more details about the project structure and development notes, see `CLAUDE.md`.