package fr.eliferd.engine.utils;

import java.io.PrintStream;

public class Logger {

    public static void print(String message, LoggerLevel level) {
        PrintStream printer = null;

        switch(level) {
            case LoggerLevel.INFO:
            case LoggerLevel.WARNING:
                printer = System.out;
                break;
            case LoggerLevel.ERROR:
                printer = System.err;
                break;
            default:
                printer = System.out;
                level = LoggerLevel.INFO;
                break;
        }

        printer.println(level.toString() + " " + message);
    }

}