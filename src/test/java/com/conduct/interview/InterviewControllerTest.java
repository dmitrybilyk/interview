package com.conduct.interview;

import com.conduct.interview.practise.spring.controller.InterviewController;
import com.conduct.interview.practise.spring.controller.service.MyService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InterviewController.class)
public class InterviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MyService myService;

    @Test
    void shouldReturnInterview() throws Exception {
        Mockito.when(myService.sayHello()).thenReturn("Hello World!");

        Mockito.verify(myService, Mockito.times(1)).sayHello();

        mockMvc.perform(get("/interview"))
                .andExpect(status().isOk())
                .andExpect(content().string("interview"));
    }
}
