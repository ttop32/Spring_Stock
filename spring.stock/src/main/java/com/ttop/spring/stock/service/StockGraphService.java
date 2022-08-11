package com.ttop.spring.stock.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.ttop.spring.stock.domain.StockDetail;
import com.ttop.spring.stock.domain.StockGraph;
import com.ttop.spring.stock.dto.StockGraphRequestBatchDto;
import com.ttop.spring.stock.dto.StockGraphRequestDto;
import com.ttop.spring.stock.dto.StockGraphResponseDto;
import com.ttop.spring.stock.dto.StockGraphSingleDto;
import com.ttop.spring.stock.error.CustomException;
import com.ttop.spring.stock.error.ErrorCode;
import com.ttop.spring.stock.repo.StockDetailRepo;
import com.ttop.spring.stock.repo.StockGraphRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

// https://www.baeldung.com/spring-data-jpa-batch-inserts

@AllArgsConstructor
@Service
@Transactional
public class StockGraphService {
    StockGraphRepo stockGraphRepo;
    StockDetailRepo stockRepo;

    private StockDetail getStockDetail(String tickerId) {
        return stockRepo.findByTickerId(tickerId).orElseThrow(() -> new CustomException(ErrorCode.STOCK_NOT_FOUND));
    }


    

    public StockGraph addStockGraphSingle(StockGraphSingleDto stockGraphSingleDto) {
        StockDetail stock = getStockDetail(stockGraphSingleDto.getTickerId());
        StockGraph stockGraph = stockGraphRepo.save(stockGraphSingleDto.toEntity(stock));
        return stockGraph;
    }

    public List<StockGraph> getStockGraphList(String tickerId) {
        return stockGraphRepo.findByStockTickerIdOrderByDateAsc(tickerId);
    }

    public void removeStockGraph(String tickerId) {
        List<StockGraph> stockGraph = getStockGraphList(tickerId);
        stockGraphRepo.deleteAllInBatch(stockGraph);
    }




    public List<StockGraph> addStockGraphList(String tickerId, StockGraphRequestDto stockGraphRequestDto) {
        StockDetail stock = getStockDetail(tickerId);
        removeBetweenDateByAxesType(tickerId, stockGraphRequestDto.getDateList(),stockGraphRequestDto.getColumnNameList());
        return stockGraphRepo.saveAll(stockGraphRequestDto.toEntity(stock));
    }






    public List<StockGraph> addStockGraphListBatch(StockGraphRequestBatchDto stockGraphRequestBatchDto) {
        // get ticker list
        List<StockDetail> stockList=new ArrayList<>();
        for( String ticker  : stockGraphRequestBatchDto.getTickerList()){
            StockDetail stock = getStockDetail(ticker); 
            stockList.add(stock);           
        }

        // date iterate remove
        for (int i = 0; i < stockGraphRequestBatchDto.getTickerList().size(); i++) {
            String ticker=stockGraphRequestBatchDto.getTickerList().get(i);
            StockDetail stock = getStockDetail(ticker); 
            stockList.add(stock);           
            removeBetweenDateByAxesType(ticker, stockGraphRequestBatchDto.getStockGraphRequestBatch().get(i).getDateList(),stockGraphRequestBatchDto.getColumnNameList());        
        }

        // removeBetweenDate(tickerId, stockGraphRequestDto.getDateList());
        return stockGraphRepo.saveAll(stockGraphRequestBatchDto.toEntity(stockList));
    }

    private void removeBetweenDateByAxesType(String tickerId, List<LocalDate> dateList,List<String> columnList){//StockGraphRequestDto stockGraphRequestDto) {
        if (dateList.isEmpty()) {
            return;
        }

        LocalDate startDate = dateList.stream().min(Comparator.comparing(LocalDate::toEpochDay)).get();
        LocalDate endDate = dateList.stream().max(Comparator.comparing(LocalDate::toEpochDay)).get();

        for (String axesType : columnList) {

            List<StockGraph> stockGraphList = stockGraphRepo.findByStockTickerIdAndAxesTypeAndDateBetween(tickerId, axesType,startDate,
            endDate);
            stockGraphRepo.deleteAllInBatch(stockGraphList);
        }

        
    }





    private void removeBetweenDate(String tickerId, List<LocalDate> localDates) {
        if (localDates.isEmpty()) {
            return;
        }

        LocalDate startDate = localDates.stream().min(Comparator.comparing(LocalDate::toEpochDay)).get();
        LocalDate endDate = localDates.stream().max(Comparator.comparing(LocalDate::toEpochDay)).get();

        List<StockGraph> stockGraphList = stockGraphRepo.findByStockTickerIdAndDateBetween(tickerId, startDate,
                endDate);
        stockGraphRepo.deleteAllInBatch(stockGraphList);
    }








    public StockGraphResponseDto getOneYearGraph(String tickerId) {
        LocalDate oneYearBeforeDate = LocalDate.now().minusYears(1);
        List<StockGraph> stockGraphList = stockGraphRepo.findByStockTickerIdAndDateAfterOrderByDateAsc(tickerId,
                oneYearBeforeDate);
        return new StockGraphResponseDto(tickerId, stockGraphList);
    }

    public StockGraphResponseDto getOneMonthGraph(String tickerId) {
        LocalDate oneYearBeforeDate = LocalDate.now().minusMonths(1);
        List<StockGraph> stockGraphList = stockGraphRepo.findByStockTickerIdAndDateAfterOrderByDateAsc(tickerId,
                oneYearBeforeDate);
        return new StockGraphResponseDto(tickerId, stockGraphList);
    }

    public StockGraphResponseDto getWholeYearGraph(String tickerId) {
        List<StockGraph> stockGraphList = stockGraphRepo.findByStockTickerIdOrderByDateAsc(tickerId);
        return new StockGraphResponseDto(tickerId, stockGraphList);
    }




}
