@echo off

cd target/package

echo Generating AOT cache...
..\image\bin\java.exe "-XX:AOTCacheOutput=app.aot" -jar .\sss-image-viewer-1.0.6.jar ..\..\images\sample.png

if exist app.aot (
    echo.
    echo AOT cache created successfully!
    dir app.aot
    
) else (
    echo Failed to create AOT cache
)
cd ../../


echo Cleaning existing jpackage output...
rmdir /s /q target\jpackage

echo Creating final package...
mvn jpackage:jpackage

echo Build complete with AOT cache!