@echo off

cd target/package

echo Generating CDS archive...
..\image\bin\java -Xshare:dump

echo Generating CDS class list...
..\image\bin\java --enable-native-access=ALL-UNNAMED -XX:ArchiveClassesAtExit=app.jsa -jar sss-image-viewer-1.0.6.jar "..\..\images\sample.png"

if exist app.jsa (
    echo.
    echo Dynamic CDS archive created successfully!
    dir app.jsa
    
) else (
    echo Failed to create Dynamic CDS archive
)
cd ../../


echo Cleaning existing jpackage output...
rmdir /s /q target\jpackage

echo Creating final package...
mvn jpackage:jpackage

echo Build complete with CDS!