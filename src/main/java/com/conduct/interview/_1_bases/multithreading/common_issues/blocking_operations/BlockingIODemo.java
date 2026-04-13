package com.conduct.interview._1_bases.multithreading.common_issues.blocking_operations;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;

public class BlockingIODemo {

    public static void main(String[] args) throws IOException {

        System.out.println("Starting Blocking Server on port 8088...");

        try (ServerSocket serverSocket = new ServerSocket(8088)) {

            while (true) {
                System.out.println("[" + LocalTime.now() + "] Waiting for client connection...");

                // This line BLOCKS the thread until a client connects
                Socket clientSocket = serverSocket.accept();

                System.out.println("[" + LocalTime.now() + "] Client connected! Starting to handle...");

                // Handle client in a separate thread (still blocking inside)
                new Thread(() -> handleClient(clientSocket)).start();
            }
        }
    }

    private static void handleClient(Socket socket) {
        String clientName = socket.getInetAddress().getHostAddress();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("[" + LocalTime.now() + "] Handling client: " + clientName);

            // Simulate slow/blocking I/O - reading from client
            String line;
            while ((line = in.readLine()) != null) {   // ← This is BLOCKING
                System.out.println("[" + LocalTime.now() + "] Received: " + line);

                // Simulate slow processing / blocking operation
                Thread.sleep(3000);   // 3 seconds delay

                out.println("Server processed: " + line.toUpperCase());
            }

        } catch (Exception e) {
            System.out.println("Client " + clientName + " disconnected.");
        }
    }
}