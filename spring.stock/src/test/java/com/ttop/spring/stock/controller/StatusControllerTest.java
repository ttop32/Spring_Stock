package com.ttop.spring.stock.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;




public class StatusControllerTest extends  IntegrationTest{
    

    @Test
    public void testAlive () throws Exception {
        mockMvc.perform(get("/status/alive")).andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string("alive"));
    }


    @Test
    public void Profile확인 () throws Exception {
        mockMvc.perform(get("/status/profile")).andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string("dev"));

    }

}
