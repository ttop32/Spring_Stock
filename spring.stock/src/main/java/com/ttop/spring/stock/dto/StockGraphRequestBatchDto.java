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
public class StockGraphRequestBatchDto {

    private List<StockGraphRequest> stockGraphRequestBatch;
    
    // @JsonFormat(pattern = "yyyy-MM-dd") // 데이터 포맷 변환
    // private List<List<LocalDate>> dateList;
    
    // private List<String> tickerList;
    
    // private List<HashMap<String, List<Float>>> stockGraphMapBatch;


    public List<String> getColumnNameList(){
        return new ArrayList<>(stockGraphRequestBatch.get(0).getStockGraphMap().keySet());
    }

    public List<String> getTickerList(){
        List<String> tickerList=new ArrayList();
        for (StockGraphRequest StockGraphRequest :stockGraphRequestBatch) {
            tickerList.add(StockGraphRequest.getTicker()            );
        }
        return tickerList;
    }

    //convert to entity list
    public List<StockGraph> toEntity(List<StockDetail> stockList) {


        List<StockGraph> entityList = new ArrayList<>();
        
        // for (StockGraphRequest StockGraphRequest :stockGraphRequestBatch) {

        for (int i = 0; i < stockGraphRequestBatch.size(); i++) {
            StockGraphRequest StockGraphRequest=stockGraphRequestBatch.get(i);
            for (Entry<String, List<Float>> entry : StockGraphRequest.getStockGraphMap().entrySet()) {

                List<Float> axesValueList = entry.getValue();
    
                Iterator<LocalDate> dateIterator = StockGraphRequest.getDateList().iterator();
                Iterator<Float> axesValueIterator = axesValueList.iterator();
                while (axesValueIterator.hasNext() && dateIterator.hasNext()) {
    
                    StockGraph stockGraph = StockGraph.builder().stock(stockList.get(i)).date(dateIterator.next())
                            .axesType(entry.getKey()).axesValue(axesValueIterator.next()).build();
    
                    entityList.add(stockGraph);
                }
            }   
        }





        return entityList;
    }

    @Getter
    public static class StockGraphRequest {
        @JsonFormat(pattern = "yyyy-MM-dd") // 데이터 포맷 변환
        private List<LocalDate> dateList;
        
        private String ticker;
        
        private HashMap<String, List<Float>> stockGraphMap;

    }

}
