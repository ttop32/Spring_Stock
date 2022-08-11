package com.ttop.spring.stock.repo;

import static org.assertj.core.api.Assertions.assertThat;

import com.ttop.spring.stock.domain.StockDetail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Transactional
public class StockDetailRepoTest {

    @Autowired
    StockDetailRepo stockRepo;

    StockDetail stockDetail;
    Pageable pageable;

    @BeforeEach
    public void setUp() throws Exception {
        stockDetail = StockDetail.builder().stockName("stockName1").tickerId("tickerId1").build();
        stockRepo.save(stockDetail);

        pageable = PageRequest.of(0, 10, Direction.ASC, "id");
    }

    @Test
    public void testFindByTickerId() {
        // given

        // when
        StockDetail stockResult = stockRepo.findByTickerId(stockDetail.getTickerId()).get();

        // then
        assertThat(stockResult).isNotNull();
        assertThat(stockResult.getStockName()).isEqualTo(stockDetail.getStockName());
    }

    @Test
    public void testFindByTickerIdIgnoreCaseContaining() {
        // given

        // when
        Page<StockDetail> stockResult = stockRepo.findByTickerIdIgnoreCaseContaining(stockDetail.getTickerId(),
                pageable);

        // then
        assertThat(stockResult).isNotNull();
        assertThat(stockResult.getContent().get(0).getTickerId()).isEqualTo(stockDetail.getTickerId());
        assertThat(stockResult.getPageable().getPageNumber()).isEqualTo(pageable.getPageNumber());
        assertThat(stockResult.getPageable().getPageSize()).isEqualTo(pageable.getPageSize());
    }

    @Test
    public void testFindByStockNameIgnoreCaseContaining() {
        // given

        // when
        Page<StockDetail> stockResult = stockRepo.findByStockNameIgnoreCaseContaining(stockDetail.getStockName(),
                pageable);

        // then
        assertThat(stockResult).isNotNull();
        assertThat(stockResult.getContent().get(0).getStockName()).isEqualTo(stockDetail.getStockName());
        assertThat(stockResult.getPageable().getPageNumber()).isEqualTo(pageable.getPageNumber());
        assertThat(stockResult.getPageable().getPageSize()).isEqualTo(pageable.getPageSize());
    }

    @Test
    public void testFindByTickerIdIgnoreCaseContainingOrStockNameIgnoreCaseContaining() {
        // given

        // when
        Page<StockDetail> stockResult = stockRepo.findByTickerIdIgnoreCaseContainingOrStockNameIgnoreCaseContaining(
                stockDetail.getStockName(), "", pageable);

        // then
        assertThat(stockResult).isNotNull();
        assertThat(stockResult.getContent().get(0).getStockName()).isEqualTo(stockDetail.getStockName());
        assertThat(stockResult.getPageable().getPageNumber()).isEqualTo(pageable.getPageNumber());
        assertThat(stockResult.getPageable().getPageSize()).isEqualTo(pageable.getPageSize());
        assertThat(stockResult.getNumberOfElements()).isEqualTo(1);

    }

}
