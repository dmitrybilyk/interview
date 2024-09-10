//package com.learn.easyboot.patterns.creational.factory.factory_method;
//
//public class FactoryMethodCheck2 {
//    public static void main(String[] args) {
//        String whichSongToWrite = "fun";
//        SongWriter songWriter;
//        if (whichSongToWrite.equals("fun")) {
//            songWriter = new FunSongWriter();
//        } else {
//            songWriter = new SadSongWriter();
//        }
//
//        songWriter.prepare();
//        songWriter.writeSong();
//    }
//}
//
//abstract class SongWriter {
//    void prepare() {
//        System.out.println("Preparing...");
//    }
//    abstract void writeSong();
//}
//
//class FunSongWriter extends SongWriter {
//
//    @Override
//    void writeSong() {
//        System.out.println("writing a fun song");
//    }
//}
//
//class SadSongWriter extends SongWriter {
//
//    @Override
//    void writeSong() {
//        System.out.println("writing a sad song");
//    }
//}