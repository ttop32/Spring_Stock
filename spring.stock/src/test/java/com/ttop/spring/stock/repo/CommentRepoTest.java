package com.ttop.spring.stock.repo;


import static org.assertj.core.api.Assertions.assertThat;

import com.ttop.spring.stock.domain.Comment;
import com.ttop.spring.stock.domain.Member;
import com.ttop.spring.stock.domain.StockDetail;

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
public class CommentRepoTest {

    @Autowired
    StockDetailRepo stockRepo;
    @Autowired
    CommentRepo commentRepo;
    @Autowired
    MemberRepo memberRepo;


    @Test
    public void testFindByTickerId() {
        // given
        StockDetail stock= StockDetail.builder().stockName("stockName1").tickerId("tickerId1").build();
        Member member= Member.builder().email("email1").password("password1").build();
        Comment comment=Comment.builder().member(member).stock(stock).content("content1").build();
        Pageable pageable= PageRequest.of(0, 10, Direction.ASC, "id");

        stockRepo.save(stock);
        memberRepo.save(member);
        commentRepo.save(comment);
        
        // when
        Page<Comment> commentPages = commentRepo.findByStockId(stock.getId(),pageable);

        // then
        assertThat(commentPages).isNotNull();
        assertThat(commentPages.getContent().get(0).getContent()).isEqualTo(comment.getContent());
    }
}
