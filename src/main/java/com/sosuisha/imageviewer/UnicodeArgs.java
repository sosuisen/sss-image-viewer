package com.sosuisha.imageviewer;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.charset.StandardCharsets;

/**
 * Windows の GetCommandLineW + CommandLineToArgvW を FFM で呼び出し、
 * Unicode のコマンドライン引数を正しく取得する。
 *
 * JDK の Windows ランチャーが GetCommandLineA (ANSI版) を使用しているため、
 * ANSI コードページに含まれない Unicode 文字が化ける問題のワークアラウンド。
 */
class UnicodeArgs {

    static String[] getArgsFromCommandLineW() throws Throwable {
        Linker linker = Linker.nativeLinker();

        SymbolLookup kernel32 = SymbolLookup.libraryLookup("kernel32", Arena.global());
        SymbolLookup shell32 = SymbolLookup.libraryLookup("shell32", Arena.global());

        // LPWSTR GetCommandLineW(void)
        MethodHandle getCommandLineW = linker.downcallHandle(
                kernel32.find("GetCommandLineW").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS)
        );

        // LPWSTR* CommandLineToArgvW(LPCWSTR lpCmdLine, int *pNumArgs)
        MethodHandle commandLineToArgvW = linker.downcallHandle(
                shell32.find("CommandLineToArgvW").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS)
        );

        // HLOCAL LocalFree(HLOCAL hMem)
        MethodHandle localFree = linker.downcallHandle(
                kernel32.find("LocalFree").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS)
        );

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment cmdLine = (MemorySegment) getCommandLineW.invokeExact();

            MemorySegment pArgc = arena.allocate(ValueLayout.JAVA_INT);
            MemorySegment argv = (MemorySegment) commandLineToArgvW.invokeExact(cmdLine, pArgc);

            int argc = pArgc.get(ValueLayout.JAVA_INT, 0);
            argv = argv.reinterpret((long) argc * ValueLayout.ADDRESS.byteSize());

            String[] args = new String[argc];
            for (int i = 0; i < argc; i++) {
                MemorySegment ptr = argv.getAtIndex(ValueLayout.ADDRESS, i)
                        .reinterpret(Long.MAX_VALUE);
                args[i] = readUtf16NullTerminated(ptr);
            }

            var _ = (MemorySegment) localFree.invokeExact(argv);
            return args;
        }
    }

    private static String readUtf16NullTerminated(MemorySegment ptr) {
        int charCount = 0;
        while (ptr.get(ValueLayout.JAVA_SHORT, (long) charCount * 2) != 0) {
            charCount++;
        }
        byte[] bytes = ptr.asSlice(0, (long) charCount * 2)
                .toArray(ValueLayout.JAVA_BYTE);
        return new String(bytes, StandardCharsets.UTF_16LE);
    }

    /**
     * Windows ではファイル関連付け経由の引数が化けるため、
     * GetCommandLineW から取得した引数を使う。
     * args[0] は実行コマンド自体なのでスキップする。
     */
    static String[] resolveArgs(String[] mainArgs) throws Throwable {
        if (!System.getProperty("os.name", "").toLowerCase().contains("win")) {
            return mainArgs;
        }
        String[] all = getArgsFromCommandLineW();
        // args[0] = "java" or exe path, 以降に -jar, jar名, 実引数... と続く
        // 末尾の引数がユーザー指定のファイルパス
        // mainArgs.length 分だけ末尾から取る
        int offset = all.length - mainArgs.length;
        String[] resolved = new String[mainArgs.length];
        System.arraycopy(all, offset, resolved, 0, mainArgs.length);
        return resolved;
    }
}
