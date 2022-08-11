package com.ttop.spring.stock.repo;

import java.util.Optional;

import com.ttop.spring.stock.domain.StockDetail;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockDetailRepo extends JpaRepository<StockDetail, Long> {
    
    Optional<StockDetail> findByTickerId(String tickerId);

    Page<StockDetail> findByTickerIdIgnoreCaseContaining(String tickerId, Pageable pageable );

    Page<StockDetail> findByStockNameIgnoreCaseContaining(String stockName, Pageable pageable);

    Page<StockDetail> findByTickerIdIgnoreCaseContainingOrStockNameIgnoreCaseContaining(String tickerId, String stockName, Pageable pageable);
}