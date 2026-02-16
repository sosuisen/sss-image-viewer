package com.sosuisha.imageviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Ensures only one instance of the application runs at a time.
 * Uses a localhost socket + port file for IPC.
 */
class SingleInstanceService {

    static final String NO_FILE = "__NO_FILE__";

    private static final Path PORT_FILE = Path.of(
            System.getProperty("java.io.tmpdir"), "sss-image-viewer.port");

    private ServerSocket serverSocket;

    /**
     * Tries to send a file path to an already-running instance.
     *
     * @return true if the message was sent successfully (caller should exit)
     */
    static boolean trySendToExisting(String filePath) {
        if (!Files.exists(PORT_FILE)) {
            return false;
        }

        int port;
        try {
            port = Integer.parseInt(Files.readString(PORT_FILE).trim());
        } catch (IOException | NumberFormatException e) {
            deletePortFile();
            return false;
        }

        try (Socket socket = new Socket(InetAddress.getLoopbackAddress(), port);
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            out.println(filePath != null ? filePath : NO_FILE);
            return true;
        } catch (IOException e) {
            // Existing instance is gone — stale port file
            deletePortFile();
            return false;
        }
    }

    /**
     * Starts the server socket on a random port, writes the port file,
     * and listens for incoming connections on daemon threads.
     *
     * @param onFileReceived callback invoked with the received file path
     *                       (or null if __NO_FILE__ was sent)
     */
    void startServer(Consumer<String> onFileReceived) throws IOException {
        serverSocket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress());
        Files.writeString(PORT_FILE, String.valueOf(serverSocket.getLocalPort()));

        Thread acceptThread = new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    Socket client = serverSocket.accept();
                    Thread handler = new Thread(() -> handleClient(client, onFileReceived));
                    handler.setDaemon(true);
                    handler.start();
                } catch (IOException e) {
                    // ServerSocket closed — exit loop
                    break;
                }
            }
        });
        acceptThread.setDaemon(true);
        acceptThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(SingleInstanceService::deletePortFile));
    }

    /**
     * Stops the server and deletes the port file.
     */
    void stopServer() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }
        }
        deletePortFile();
    }

    private static void handleClient(Socket client, Consumer<String> onFileReceived) {
        try (client;
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8))) {
            String line = in.readLine();
            if (line != null) {
                onFileReceived.accept(NO_FILE.equals(line) ? null : line);
            }
        } catch (IOException ignored) {
        }
    }

    private static void deletePortFile() {
        try {
            Files.deleteIfExists(PORT_FILE);
        } catch (IOException ignored) {
        }
    }
}
