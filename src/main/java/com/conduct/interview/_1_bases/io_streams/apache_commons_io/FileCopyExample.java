package com.conduct.interview._1_bases.io_streams.apache_commons_io;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;

public class FileCopyExample {
    public static void main(String[] args) {
        File sourceFile = new File("/home/dmytro/dev/projects/interview/src/main/java/com/conduct/interview/_1_bases/io_streams/apache_commons_io/source.txt");
        File destFile = new File("/home/dmytro/dev/projects/interview/src/main/java/com/conduct/interview/_1_bases/io_streams/apache_commons_io/dest.txt");

        try {
            // Copy file from source to destination
            FileUtils.copyFile(sourceFile, destFile);
            System.out.println("File copied successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
