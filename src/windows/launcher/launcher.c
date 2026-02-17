/**
 * sss-image-viewer-launcher.exe
 *
 * Lightweight native Win32 launcher for SSS Image Viewer.
 *
 * 1. Reads %TEMP%\sss-image-viewer.port
 * 2. Tries to connect to localhost:<port> and send the file path (UTF-8)
 * 3. If that succeeds the viewer already has the request → exit
 * 4. If it fails, launches sss-image-viewer.exe in the same directory
 *
 * All filename handling uses wide (UTF-16) APIs so Unicode paths work correctly.
 * Built as a Windows-subsystem app (no console window).
 */

#define WIN32_LEAN_AND_MEAN
#define UNICODE
#define _UNICODE

#include <windows.h>
#include <winsock2.h>
#include <ws2tcpip.h>
#include <shellapi.h>
#include <shlwapi.h>

/* ---- helpers ------------------------------------------------------------ */

/** Strip trailing whitespace / CR / LF in-place. */
static void trimRight(char *s) {
    int len = lstrlenA(s);
    while (len > 0 && (s[len - 1] == ' ' || s[len - 1] == '\r' ||
                        s[len - 1] == '\n' || s[len - 1] == '\t')) {
        s[--len] = '\0';
    }
}

/**
 * Try to send a UTF-8 path to an already-running viewer instance.
 * Returns TRUE on success (caller should exit).
 */
static BOOL trySendToExisting(const char *utf8Msg) {
    /* Build port-file path: %TEMP%\sss-image-viewer.port */
    WCHAR tempDir[MAX_PATH];
    GetTempPathW(MAX_PATH, tempDir);

    WCHAR portFilePath[MAX_PATH];
    PathCombineW(portFilePath, tempDir, L"sss-image-viewer.port");

    /* Read port number */
    HANDLE hFile = CreateFileW(portFilePath, GENERIC_READ, FILE_SHARE_READ,
                               NULL, OPEN_EXISTING, 0, NULL);
    if (hFile == INVALID_HANDLE_VALUE)
        return FALSE;

    char fileBuf[64];
    DWORD bytesRead = 0;
    ReadFile(hFile, fileBuf, sizeof(fileBuf) - 1, &bytesRead, NULL);
    CloseHandle(hFile);
    fileBuf[bytesRead] = '\0';

    /* Parse "port\npid" format */
    int port = 0;
    DWORD pid = 0;
    char *p = fileBuf;

    /* Read port number */
    while (*p >= '0' && *p <= '9') {
        port = port * 10 + (*p - '0');
        p++;
    }
    if (port <= 0 || port > 65535)
        return FALSE;

    /* Skip newline/whitespace, then read PID */
    while (*p == '\r' || *p == '\n' || *p == ' ')
        p++;
    while (*p >= '0' && *p <= '9') {
        pid = pid * 10 + (*p - '0');
        p++;
    }

    /* Winsock init */
    WSADATA wsa;
    if (WSAStartup(MAKEWORD(2, 2), &wsa) != 0)
        return FALSE;

    /* Connect to loopback */
    SOCKET sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    if (sock == INVALID_SOCKET) {
        WSACleanup();
        return FALSE;
    }

    struct sockaddr_in addr;
    ZeroMemory(&addr, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_port   = htons((u_short)port);
    addr.sin_addr.s_addr = htonl(INADDR_LOOPBACK);

    if (connect(sock, (struct sockaddr *)&addr, sizeof(addr)) != 0) {
        closesocket(sock);
        WSACleanup();
        DeleteFileW(portFilePath);   /* stale port file */
        return FALSE;
    }

    /* Grant the viewer process permission to steal foreground */
    if (pid > 0) {
        AllowSetForegroundWindow(pid);
    }

    /* Send UTF-8 message + newline */
    send(sock, utf8Msg, lstrlenA(utf8Msg), 0);
    send(sock, "\n", 1, 0);

    closesocket(sock);
    WSACleanup();
    return TRUE;
}

/**
 * Launch sss-image-viewer.exe located next to this launcher.
 * Passes the original file path (wide) as the first argument.
 */
static void launchViewer(const WCHAR *fileArg) {
    /* Get this exe's directory */
    WCHAR exePath[MAX_PATH];
    GetModuleFileNameW(NULL, exePath, MAX_PATH);
    PathRemoveFileSpecW(exePath);
    PathAppendW(exePath, L"sss-image-viewer.exe");

    /* Build command line */
    WCHAR cmdLine[32768];
    if (fileArg && fileArg[0]) {
        wsprintfW(cmdLine, L"\"%s\" \"%s\"", exePath, fileArg);
    } else {
        wsprintfW(cmdLine, L"\"%s\"", exePath);
    }

    STARTUPINFOW si;
    ZeroMemory(&si, sizeof(si));
    si.cb = sizeof(si);

    PROCESS_INFORMATION pi;
    ZeroMemory(&pi, sizeof(pi));

    CreateProcessW(NULL, cmdLine, NULL, NULL, FALSE, 0, NULL, NULL, &si, &pi);
    if (pi.hThread)  CloseHandle(pi.hThread);
    if (pi.hProcess) CloseHandle(pi.hProcess);
}

/* ---- entry point -------------------------------------------------------- */

int WINAPI wWinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance,
                    LPWSTR lpCmdLine, int nCmdShow) {
    (void)hInstance;
    (void)hPrevInstance;
    (void)lpCmdLine;
    (void)nCmdShow;

    /* Parse command line (Unicode) */
    int argc = 0;
    LPWSTR *argv = CommandLineToArgvW(GetCommandLineW(), &argc);
    if (!argv)
        return 1;

    /* argv[0] = this exe, argv[1] = file path (if any) */
    const WCHAR *filePath = (argc > 1) ? argv[1] : NULL;

    /* Convert to UTF-8 for the socket protocol */
    char utf8Msg[32768];
    if (filePath) {
        int len = WideCharToMultiByte(CP_UTF8, 0, filePath, -1,
                                      utf8Msg, sizeof(utf8Msg), NULL, NULL);
        if (len <= 0) {
            /* Conversion failed — fall back to no-file */
            lstrcpyA(utf8Msg, "__NO_FILE__");
        }
    } else {
        lstrcpyA(utf8Msg, "__NO_FILE__");
    }

    if (!trySendToExisting(utf8Msg)) {
        launchViewer(filePath);
    }

    LocalFree(argv);
    return 0;
}
