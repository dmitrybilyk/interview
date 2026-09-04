package com.oauth2backendflow.bff;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * One recorded step of the flow, kept in the HttpSession so every page can render
 * the full history instead of only the last hop.
 */
public record TraceEntry(String time, String title, String request, String response) implements Serializable {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public static TraceEntry of(String title, String request, String response) {
        return new TraceEntry(LocalTime.now().format(FORMAT), title, request, response);
    }
}
