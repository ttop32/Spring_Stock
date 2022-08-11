package com.ttop.spring.stock.service;

import com.ttop.spring.stock.domain.StockDetail;
import com.ttop.spring.stock.dto.StockDetailDto;
import com.ttop.spring.stock.dto.StockPageResponseDto;
import com.ttop.spring.stock.repo.StockDetailRepo;


import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

import static org.assertj.core.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class StockDetailServiceTest {


    @Mock
    private StockDetailRepo stockRepo;

    @InjectMocks
    private StockDetailService stockService;
    

    @Test
    public void testSaveStock(){

        //given
        StockDetailDto stockDto=StockDetailDto.builder().tickerId("ticker1").stockName("stockName1").build();
        given(stockRepo.save(any())).willReturn(stockDto.toEntity());


        //when
        StockDetail stock =stockService.saveStock(stockDto);

        //then
        assertThat(stock).isNotNull();
        assertThat(stock.getStockName()).isEqualTo(stockDto.toEntity().getStockName());
    }


    @Test
    public void testModifyStock(){

        //given
        StockDetailDto stockDto=StockDetailDto.builder().tickerId("ticker1").stockName("stockName1").build();
        given(stockRepo.findByTickerId(any())).willReturn(Optional.of(stockDto.toEntity()));

        //when
        StockDetail stock =stockService.modifyStock(stockDto);

        //then
        assertThat(stock).isNotNull();
        assertThat(stock.getStockName()).isEqualTo(stockDto.toEntity().getStockName());
    }


    @Test
    public void testSearchStockPage(){

        //given
        StockDetail stockDetail=StockDetail.builder().tickerId("ticker1").stockName("stockName1").build();
        List<StockDetail> stockDetailList=Collections.singletonList(stockDetail);
        Page<StockDetail> stockDetailPage=new PageImpl<>(stockDetailList);

        given(stockRepo.findByTickerIdIgnoreCaseContainingOrStockNameIgnoreCaseContaining(any(),
        any(), any())
        ).willReturn(stockDetailPage);

        String searchParam="";
        Pageable pageable=PageRequest.of(0, 10, Direction.ASC, "id");
        
        //when
        StockPageResponseDto stockPageResponseDto =stockService.searchStockPage(searchParam,"All",pageable);


        //then
        assertThat(stockPageResponseDto).isNotNull();
        assertThat(stockPageResponseDto.getPage()).isEqualTo(stockDetailPage.getNumber());
        assertThat(stockPageResponseDto.getSize()).isEqualTo(stockDetailPage.getSize());
        assertThat(stockPageResponseDto.getContents().get(0).getStockName()).isEqualTo(stockDetail.getStockName());
    }




}
