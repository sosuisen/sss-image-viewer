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
set "RC=%~dp0src\windows\launcher\launcher.rc"
set "RCDIR=%~dp0src\windows\launcher"
set "OUTDIR=%~dp0target\launcher"
set "OUT=%OUTDIR%\sss-image-viewer-launcher.exe"
set "FORCE=%~1"

if not exist "%OUTDIR%" mkdir "%OUTDIR%"

if /I "%FORCE%"=="gcc" goto try_gcc

REM --- Try MSVC first ---
where cl >nul 2>&1
if errorlevel 1 goto msvc_unavailable

echo [*] Building with MSVC ...
echo [*] Compiling resources (icon) ...
rc /nologo /fo "%OUTDIR%\launcher.res" "%RC%"
if errorlevel 1 goto fail_rc

cl /O2 /W4 /DUNICODE /D_UNICODE /Fe:"%OUT%" "%SRC%" "%OUTDIR%\launcher.res" /link /SUBSYSTEM:WINDOWS ws2_32.lib shell32.lib shlwapi.lib user32.lib
if errorlevel 1 goto msvc_failed

echo [OK] %OUT%
REM Clean up MSVC intermediate files
del /q "%OUTDIR%\launcher.obj" 2>nul
del /q "%OUTDIR%\launcher.res" 2>nul
goto done

:msvc_failed
echo [!] MSVC build failed.
:msvc_unavailable
if /I "%FORCE%"=="msvc" goto fail

:try_gcc
where gcc >nul 2>&1
if errorlevel 1 goto fail

echo [*] Building with MinGW gcc ...
echo [*] Compiling resources (icon) ...
windres "%RC%" -O coff -o "%OUTDIR%\launcher_res.o" --include-dir "%RCDIR%"
if errorlevel 1 goto fail_windres

gcc -O2 -municode -mwindows -Wall -o "%OUT%" "%SRC%" "%OUTDIR%\launcher_res.o" -lws2_32 -lshlwapi -lshell32
if errorlevel 1 goto gcc_failed

echo [OK] %OUT%
del /q "%OUTDIR%\launcher_res.o" 2>nul
goto done

:gcc_failed
echo [!] gcc build failed.
goto fail

:fail_rc
echo [ERROR] rc.exe failed to compile resources.
exit /b 1

:fail_windres
echo [ERROR] windres failed to compile resources.
exit /b 1

:fail
echo [ERROR] No C compiler found. Install MSVC (Visual Studio) or MinGW-w64.
exit /b 1

:done
endlocal
