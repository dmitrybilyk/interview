package com.conduct.interview._1_bases.multithreading.udemy.throughput;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThroughputHttpServer {
    private static final String INPUT_FILE =
            "/home/dmytro/dev/projects/interview/src/main/java/com/conduct/interview/_1_bases/multithreading/udemy/resources/throughput/war_and_peace.txt";
    private static final int NUMBER_OF_THREADS = 6;

    public static void main(String[] args) throws IOException {
        String text = new String(Files.readAllBytes(Paths.get(INPUT_FILE)));
        startServer(text);
    }

    public static void startServer(String text) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/search", new WordCountHandler(text));

        Executor executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        server.setExecutor(executor);

        server.start();
        System.out.println("Server started on port 8000 with "
                + NUMBER_OF_THREADS + " threads");
    }

    private static class WordCountHandler implements HttpHandler {
        private final String text;

        public WordCountHandler(String text) {
            this.text = text.toLowerCase(); // normalize once
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            URI requestURI = httpExchange.getRequestURI();
            String query = requestURI.getQuery();
            String word = "";

            // simple parsing of ?word=...
            if (query != null && query.startsWith("word=")) {
                word = query.substring("word=".length()).toLowerCase();
            }

            long count = countWord(word);

            String response = "Found " + count + " occurrences of word: " + word;
            httpExchange.sendResponseHeaders(200, response.getBytes().length);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        private long countWord(String word) {
            if (word == null || word.isEmpty()) {
                return 0;
            }
            long count = 0;
            int index = 0;
            while ((index = text.indexOf(word, index)) != -1) {
                count++;
                index += word.length();
            }
            return count;
        }
    }
}
