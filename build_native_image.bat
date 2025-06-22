@echo off
echo Execute this script on Developper Command Prompt for VS2022 with administrative privileges.
echo.
echo Building SSS Image Viewer Native Image (First Half)...
echo.

echo Step 0: Setting up environment for Visual Studio...
call "C:\Program Files\Microsoft Visual Studio\2022\Community\VC\Auxiliary\Build\vcvars64.bat"

echo Step 1: Setting UTF-8 code page for Unicode file support...
call utf_registry.bat
if %errorlevel% neq 0 (
    echo ERROR: Failed to set UTF-8 code page
    exit /b %errorlevel%
)
echo UTF-8 code page set successfully.
echo.

echo Step 2: Building native image with GluonFX...
call mvn clean gluonfx:build
set BUILD_RESULT=%errorlevel%
echo.
echo Maven build finished with exit code: %BUILD_RESULT%
echo.

echo Checking build result...
if not exist "target\gluonfx\x86_64-windows\sss-image-viewer.exe" (
    echo ERROR: Failed to build native image - executable not found
    echo Maven exit code: %BUILD_RESULT%
    echo Restoring original code page...
    call shiftjis_registry.bat
    exit /b 1
)

echo.
echo ===================================
echo Native image build completed successfully!
echo ===================================
echo Executable found at: target\gluonfx\x86_64-windows\sss-image-viewer.exe
echo.

echo Step 3: Restoring original code page...
call shiftjis_registry.bat
if %errorlevel% neq 0 (
    echo WARNING: Failed to restore original code page
)
echo Original code page restored.
echo.

echo Step 4: Embedding manifest into executable...
mt -manifest sss-image-viewer.exe.manifest -outputresource:target\gluonfx\x86_64-windows\sss-image-viewer.exe;1
if %errorlevel% neq 0 (
    echo WARNING: Failed to embed manifest into executable
    echo The executable will still work but may not have proper OS integration
) else (
    echo Manifest embedded successfully.
)
echo.

echo ===================================
echo Build process completed!
echo ===================================
echo Native executable location: target\gluonfx\x86_64-windows\sss-image-viewer.exe
pause
