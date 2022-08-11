package com.ttop.spring.stock.controller;

import javax.validation.Valid;

import com.ttop.spring.stock.domain.StockDetail;
import com.ttop.spring.stock.dto.StockDetailDto;
import com.ttop.spring.stock.dto.StockPageResponseDto;
import com.ttop.spring.stock.service.StockDetailService;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/stock")
public class StockDetailController {
    StockDetailService stockDetailService;



    @PostMapping("/editor/")
    public StockDetail createStock(@Valid @RequestBody StockDetailDto stockDto) {
        return stockDetailService.saveStock(stockDto);
    }

    @PutMapping("/editor/")
    public StockDetail updateStock(@Valid @RequestBody StockDetailDto stockDto) {
        return stockDetailService.modifyStock(stockDto);
    }

    @DeleteMapping("/editor/{tickerId}")
    public void deleteStock(@PathVariable String tickerId) {
        stockDetailService.removeStock(tickerId);
    }

    @GetMapping("")
    public StockPageResponseDto readStockPage(@RequestParam(defaultValue="") String searchParam, @RequestParam(defaultValue="All") String searchType,
            @PageableDefault(page = 0,size = 10, sort = "id", direction = Direction.DESC) Pageable pageable) {
        return stockDetailService.searchStockPage(searchParam,searchType, pageable);
    }


    @GetMapping("/detail")
    public StockDetail readStockDetail(@RequestParam(defaultValue="") String tickerId) {
        return stockDetailService.getStockDetail( tickerId);
    }


}
