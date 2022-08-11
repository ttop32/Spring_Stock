package com.ttop.spring.stock.controller;




import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.ttop.spring.stock.domain.StockDetail;
import com.ttop.spring.stock.domain.StockGraph;
import com.ttop.spring.stock.dto.StockGraphRequestDto;
import com.ttop.spring.stock.dto.StockGraphSingleDto;
import com.ttop.spring.stock.repo.MemberRepo;
import com.ttop.spring.stock.repo.StockDetailRepo;
import com.ttop.spring.stock.repo.StockGraphRepo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions; 




public class StockGraphControllerTest extends IntegrationTest {

    @Autowired
    StockGraphRepo stockGraphRepo;

    @Autowired
    MemberRepo memberRepo;

    @Autowired
    StockDetailRepo stockDetailRepo;

    

    StockDetail stock;
    StockGraphRequestDto stockGraphRequestDto;
    List<StockGraph> stockGraphList;
    List<LocalDate> dateList ;
    List<Float> stockValueList;
    String axesType;


    @BeforeEach
    public void setUp() throws Exception {

        stock=StockDetail.builder().stockName("stockName4").tickerId("tickerId4").build();
        stock=stockDetailRepo.save(stock);

        axesType="high";
        LocalDate date1=LocalDate.of(2021,2,3);
        LocalDate date2=LocalDate.of(2021,2,4);
        LocalDate date3=LocalDate.now();
        dateList = Arrays.asList(date1,date2,date3);
        stockValueList= Arrays.asList(4f,5f,6f);
        HashMap<String,List<Float>> stockGraphMap=new HashMap<>();
        stockGraphMap.put(axesType,stockValueList);

        
        stockGraphRequestDto=StockGraphRequestDto.builder().dateList(dateList).stockGraphMap(stockGraphMap).build();
        
        stockGraphList=new ArrayList<>();
        for (int i = 0; i < 3; i++){
            StockGraph stockGraph=StockGraph.builder().stock(stock).axesValue(stockValueList.get(i)).date(dateList.get(i)).axesType(axesType).build();
            stockGraphList.add(stockGraph);
        }


    }


    @WithMockUser(username = "테스트계정", password = "custom_password", roles = {"MEMBER","ADMIN"})
    @Test
    void testCreateStockGraph() throws Exception {
        //given        
        LocalDate date=LocalDate.now();
        StockGraphSingleDto stockGraphSingleDto=StockGraphSingleDto.builder().axesType(axesType).axesValue(53L).tickerId(stock.getTickerId()).date(date).build();

        //when
        ResultActions resultActions=requestWithBodyParam(post("/stockgraph/editor/"),stockGraphSingleDto);

        //then
        resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("axesType").value(stockGraphSingleDto.getAxesType()))
        .andExpect(jsonPath("axesValue").value(stockGraphSingleDto.getAxesValue()));


        List<StockGraph> stockGraphResult=stockGraphRepo.findByStockTickerIdOrderByDateAsc(stock.getTickerId());
        assertThat(stockGraphResult).isNotEmpty();
        assertThat(stockGraphResult.get(0).getAxesValue()).isEqualTo(stockGraphSingleDto.getAxesValue());
        
    }

    @WithMockUser(username = "테스트계정", password = "custom_password", roles = {"MEMBER","ADMIN"})
    @Test
    void testDeleteStockGraph() throws Exception {
        //given
        stockGraphRepo.saveAll(stockGraphList);

        //when
        ResultActions resultActions=requestWithBodyParam(delete("/stockgraph/editor/"+stock.getTickerId()),"");

        //then
        resultActions
        .andExpect(status().isOk());

        List<StockGraph> stockGraphResult=stockGraphRepo.findByStockTickerIdOrderByDateAsc(stock.getTickerId());
        assertThat(stockGraphResult).isEmpty();
    }

    @WithMockUser(username = "테스트계정", password = "custom_password", roles = {"MEMBER","ADMIN"})
    @Test
    void testInsertStockGraphList() throws Exception {
        //when
        ResultActions resultActions=requestWithBodyParam(post("/stockgraph/editor/"+stock.getTickerId()),stockGraphRequestDto);

        //then
        resultActions
        .andExpect(status().isOk());

        List<StockGraph> stockGraphResult=stockGraphRepo.findByStockTickerIdOrderByDateAsc(stock.getTickerId());
        assertThat(stockGraphResult).isNotEmpty();
        assertThat(stockGraphResult.get(0).getAxesValue()).isEqualTo(stockValueList.get(0));
        assertThat(stockGraphResult.get(1).getAxesValue()).isEqualTo(stockValueList.get(1));
    }

    @WithMockUser(username = "테스트계정", password = "custom_password", roles = {"MEMBER","ADMIN"})
    @Test
    void testReadStockGraphList() throws Exception {

        //given
        stockGraphRepo.saveAll(stockGraphList);

        //when
        ResultActions resultActions=requestWithBodyParam(get("/stockgraph/editor/"+stock.getTickerId()),"");

        //then
        resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].axesValue").value(stockGraphList.get(0).getAxesValue()))
        .andExpect(jsonPath("$[1].axesValue").value(stockGraphList.get(1).getAxesValue()))
        .andExpect(jsonPath("$[2].axesValue").value(stockGraphList.get(2).getAxesValue()));

    }

    @Test
    void testReadOneYearGraph() throws Exception {

        //given
        stockGraphRepo.saveAll(stockGraphList);

        //when
        ResultActions resultActions=requestWithBodyParam(get("/stockgraph/"+stock.getTickerId()),"");

        //then
        resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tickerId").value(stock.getTickerId()))
        .andExpect(jsonPath("$.stockGraphMap.high[0]").value(stockGraphList.get(2).getAxesValue()));

    }

    @Test
    void testReadWholeYearGraph() throws Exception {

        //given
        stockGraphRepo.saveAll(stockGraphList);

        //when
        ResultActions resultActions=requestWithBodyParam(get("/stockgraph/"+stock.getTickerId()+"/all"),"");

        //then
        resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tickerId").value(stock.getTickerId()))
        .andExpect(jsonPath("$.stockGraphMap.high[0]").value(stockGraphList.get(0).getAxesValue()))
        .andExpect(jsonPath("$.stockGraphMap.high[1]").value(stockGraphList.get(1).getAxesValue()));

    }
}
