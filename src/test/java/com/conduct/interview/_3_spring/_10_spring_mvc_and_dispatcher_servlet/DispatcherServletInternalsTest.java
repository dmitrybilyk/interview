package com.conduct.interview._3_spring._10_spring_mvc_and_dispatcher_servlet;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Manually drives the two objects DispatcherServlet.doDispatch() calls
 * internally, without a servlet container - see
 * spring_mvc_and_dispatcher_servlet.md for why this lives here.
 */
class DispatcherServletInternalsTest {

    @Test
    void dispatchesARequestWithoutAnyServletContainer() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(GreetingController.class);
            context.registerBean(RequestMappingHandlerMapping.class);
            context.registerBean(RequestMappingHandlerAdapter.class);
            context.refresh();

            RequestMappingHandlerMapping handlerMapping = context.getBean(RequestMappingHandlerMapping.class);
            RequestMappingHandlerAdapter handlerAdapter = context.getBean(RequestMappingHandlerAdapter.class);

            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/greet");
            request.setParameter("name", "world");
            MockHttpServletResponse response = new MockHttpServletResponse();

            // step 1 of DispatcherServlet.doDispatch(): who handles this URL?
            HandlerExecutionChain chain = handlerMapping.getHandler(request);
            assertThat(chain).isNotNull();
            System.out.println("resolved handler -> " + chain.getHandler());

            // step 2: invoke it, resolving @RequestParam etc. along the way
            handlerAdapter.handle(request, response, chain.getHandler());

            System.out.println("response body    -> " + response.getContentAsString());
            assertThat(response.getContentAsString()).isEqualTo("Hello, world");
        }
    }
}
