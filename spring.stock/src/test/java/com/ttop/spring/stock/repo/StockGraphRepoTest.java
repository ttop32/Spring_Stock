package com.ttop.spring.stock.repo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import com.ttop.spring.stock.domain.StockDetail;
import com.ttop.spring.stock.domain.StockGraph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@DataJpaTest
public class StockGraphRepoTest {

    @Autowired
    StockGraphRepo stockGraphRepo;

    @Autowired
    StockDetailRepo stockDetailRepo;

    StockDetail stockDetail;
    StockGraph stockGraph;

    @BeforeEach
    public void setUp() throws Exception {

        stockDetail = StockDetail.builder().stockName("stockName1").tickerId("tickerId1").build();
        stockDetailRepo.save(stockDetail);

        stockGraph = StockGraph.builder().date(LocalDate.of(2018, 2, 2)).axesType("high").axesValue(3f)
                .stock(stockDetail).build();
        stockGraphRepo.save(stockGraph);

    }

    @Test
    public void testFindByStockTickerIdOrderByDateAsc() {
        // given

        // when
        List<StockGraph> stockGraphListResult = stockGraphRepo
                .findByStockTickerIdOrderByDateAsc(stockDetail.getTickerId());

        // then
        assertThat(stockGraphListResult).isNotNull();
        assertThat(stockGraphListResult.get(0).getAxesValue()).isEqualTo(stockGraph.getAxesValue());
    }

    @Test
    public void testFindByStockTickerIdAndDateAfterOrderByDateAsc() {
        // given
        LocalDate localDateInput = LocalDate.of(2015, 2, 3);

        // when
        List<StockGraph> stockGraphListResult = stockGraphRepo
                .findByStockTickerIdAndDateAfterOrderByDateAsc(stockDetail.getTickerId(), localDateInput);

        // then
        assertThat(stockGraphListResult).isNotNull();
        assertThat(stockGraphListResult.get(0).getAxesValue()).isEqualTo(stockGraph.getAxesValue());
    }

    @Test
    public void testFindByStockTickerIdAndDateBetween() {
        // given
        LocalDate startDate = LocalDate.of(2015, 2, 3);
        LocalDate endDate = LocalDate.of(2020, 2, 3);

        // when
        List<StockGraph> stockGraphListResult = stockGraphRepo
                .findByStockTickerIdAndDateBetween(stockDetail.getTickerId(), startDate, endDate);

        // then
        assertThat(stockGraphListResult).isNotNull();
        assertThat(stockGraphListResult.get(0).getAxesValue()).isEqualTo(stockGraph.getAxesValue());
    }

}
