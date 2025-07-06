@echo off
cd target/package

echo Generating CDS class list...
..\image\bin\java -XX:DumpLoadedClassList=app.classlist -Dcds.training.mode=true -jar sss-image-viewer-1.0.5.jar

echo Creating CDS archive...
..\image\bin\java -Xshare:dump -XX:SharedClassListFile=app.classlist -XX:SharedArchiveFile=app.jsa

echo CDS archive created: app.jsa
dir app.jsa