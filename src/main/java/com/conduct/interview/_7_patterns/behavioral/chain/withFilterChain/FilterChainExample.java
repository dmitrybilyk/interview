package com.conduct.interview._7_patterns.behavioral.chain.withFilterChain;

import java.util.ArrayList;
import java.util.List;

// Request class to hold request data
class Request {
    private String requestData;

    public Request(String requestData) {
        this.requestData = requestData;
    }

    public String getRequestData() {
        return requestData;
    }
}

// Response class to hold response data
class Response {
    private String responseData;

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public String getResponseData() {
        return responseData;
    }
}

// Filter interface, similar to javax.servlet.Filter
interface Filter {
    void doFilter(Request request, Response response, FilterChain chain);
}

// FilterChain class to manage the sequence of filters
class FilterChain {
    private List<Filter> filters = new ArrayList<>();
    private int currentPosition = 0;

    // Add filters to the chain
    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    // Invoke the next filter in the chain
    public void doFilter(Request request, Response response) {
        if (currentPosition < filters.size()) {
            Filter nextFilter = filters.get(currentPosition);
            currentPosition++;
            nextFilter.doFilter(request, response, this);
        } else {
            // If no more filters, process the target (e.g., a servlet)
            System.out.println("Reached the target servlet. Processing request...");
        }
    }
}

// Concrete filter for authentication
class AuthenticationFilter implements Filter {
    @Override
    public void doFilter(Request request, Response response, FilterChain chain) {
        System.out.println("AuthenticationFilter: Checking authentication...");
        if (request.getRequestData().equals("authenticated")) {
            // Proceed to the next filter
            chain.doFilter(request, response);
        } else {
            System.out.println("AuthenticationFilter: Request is not authenticated!");
        }
    }
}

// Concrete filter for logging
class LoggingFilter implements Filter {
    @Override
    public void doFilter(Request request, Response response, FilterChain chain) {
        System.out.println("LoggingFilter: Logging request: " + request.getRequestData());
        // Proceed to the next filter
        chain.doFilter(request, response);
    }
}

// Concrete filter for compression
class CompressionFilter implements Filter {
    @Override
    public void doFilter(Request request, Response response, FilterChain chain) {
        System.out.println("CompressionFilter: Compressing response...");
        // Proceed to the next filter or servlet
        chain.doFilter(request, response);
        // Modify response after other filters and servlet have processed the request
        response.setResponseData("Compressed data");
    }
}

// Main class to test the FilterChain
public class FilterChainExample {
    public static void main(String[] args) {
        // Create a request and response
        Request request = new Request("authenticated");
        Response response = new Response();

        // Create a filter chain and add filters to it
        FilterChain filterChain = new FilterChain();
        filterChain.addFilter(new AuthenticationFilter());
        filterChain.addFilter(new LoggingFilter());
        filterChain.addFilter(new CompressionFilter());

        // Start the filter chain
        filterChain.doFilter(request, response);

        // Display the response after all filters have executed
        System.out.println("Final response: " + response.getResponseData());
    }
}
