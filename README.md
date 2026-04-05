# SSS Image Viewer

A fast, lightweight image viewer built with JavaFX.

JavaFX with AOT compilation enables fast startup, making it well suited for an image viewer.

## Features

- Image viewing with rotation support
- Zoom in/out with the mouse wheel
- Window positioning by dragging
- Frameless and fullscreen modes
- Navigate between images in a directory
- Mark images for slideshow and grid view
- Copy image to clipboard (Ctrl+C)
- System tray resident mode

## Supported Image Formats

Set this app as the default image viewer to enjoy fast image browsing.

- **GIF** - Full support
- **PNG** - Full support
- **JPEG/JPG** - Full support
- **JFIF** - Full support
- **BMP** - Limited support (some variants may not display correctly)

## Usage

### Keyboard Shortcuts

- **Left/Right Arrow Keys** - Navigate between images
- **Up/Down Arrow Keys** - Rotate image (90 increments)
- **Space** - Mark/unmark image
- **S** - Toggle slideshow mode
- **G** - Show marked images in grid view
- **H** - Show help
- **Enter** - Toggle window frame
- **D** - Duplicate window
- **F11** - Toggle fullscreen
- **Ctrl+C** - Copy image to clipboard
- **Ctrl+Q** - Quit application
- **Esc** - Exit fullscreen or close window
- **Delete** - Move current image to trash

### Mouse Controls

- **Click and drag** - Move window (windowed) / Pan image (fullscreen)
- **Scroll wheel** - Zoom in/out
- **Double-click** - Toggle fullscreen

## Build Requirements

- Java 25
- Maven
- JavaFX 25.0.2

## Building and Running

```bash
# Compile
mvn clean compile

# Run
mvn javafx:run
```

## Windows AOT Build

```bash
mvn clean package
build-with-aot.bat
```

## Win32 Launcher

Build the launcher on Developer Command Prompt for VS:

```bash
build-launchar.bat
```

This generates `target/launcher/sss-image-viewer-launcher.exe`. Place it in the same directory as `sss-image-viewer.exe` and associate it with image file types.
