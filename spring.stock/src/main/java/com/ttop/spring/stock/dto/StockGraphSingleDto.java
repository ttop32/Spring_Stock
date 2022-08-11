package com.ttop.spring.stock.dto;


import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
public class StockGraphSingleDto {

    // https://www.baeldung.com/spring-boot-formatting-json-dates
    @NotBlank
    private String tickerId;

    @Size(min=1,max=100)
    private String axesType;

    @NotNull
    private float axesValue;

    @JsonFormat(pattern = "yyyy-MM-dd") //데이터 포맷 변환 
    private LocalDate date;

    public StockGraph toEntity(StockDetail stock){
        return StockGraph.builder().axesType(axesType).axesValue(axesValue).date(date).stock(stock)
                .build();
    }
}


