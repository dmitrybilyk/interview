package com.conduct.interview._1_bases.io_streams.nio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.Path;
import java.io.IOException;

public class AsyncFileReadExample {
    private static final String FILE_PATH = "/home/dmytro/dev/projects/interview/src/main/java/" +
            "com/conduct/interview/_1_bases/io_streams/nio/" +
            "output_nio.txt";

    public static void main(String[] args) {
        Path path = Paths.get(FILE_PATH); // Change to your file path
        try (AsynchronousFileChannel fileChannel = 
                AsynchronousFileChannel.open(path, StandardOpenOption.READ)) {
            
            // Create a ByteBuffer to hold the file data
            ByteBuffer buffer = ByteBuffer.allocate(1024); // 1 KB buffer
            
            // Read the file asynchronously
            fileChannel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    System.out.println("Read " + result + " bytes.");
                    attachment.flip(); // Prepare the buffer for reading
                    byte[] data = new byte[result];
                    attachment.get(data); // Read data into byte array
                    System.out.println("Data: " + new String(data)); // Convert to String
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    System.err.println("Failed to read file: " + exc.getMessage());
                }
            });

            // Keep the program alive until the read completes
            Thread.sleep(5000); // Wait for the asynchronous operation to finish

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
