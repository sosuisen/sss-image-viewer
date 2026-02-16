Set-Location target/package

# 最初の１回は捨てる
Start-Process -Wait ..\image\bin\java.exe -ArgumentList '"-Dauto.exit=true"', '-jar', '.\sss-image-viewer-1.0.6.jar', '..\..\images\sample.png'
# === No CDS ===
Write-Host "=== No CDS ==="
1..10 | ForEach-Object {
    (Measure-Command {
        Start-Process -Wait ..\image\bin\java.exe -ArgumentList '"-Dauto.exit=true"', '-jar', '.\sss-image-viewer-1.0.6.jar', '..\..\images\sample.png'
    }).TotalMilliseconds
} | Measure-Object -Average -Minimum -Maximum | Format-List

# === With CDS ===
Write-Host "=== With CDS ==="
Start-Process -Wait ..\image\bin\java.exe -ArgumentList '"-Dauto.exit=true"', '"-XX:SharedArchiveFile=app.jsa"', '-jar', '.\sss-image-viewer-1.0.6.jar', '..\..\images\sample.png'
1..10 | ForEach-Object {
    (Measure-Command {
        Start-Process -Wait ..\image\bin\java.exe -ArgumentList '"-Dauto.exit=true"', '"-XX:SharedArchiveFile=app.jsa"', '-jar', '.\sss-image-viewer-1.0.6.jar', '..\..\images\sample.png'
    }).TotalMilliseconds
} | Measure-Object -Average -Minimum -Maximum | Format-List
