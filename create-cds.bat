@echo off
cd target/package

echo Generating CDS archive...
..\image\bin\java -Xshare:dump

echo Generating CDS class list...
..\image\bin\java --enable-native-access=ALL-UNNAMED -XX:ArchiveClassesAtExit=app.jsa -jar sss-image-viewer-1.0.6.jar "..\..\images\sample.png"

echo CDS archive created: app.jsa
dir app.jsa