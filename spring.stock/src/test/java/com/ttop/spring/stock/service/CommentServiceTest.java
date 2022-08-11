package com.ttop.spring.stock.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.Optional;

import com.ttop.spring.stock.domain.Comment;
import com.ttop.spring.stock.domain.Member;
import com.ttop.spring.stock.domain.StockDetail;
import com.ttop.spring.stock.dto.CommentCreateDto;
import com.ttop.spring.stock.dto.CommentEditDto;
import com.ttop.spring.stock.repo.CommentRepo;
import com.ttop.spring.stock.repo.MemberRepo;
import com.ttop.spring.stock.repo.StockDetailRepo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    CommentRepo commentRepo;
    @Mock
    MemberRepo memberRepo;
    @Mock
    StockDetailRepo stockRepo;

    @InjectMocks
    CommentService commentService;


    

    StockDetail stock;
    Member member;
    CommentCreateDto commentCreateDto;
    CommentEditDto commentEditDto;
    Comment comment;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        stock=StockDetail.builder().tickerId("tickerId2").stockName("stockName3").build();
        member=Member.builder().email("email1").name("name3").password("password3").build();
        commentCreateDto= CommentCreateDto.builder().content("content1").tickerId(stock.getTickerId()).build();
        commentEditDto=CommentEditDto.builder().content("content44").commentId(1L).build();
        comment=commentCreateDto.toEntity(member, stock);


    }


    @Test
    void testGetCommentPage() {

        //given
        Pageable pageable=PageRequest.of(0, 10, Direction.ASC, "id");
        Page<Comment> pageComment= new PageImpl<>(Collections.singletonList(comment), pageable,1);
        given(commentRepo.findByStockId(any(),any())).willReturn(pageComment);
        

        //when
        Page<Comment> pageCommentResult =commentService.getCommentPage(stock.getId(),pageable);

        //then
        assertThat(pageCommentResult).isNotNull();
        assertThat(pageCommentResult.getContent().get(0).getContent()).isEqualTo(commentCreateDto.getContent());
        assertThat(pageCommentResult.getPageable().getPageSize()).isEqualTo(pageable.getPageSize());
        assertThat(pageCommentResult.getPageable().getPageNumber()).isEqualTo(pageable.getPageNumber());

    }

    @Test
    void testInsertComment() {
        //given
        given(commentRepo.save(any())).willReturn(comment);
        given(stockRepo.findByTickerId(any())).willReturn(Optional.of(stock));
        given(memberRepo.findById(any())).willReturn(Optional.of(member));
        
        //when
        Comment commentResult =commentService.insertComment(commentCreateDto,member);

        //then
        assertThat(commentResult).isNotNull();
        assertThat(commentResult.getContent()).isEqualTo(commentCreateDto.getContent());
        assertThat(commentResult.getMember().getEmail()).isEqualTo(member.getEmail());
        assertThat(commentResult.getStock().getStockName()).isEqualTo(stock.getStockName());

    }

    @Test
    void testModifyComment() {
        //given
        given(commentRepo.findById(any())).willReturn(Optional.of(comment));
        given(memberRepo.findById(any())).willReturn(Optional.of(member));

        //when
        Comment commentResult =commentService.modifyComment(commentEditDto,member);

        //then
        assertThat(commentResult).isNotNull();
        assertThat(commentResult.getContent()).isEqualTo(commentEditDto.getContent());
        assertThat(commentResult.getMember().getEmail()).isEqualTo(member.getEmail());
        assertThat(commentResult.getStock().getStockName()).isEqualTo(stock.getStockName());


    }

    @Test
    void testRemoveComment() {
        //given
        given(commentRepo.findById(any())).willReturn(Optional.of(comment));
        given(memberRepo.findById(any())).willReturn(Optional.of(member));
        String prevCommentContent=comment.getContent();

        //when
        commentService.removeComment(comment.getId(),member);

        //then
        assertThat(comment).isNotNull();
        assertThat(comment.getContent()).isNotEqualTo(prevCommentContent);
        assertThat(comment.getIsDeleted()).isTrue();
        assertThat(comment.getMember()).isEqualTo(null);        
    }


    


}
