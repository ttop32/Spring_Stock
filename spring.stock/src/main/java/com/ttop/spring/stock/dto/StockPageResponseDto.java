package com.ttop.spring.stock.dto;

import java.util.List;

import com.ttop.spring.stock.domain.StockDetail;

import org.springframework.data.domain.Page;

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
public class StockPageResponseDto {

    private List<StockDetail> contents;
    private long totalElements;
    private int totalPages;
    private int size;
    private int page;
    

    public StockPageResponseDto(Page<StockDetail> stockPage) {
        this.contents=stockPage.getContent();
        this.page=stockPage.getNumber();
        this.size=stockPage.getSize();
        this.totalElements=stockPage.getTotalElements();
        this.totalPages=stockPage.getTotalPages();
    }
}
