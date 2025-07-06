@echo off

cd target/package

..\image\bin\java -Xshare:dump

..\image\bin\java -XX:ArchiveClassesAtExit=app.jsa -Dcds.training.mode=true -jar sss-image-viewer-1.0.5.jar ../../images/sample.png

if exist app.jsa (
    echo.
    echo Dynamic CDS archive created successfully!
    dir app.jsa
    
    echo.
    echo Testing with Dynamic CDS...
    ..\image\bin\java -XX:SharedArchiveFile=app.jsa -jar sss-image-viewer-1.0.5.jar
) else (
    echo Failed to create Dynamic CDS archive
)
cd ../../


echo Cleaning existing jpackage output...
rmdir /s /q target\jpackage

echo Creating final package...
mvn jpackage:jpackage

echo Build complete with CDS!