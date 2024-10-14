package com.conduct.interview._7_patterns.behavioral.chain.check;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class FilterChainCheck {
    public static void main(String[] args) {
        FilterChain filterChain = new FilterChain();
        filterChain.addFilter(new BlackFilter());
        filterChain.addFilter(new WhiteFilter());
        filterChain.addFilter(new AuthenticateFilter());
        filterChain.doFilter(new SomeChainRequest("Blackauthenticated"), new SomeChainResponse());
    }
}

@Getter
@Setter
@AllArgsConstructor
class SomeChainRequest {
    private String content;
}

@Getter
@Setter
class SomeChainResponse {
    private String content;
}

interface Filter {
    void doFilter(SomeChainRequest someChainRequest,
                  SomeChainResponse someChainResponse,
                  FilterChain filterChain);
}

class WhiteFilter implements Filter {

    @Override
    public void doFilter(SomeChainRequest someChainRequest, SomeChainResponse someChainResponse, FilterChain filterChain) {
        if (someChainRequest.getContent().contains("White")) {
            System.out.println("In White - " + someChainRequest);
        }
        filterChain.doFilter(someChainRequest, someChainResponse);
    }
}

class BlackFilter implements Filter {

    @Override
    public void doFilter(SomeChainRequest someChainRequest, SomeChainResponse someChainResponse, FilterChain filterChain) {
        if (someChainRequest.getContent().contains("Black")) {
            System.out.println("In Black - " + someChainRequest);
        }
        filterChain.doFilter(someChainRequest, someChainResponse);
    }
}

class AuthenticateFilter implements Filter {

    @Override
    public void doFilter(SomeChainRequest someChainRequest, SomeChainResponse someChainResponse, FilterChain filterChain) {
        if (!someChainRequest.getContent().contains("authenticated")) {
            System.out.println("Authenticated the request");
        }
        System.out.println("Already authenticated");
        filterChain.doFilter(someChainRequest, someChainResponse);
    }
}

class FilterChain {
    private List<Filter> filters = new ArrayList<>();
    private int currentPosition;

    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    public void doFilter(SomeChainRequest someChainRequest,
                         SomeChainResponse someChainResponse) {
        if (!filters.isEmpty() && currentPosition < filters.size()) {
            Filter currentFilter = filters.get(currentPosition);
            currentPosition++;
            currentFilter.doFilter(someChainRequest, someChainResponse, this);
        } else {
            System.out.println("Got to Servlet already");
        }
    }
}