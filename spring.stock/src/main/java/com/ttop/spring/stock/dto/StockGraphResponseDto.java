package com.ttop.spring.stock.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ttop.spring.stock.domain.StockGraph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockGraphResponseDto {

    private String tickerId;

    // @JsonFormat(pattern = "yyyy-MM-dd") // 데이터 포맷 변환
    // private List<LocalDate> dateList;

    @JsonFormat(pattern = "yyyy-MM-dd") // 데이터 포맷 변환
    private TreeSet<LocalDate> dateSet;

    private HashMap<String, List<Float>> stockGraphMap;

    public StockGraphResponseDto(String tickerId, List<StockGraph> stockGraphList) {
        this.tickerId = tickerId;
        this.stockGraphMap = new HashMap<>();
        this.dateSet = new TreeSet<>();
        for (StockGraph stockGraph : stockGraphList) {
            // add axes value
            String axesType = stockGraph.getAxesType();
            List<Float> stockList = stockGraphMap.getOrDefault(axesType, new ArrayList<>());
            stockList.add(stockGraph.getAxesValue());
            this.stockGraphMap.put(axesType, stockList);

            // add date
            this.dateSet.add(stockGraph.getDate());
        }
    }
}
