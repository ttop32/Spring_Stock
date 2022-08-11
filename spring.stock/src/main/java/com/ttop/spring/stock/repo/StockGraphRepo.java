package com.ttop.spring.stock.repo;

import java.time.LocalDate;
import java.util.List;

import com.ttop.spring.stock.domain.StockGraph;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StockGraphRepo extends JpaRepository<StockGraph, Long> {

    List<StockGraph> findByStockTickerIdOrderByDateAsc(String tickerId);

    List<StockGraph> findByStockTickerIdAndDateAfterOrderByDateAsc(String tickerId, LocalDate date);

    List<StockGraph> findByStockTickerIdAndDateBetween(String tickerId, LocalDate startDate, LocalDate endDate);


    List<StockGraph> findByStockTickerIdAndAxesTypeAndDateBetween(String tickerId, String axesType, LocalDate startDate, LocalDate endDate);


}