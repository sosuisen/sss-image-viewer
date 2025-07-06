@echo off
cd target/package

echo Running with CDS...
..\image\bin\java -XX:SharedArchiveFile=app.jsa -jar sss-image-viewer-1.0.5.jar