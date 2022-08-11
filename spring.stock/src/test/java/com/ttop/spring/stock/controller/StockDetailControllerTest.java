package com.ttop.spring.stock.controller;



import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ttop.spring.stock.domain.StockDetail;
import com.ttop.spring.stock.dto.StockDetailDto;
import com.ttop.spring.stock.repo.StockDetailRepo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;



public class StockDetailControllerTest extends IntegrationTest {
    
    @Autowired
    StockDetailRepo stockDetailRepo;
    


    @WithMockUser(username = "테스트계정", password = "custom_password", roles = {"MEMBER","ADMIN"})
    @Test
    public void testCreateStock() throws Exception{
        //given
        StockDetailDto stockDto=StockDetailDto.builder().tickerId("tickerlg3158").stockName("stockNamelg32156").build();

        //when
        ResultActions resultActions=requestWithBodyParam(post("/stock/editor/"),stockDto);

        //then
        resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("stockName").value(stockDto.getStockName()))
        .andExpect(jsonPath("tickerId").value(stockDto.getTickerId()));


        
        assertThat(stockDetailRepo.findByTickerId(stockDto.getTickerId()).isPresent()).isTrue();
        
    }





    @WithMockUser(username = "테스트계정", password = "custom_password", roles = {"MEMBER","ADMIN"})
    @Test
    void testDeleteStock() throws Exception {

        //given
        StockDetailDto stockDto=StockDetailDto.builder().tickerId("tickerlg3158").stockName("stockNamelg32156").build();
        stockDetailRepo.save(stockDto.toEntity());

        //when
        ResultActions resultActions=requestWithBodyParam(delete("/stock/editor/"+stockDto.getTickerId()),"");

        //then
        resultActions
        .andExpect(status().isOk());

        assertThat(stockDetailRepo.findByTickerId(stockDto.getTickerId()).isPresent()).isFalse();
        
    }

    
    @WithMockUser(username = "테스트계정", password = "custom_password", roles = {"MEMBER","ADMIN"})
    @Test
    void testReadStockPage() throws Exception {

        //given
        StockDetailDto stockDto=StockDetailDto.builder().tickerId("tickerlg3158").stockName("stockNamelg32156").build();
        stockDetailRepo.save(stockDto.toEntity());

        //when
        ResultActions resultActions=requestWithBodyParam(get("/stock"),"");

        //then
        resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.contents[0].stockName").value(stockDto.getStockName()))
        .andExpect(jsonPath("$.page").value(0));

    }

    @WithMockUser(username = "테스트계정", password = "custom_password", roles = {"MEMBER","ADMIN"})
    @Test
    void testUpdateStock() throws Exception {

        //given
        StockDetailDto stockDto=StockDetailDto.builder().tickerId("tickerlg31568").stockName("stockNamelg32156").build();
        stockDetailRepo.save(stockDto.toEntity());
        stockDto.setStockName("new44stockName");

        //when
        ResultActions resultActions=requestWithBodyParam(put("/stock/editor/"),stockDto);

        //then
        resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("stockName").value(stockDto.getStockName()))
        .andExpect(jsonPath("tickerId").value(stockDto.getTickerId()));


        StockDetail savedResult=stockDetailRepo.findByTickerId(stockDto.getTickerId()).get();
        assertThat(savedResult.getStockName()).isEqualTo(stockDto.getStockName());
    }




}
