@echo off
echo Building SSS Image Viewer Native Image (Second Half)...
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

echo Build process completed!
echo Native executable location: target\gluonfx\x86_64-windows\sss-image-viewer.exe
pause