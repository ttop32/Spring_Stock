package com.ttop.spring.stock.service;

import com.ttop.spring.stock.domain.StockDetail;
import com.ttop.spring.stock.dto.StockDetailDto;
import com.ttop.spring.stock.dto.StockPageResponseDto;
import com.ttop.spring.stock.error.CustomException;
import com.ttop.spring.stock.error.ErrorCode;
import com.ttop.spring.stock.repo.StockDetailRepo;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Service
@Transactional
@Slf4j
public class StockDetailService {
   // https://ryumodrn.tistory.com/46
    

    StockDetailRepo stockRepo;

    

    private StockDetail findStockByTickerId(String tickerId) {
        return stockRepo.findByTickerId(tickerId).orElseThrow(() -> new CustomException(ErrorCode.STOCK_NOT_FOUND));
    }

    public StockDetail getStockDetail(String tickerId){
        return findStockByTickerId(tickerId);
    }

    @CacheEvict(value = "stockPage", allEntries = true)
    public StockDetail saveStock(StockDetailDto stockDto) {
        
        StockDetail stock=stockRepo.findByTickerId(stockDto.getTickerId()).orElse(stockDto.toEntity());
        stock.updateStcokName(stockDto.getStockName());
        return stockRepo.save(stock);
    }


    @CacheEvict(value = "stockPage", allEntries = true)
    public StockDetail modifyStock(StockDetailDto stockDto) {
        StockDetail stock = findStockByTickerId(stockDto.getTickerId());
        return stock.updateStcokName(stockDto.getStockName());
        // return stockRepo.save(stockDto.toEntity());
    }

    @CacheEvict(value = "stockPage", allEntries = true)
    public void removeStock(String tickerId) {
        StockDetail stock = findStockByTickerId(tickerId);
        stockRepo.delete(stock);
    }

    @Cacheable(value = "stockPage")
    public StockPageResponseDto searchStockPage(String searchParam,String searchType, Pageable pageable) {
        Page<StockDetail> pageStock;
        if(searchType.equals("TickerID")){
            pageStock = stockRepo.findByTickerIdIgnoreCaseContaining(searchParam, pageable);
        } else if (searchType.equals("TickerName")) {
            pageStock = stockRepo.findByStockNameIgnoreCaseContaining(searchParam, pageable);
        } else {
            // search all
            pageStock = stockRepo.findByTickerIdIgnoreCaseContainingOrStockNameIgnoreCaseContaining(searchParam,
            searchParam, pageable);
        }
        
        
        return new StockPageResponseDto(pageStock);
    }

}
