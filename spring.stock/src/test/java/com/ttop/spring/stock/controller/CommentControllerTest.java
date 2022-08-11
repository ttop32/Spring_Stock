package com.ttop.spring.stock.controller;



import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ttop.spring.stock.domain.Comment;
import com.ttop.spring.stock.domain.Member;
import com.ttop.spring.stock.domain.Role;
import com.ttop.spring.stock.domain.StockDetail;
import com.ttop.spring.stock.dto.CommentCreateDto;
import com.ttop.spring.stock.dto.CommentEditDto;
import com.ttop.spring.stock.repo.CommentRepo;
import com.ttop.spring.stock.repo.MemberRepo;
import com.ttop.spring.stock.repo.StockDetailRepo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.ResultActions;




public class CommentControllerTest extends IntegrationTest{



    @Autowired
    CommentRepo commentRepo;

    @Autowired
    MemberRepo memberRepo;

    @Autowired
    StockDetailRepo stockRepo;
    

    StockDetail stock;
    Member member;
    Member adminMember;
    CommentCreateDto commentCreateDto;
    CommentEditDto commentEditDto;
    Comment comment;

    @BeforeEach
    public void setUp() throws Exception {

        stock=StockDetail.builder().tickerId("tickerId2").stockName("stockName3").build();
        stock=stockRepo.save(stock);

        
        commentCreateDto= CommentCreateDto.builder().content("content1").tickerId(stock.getTickerId()).build();
        commentEditDto=CommentEditDto.builder().content("content44").commentId(1L).build();
        comment=commentCreateDto.toEntity(member, stock);  

    }



    @BeforeTransaction
    public void accountSetup() {
        String password="5password";
        member=Member.builder().email("member@gmail.com").password(password).build();
        adminMember=Member.builder().email("admin@gmail.com").password(password).build();
        adminMember.addRole(Role.ADMIN);

        member=memberRepo.save(member);
        adminMember=memberRepo.save(adminMember);
    }

    @AfterTransaction
    public void accountCleanup() {
        memberRepo.deleteById(member.getId());
        memberRepo.deleteById(adminMember.getId());
    }








    @WithUserDetails("member@gmail.com")
    @Test
    void testCreateComment() throws Exception {
        //given

        //when
        ResultActions resultActions=requestWithBodyParam(post("/comment/"),commentCreateDto);

        //then
        resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("content").value(commentCreateDto.getContent()));

        
        Comment commentResult=commentRepo.findAll().get(0);
        assertThat(commentResult.getContent()).isEqualTo(commentCreateDto.getContent());
        
    }


    
    @WithUserDetails("member@gmail.com")
    @Test
    void testDeleteStock() throws Exception {
        //given
        commentRepo.save(comment);


        //when
        ResultActions resultActions=requestWithBodyParam(delete("/comment/"+comment.getId()),commentCreateDto);

        //then
        resultActions
        .andExpect(status().isOk());

        assertThat(commentRepo.findById(comment.getId()).isPresent()).isTrue();
    
    }

    @Test
    void testGetCommentPage()  throws Exception {
        //given
        commentRepo.save(comment);


        //when
        ResultActions resultActions=requestWithBodyParam(get("/comment/").param("tickerId",stock.getTickerId()),"");
        

        //then
        resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].content").value(comment.getContent()));


    }

    
    @WithUserDetails("member@gmail.com")
    @Test
    void testUpdateComment() throws Exception {

        //given
        commentRepo.save(comment);
        CommentEditDto commentEditDto=CommentEditDto.builder().commentId(comment.getId()).content("comment53463").build();


        //when
        ResultActions resultActions=requestWithBodyParam(put("/comment/"),commentEditDto);

        //then
        resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("content").value(commentEditDto.getContent()));
        
        
        Comment commentResult=commentRepo.findById(commentEditDto.getCommentId()).get();
        assertThat(commentResult.getContent()).isEqualTo(commentEditDto.getContent());

    }
}
