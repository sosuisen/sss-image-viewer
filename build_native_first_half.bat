@echo off
echo Building SSS Image Viewer Native Image (First Half)...
echo.

echo Step 1: Setting UTF-8 code page for Unicode file support...
call utf_registry.bat
if %errorlevel% neq 0 (
    echo ERROR: Failed to set UTF-8 code page
    exit /b %errorlevel%
)
echo UTF-8 code page set successfully.
echo.

echo Step 2: Building native image with GluonFX...
mvn clean gluonfx:build
if %errorlevel% neq 0 (
    echo ERROR: Failed to build native image
    echo Restoring original code page...
    call shiftjis_registry.bat
    exit /b %errorlevel%
)
echo Native image build completed successfully.
echo.
