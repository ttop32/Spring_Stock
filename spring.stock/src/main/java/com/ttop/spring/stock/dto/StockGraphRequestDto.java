package com.ttop.spring.stock.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ttop.spring.stock.domain.StockDetail;
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
public class StockGraphRequestDto {

    
    @JsonFormat(pattern = "yyyy-MM-dd") // 데이터 포맷 변환
    private List<LocalDate> dateList;

    private HashMap<String, List<Float>> stockGraphMap;


    public List<String> getColumnNameList(){
        return new ArrayList<>(stockGraphMap.keySet());
    }

    //convert to entity list
    public List<StockGraph> toEntity(StockDetail stock) {
        List<StockGraph> entityList = new ArrayList<>();

        for (Entry<String, List<Float>> entry : stockGraphMap.entrySet()) {

            List<Float> axesValueList = entry.getValue();

            Iterator<LocalDate> dateIterator = dateList.iterator();
            Iterator<Float> axesValueIterator = axesValueList.iterator();
            while (axesValueIterator.hasNext() && dateIterator.hasNext()) {

                StockGraph stockGraph = StockGraph.builder().stock(stock).date(dateIterator.next())
                        .axesType(entry.getKey()).axesValue(axesValueIterator.next()).build();

                entityList.add(stockGraph);
            }

        }

        return entityList;
    }

}
