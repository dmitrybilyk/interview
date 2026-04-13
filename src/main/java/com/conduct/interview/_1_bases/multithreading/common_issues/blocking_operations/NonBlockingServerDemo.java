package com.conduct.interview._1_bases.multithreading.common_issues.blocking_operations;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NonBlockingServerDemo {
    public static void main(String[] args) throws IOException {
        // 1. Open a Selector (the traffic controller)
        Selector selector = Selector.open();

        // 2. Open a ServerSocketChannel and make it non-blocking
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(8088));
        serverChannel.configureBlocking(false);

        // 3. Register the server with the selector to listen for "Accept" events
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Non-blocking server started on port 8088...");

        while (true) {
            // This waits for events, but the thread can be woken up or timeout
            selector.select(); 

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();

            while (iter.hasNext()) {
                SelectionKey key = iter.next();

                if (key.isAcceptable()) {
                    // Handle new connection without blocking
                    registerClient(selector, serverChannel);
                }

                if (key.isReadable()) {
                    // Read data from client without blocking
                    answerClient(key);
                }
                iter.remove();
            }
        }
    }

    private static void registerClient(Selector selector, ServerSocketChannel serverChannel) throws IOException {
        SocketChannel client = serverChannel.accept();
        client.configureBlocking(false); // CRITICAL: client is also non-blocking
        client.register(selector, SelectionKey.OP_READ);
        System.out.println("New client connected: " + client.getRemoteAddress());
    }

    private static void answerClient(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(256);
        int bytesRead = client.read(buffer);

        if (bytesRead == -1) {
            client.close();
            System.out.println("Client disconnected.");
        } else {
            String message = new String(buffer.array()).trim();
            System.out.println("Received: " + message);
            
            ByteBuffer response = ByteBuffer.wrap(("Echo: " + message.toUpperCase()).getBytes());
            client.write(response);
        }
    }
}