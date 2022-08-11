package com.ttop.spring.stock.domain;

import java.time.LocalDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@ToString
public class StockGraph {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private StockDetail stock;

    @Column(length = 30)
    private String axesType;

    @Column
    private Float axesValue;

    @Column
    private LocalDate date;

    @Builder
    public StockGraph(StockDetail stock, String axesType,Float axesValue, LocalDate date) {
        this.stock = stock;
        this.axesType = axesType;
        this.axesValue = axesValue;
        this.date = date;
    }
}
