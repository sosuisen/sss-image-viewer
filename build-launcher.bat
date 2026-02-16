@echo off
setlocal

REM ============================================================
REM  Build sss-image-viewer-launcher.exe
REM
REM  Usage:
REM    build-launcher.bat          (auto-detect compiler)
REM    build-launcher.bat msvc     (force MSVC cl.exe)
REM    build-launcher.bat gcc      (force MinGW gcc)
REM ============================================================

set "SRC=%~dp0src\windows\launcher\launcher.c"
set "OUTDIR=%~dp0target\launcher"
set "OUT=%OUTDIR%\sss-image-viewer-launcher.exe"
set "FORCE=%~1"

if not exist "%OUTDIR%" mkdir "%OUTDIR%"

REM --- Try MSVC first ---
if /I "%FORCE%"=="gcc" goto :try_gcc

where cl >nul 2>&1
if %ERRORLEVEL%==0 (
    echo [*] Building with MSVC ...
    cl /O2 /W4 /DUNICODE /D_UNICODE /Fe:"%OUT%" "%SRC%" ^
        /link /SUBSYSTEM:WINDOWS ws2_32.lib shell32.lib shlwapi.lib user32.lib
    if %ERRORLEVEL%==0 (
        echo [OK] %OUT%
        REM Clean up MSVC intermediate files
        del /q "%OUTDIR%\launcher.obj" 2>nul
        goto :done
    )
    echo [!] MSVC build failed.
)
if /I "%FORCE%"=="msvc" goto :fail

:try_gcc
where gcc >nul 2>&1
if %ERRORLEVEL%==0 (
    echo [*] Building with MinGW gcc ...
    gcc -O2 -municode -mwindows -Wall -o "%OUT%" "%SRC%" ^
        -lws2_32 -lshlwapi -lshell32
    if %ERRORLEVEL%==0 (
        echo [OK] %OUT%
        goto :done
    )
    echo [!] gcc build failed.
)

:fail
echo [ERROR] No C compiler found. Install MSVC (Visual Studio) or MinGW-w64.
exit /b 1

:done
endlocal
