package com.ttop.spring.stock.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.ttop.spring.stock.domain.StockDetail;

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
public class StockDetailDto {

    @NotBlank
    private String tickerId;

    @Size(min=1,max=100)
    @NotBlank
    private String stockName;
    
    public StockDetail toEntity(){
        return StockDetail.builder().stockName(stockName).tickerId(tickerId)
                .build();
    }

}


