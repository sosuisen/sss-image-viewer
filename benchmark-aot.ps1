Set-Location target\package

# 最初の１回は捨てる
Start-Process -Wait ..\image\bin\java.exe -ArgumentList '"-Dauto.exit=true"', '-jar', '.\sss-image-viewer-1.0.6.jar'
# === No CDS ===
Write-Host "=== No AOT ==="
1..10 | ForEach-Object {
    (Measure-Command {
        Start-Process -Wait ..\image\bin\java.exe -ArgumentList '"-Dauto.exit=true"', '-jar', '.\sss-image-viewer-1.0.6.jar'
    }).TotalMilliseconds
} | Measure-Object -Average -Minimum -Maximum | Format-List

# === With CDS ===
Write-Host "=== With AOT ==="
Start-Process -Wait ..\image\bin\java.exe -ArgumentList '"-Dauto.exit=true"', '"-XX:AOTCache=app.aot"', '-jar', '.\sss-image-viewer-1.0.6.jar'
1..10 | ForEach-Object {
    (Measure-Command {
        Start-Process -Wait ..\image\bin\java.exe -ArgumentList '"-Dauto.exit=true"', '"-XX:AOTCache=app.aot"', '-jar', '.\sss-image-viewer-1.0.6.jar'
    }).TotalMilliseconds
} | Measure-Object -Average -Minimum -Maximum | Format-List

Set-Location ..\..