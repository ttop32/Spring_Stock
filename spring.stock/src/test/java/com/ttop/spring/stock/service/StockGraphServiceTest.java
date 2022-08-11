package com.ttop.spring.stock.service;



import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.ttop.spring.stock.domain.StockDetail;
import com.ttop.spring.stock.domain.StockGraph;
import com.ttop.spring.stock.dto.StockGraphRequestDto;
import com.ttop.spring.stock.dto.StockGraphResponseDto;
import com.ttop.spring.stock.dto.StockGraphSingleDto;
import com.ttop.spring.stock.repo.StockDetailRepo;
import com.ttop.spring.stock.repo.StockGraphRepo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class StockGraphServiceTest {

    @Mock
    StockGraphRepo stockGraphRepo;
    @Mock
    StockDetailRepo stockRepo;

    @InjectMocks
    StockGraphService stockGraphService;


    StockDetail stock;
    StockGraphRequestDto stockGraphRequestDto;
    List<StockGraph> stockGraphList;
    String axesType;

    @BeforeEach
    public void setUp() throws Exception {
        axesType="axesType1";
        LocalDate date1=LocalDate.of(2021,2,3);
        LocalDate date2=LocalDate.of(2021,2,4);
        List<LocalDate> dateList = Arrays.asList(date1,date2);
        List<Float> stockValueList= Arrays.asList(4f,5f);
        HashMap<String,List<Float>> stockGraphMap=new HashMap<>();
        stockGraphMap.put(axesType,stockValueList);

        stock=StockDetail.builder().stockName("stockName2").tickerId("tickerId3").build();
        stockGraphRequestDto=StockGraphRequestDto.builder().dateList(dateList).stockGraphMap(stockGraphMap).build();
        
        StockGraph stockGraph1=StockGraph.builder().axesValue(stockValueList.get(0)).date(date1).axesType(axesType).build();
        StockGraph stockGraph2=StockGraph.builder().axesValue(stockValueList.get(1)).date(date2).axesType(axesType).build();
        stockGraphList=Arrays.asList(stockGraph1,stockGraph2);
                
    }


    @Test
    void testAddStockGraphList() {
        //given
        given(stockGraphRepo.saveAll(any())).willReturn(stockGraphList);
        given(stockRepo.findByTickerId(stock.getTickerId())).willReturn(Optional.of(stock));
        
        //when
        List<StockGraph> stockGraphListResult =stockGraphService.addStockGraphList(stock.getTickerId(),stockGraphRequestDto);


        //then
        assertThat(stockGraphListResult).isNotNull();
        assertThat(stockGraphListResult).isNotEmpty();
        assertThat(stockGraphListResult.get(0).getAxesValue()).isEqualTo(stockGraphList.get(0).getAxesValue());
        assertThat(stockGraphListResult.get(1).getAxesValue()).isEqualTo(stockGraphList.get(1).getAxesValue());
    }

    @Test
    void testAddStockGraphSingle() {
        //given
        LocalDate date=LocalDate.of(2021,2,3);
        StockDetail stock=StockDetail.builder().stockName("stockName2").tickerId("tickerId3").build();
        StockGraphSingleDto stockGraphSingleDto=StockGraphSingleDto.builder().axesType("axesType").axesValue(3542).date(date).tickerId(stock.getTickerId()).build();
        StockGraph stockGraph=stockGraphSingleDto.toEntity(stock);
        given(stockGraphRepo.save(any())).willReturn(stockGraph);
        given(stockRepo.findByTickerId(stock.getTickerId())).willReturn(Optional.of(stock));


        //when
        StockGraph stockGraphResult =stockGraphService.addStockGraphSingle(stockGraphSingleDto);

        //then
        assertThat(stockGraphResult).isNotNull();
        assertThat(stockGraphResult.getAxesValue()).isEqualTo(stockGraph.getAxesValue());
    }

    @Test
    void testGetStockGraphList() {
        //given

        given(stockGraphRepo.findByStockTickerIdOrderByDateAsc(any())).willReturn(stockGraphList);

        //when
        List<StockGraph> stockGraphListResult =stockGraphService.getStockGraphList(stock.getTickerId());

        //then
        assertThat(stockGraphListResult).isNotNull();
        assertThat(stockGraphListResult).isNotEmpty();
        
        assertThat(stockGraphListResult.get(0).getAxesValue()).isEqualTo(stockGraphList.get(0).getAxesValue());
    }


    @Test
    void testGetOneYearGraph() {
        //given
        given(stockGraphRepo.findByStockTickerIdAndDateAfterOrderByDateAsc(any(),any())).willReturn(stockGraphList);

        //when
        StockGraphResponseDto stockGraphResponseDto =stockGraphService.getOneYearGraph(stock.getTickerId());

        //then
        assertThat(stockGraphResponseDto).isNotNull();        
        assertThat(stockGraphResponseDto.getStockGraphMap().get(axesType).get(0)).isEqualTo(stockGraphList.get(0).getAxesValue());

    }


    @Test
    void testGetWholeYearGraph() {
        //given
        given(stockGraphRepo.findByStockTickerIdOrderByDateAsc(any())).willReturn(stockGraphList);

        //when
        StockGraphResponseDto stockGraphResponseDto =stockGraphService.getWholeYearGraph(stock.getTickerId());

        //then
        assertThat(stockGraphResponseDto).isNotNull();        
        assertThat(stockGraphResponseDto.getStockGraphMap().get(axesType).get(0)).isEqualTo(stockGraphList.get(0).getAxesValue());

    }


}
