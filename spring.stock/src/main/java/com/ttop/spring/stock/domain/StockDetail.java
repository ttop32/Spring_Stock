package com.ttop.spring.stock.domain;

import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class StockDetail  extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(length=30,nullable = false, unique = true)
    private String tickerId;

    @Column(length=50,nullable = false)
    private String stockName;

    @Column
    private Long viewCount;
    
    @JsonIgnore
    @OneToMany(mappedBy = "stock", cascade = CascadeType.REMOVE, orphanRemoval=true, fetch = FetchType.LAZY)
    private List<StockGraph> stockGraphs;
    

    @Builder
    public StockDetail(String tickerId, String stockName) {
        this.tickerId=tickerId;
        this.stockName = stockName;
        this.viewCount=0L;
    }

    public StockDetail updateStcokName(String stockName){
        this.stockName=stockName;
        return this;
    }
}
