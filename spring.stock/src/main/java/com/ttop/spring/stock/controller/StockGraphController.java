package com.ttop.spring.stock.controller;

import java.util.List;

import javax.validation.Valid;

import com.ttop.spring.stock.domain.StockGraph;
import com.ttop.spring.stock.dto.StockGraphRequestBatchDto;
import com.ttop.spring.stock.dto.StockGraphRequestDto;
import com.ttop.spring.stock.dto.StockGraphResponseDto;
import com.ttop.spring.stock.dto.StockGraphSingleDto;
import com.ttop.spring.stock.service.StockGraphService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/stockgraph")
public class StockGraphController {
    StockGraphService stockGraphService;



    @PostMapping("/editor")
    public StockGraph createStockGraph(@Valid @RequestBody StockGraphSingleDto stockGraphSingleDto){
        return stockGraphService.addStockGraphSingle(stockGraphSingleDto);
    }

    @DeleteMapping("/editor/{tickerId}")
    public void deleteStockGraph(@PathVariable String tickerId){
        stockGraphService.removeStockGraph(tickerId);
    }


    @PostMapping("/editor/{tickerId}")
    public List<StockGraph>  insertStockGraphList(@PathVariable String tickerId,
        @Valid @RequestBody StockGraphRequestDto stockGraphRequestDto){
        return stockGraphService.addStockGraphList(tickerId,stockGraphRequestDto);
    }

    @PostMapping("/editor/batch")
    public List<StockGraph>  insertStockGraphBatchList(
        @Valid @RequestBody StockGraphRequestBatchDto stockGraphRequestBatchDto){
        return stockGraphService.addStockGraphListBatch(stockGraphRequestBatchDto);
    }

    

    @GetMapping("/editor/{tickerId}")
    public List<StockGraph> readStockGraphList(@PathVariable String tickerId){
        return stockGraphService.getStockGraphList(tickerId);
    }

    
    @GetMapping("/{tickerId}")
    public StockGraphResponseDto readOneMonthGraph(@PathVariable String tickerId){
        return stockGraphService.getOneMonthGraph(tickerId);
    }
    
    @GetMapping("/{tickerId}/year")
    public StockGraphResponseDto readOneYearGraph(@PathVariable String tickerId){
        return stockGraphService.getOneYearGraph(tickerId);
    }
    @GetMapping("/{tickerId}/all")
    public StockGraphResponseDto readWholeYearGraph(@PathVariable String tickerId){
        return stockGraphService.getWholeYearGraph(tickerId);
    }
    

    



}