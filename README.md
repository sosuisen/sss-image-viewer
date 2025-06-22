# SSS Image Viewer

A JavaFX-based quick image viewer.

One of the advantages of JavaFX is that it allows for the creation of fast-starting applications by generating native images. This feature makes it particularly suitable for developing image viewers.

## Features

- Image viewing with rotation support
- Easy image scaling using the mouse wheel
- Simple image layout by dragging the window
- No frame mode
- Fullscreen mode
- Navigate between images in a directory
- Slideshow feature
- Copy image to clipboard (Ctrl+C)

## Supported Image Formats

If you set this app as the default for opening image files, you can enjoy a fast image viewer.

The app supports the following image formats:

- **GIF** - Full support
- **PNG** - Full support
- **JPEG/JPG** - Full support
- **JFIF** - Full support
- **BMP** - ⚠️ **Limited support** - Some BMP variants may not display correctly

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

## Build Requirements

- Java 23
- Maven
- JavaFX 24.0.1

- GraalVM for Native Image
- mt.exe for UTF-8 filename

## Building and Running

```bash
# Compile
mvn clean compile

# Run
mvn javafx:run

# Build JAR
mvn clean package
```
## Native Image Support

The project includes GraalVM native image configuration for creating native executables.

### Windows Code Page Handling

When building native images on Windows, special handling is required for Unicode file names:

1. Before creating the GraalVM Native Image, run `utf_registry.bat`.
2. After creating the native image, run `shiftjis_registry.bat` (If your Windows is Japanese Code Page.)
3. Embed manifest file into the .exe using the `mt.exe` command.

`build_native_image.bat` is a batch file that automates this process.
