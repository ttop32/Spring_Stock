package com.ttop.spring.stock.controller;

import javax.validation.Valid;

import com.ttop.spring.stock.domain.Comment;
import com.ttop.spring.stock.domain.Member;
import com.ttop.spring.stock.dto.CommentCreateDto;
import com.ttop.spring.stock.dto.CommentEditDto;
import com.ttop.spring.stock.service.CommentService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    CommentService commentService;

    // @AuthenticationPrincipal Member member

    @PostMapping("")
    public Comment createComment(@Valid @RequestBody CommentCreateDto commentCreateDto,
            @AuthenticationPrincipal Member member) {
        return commentService.insertComment(commentCreateDto, member);
    }

    @PutMapping("")
    public Comment updateComment(@Valid @RequestBody CommentEditDto commentEditDto,
            @AuthenticationPrincipal Member member) {
        return commentService.modifyComment(commentEditDto, member);
    }

    @DeleteMapping("/{commentId}")
    public String deleteStock(@PathVariable Long commentId, @AuthenticationPrincipal Member member) {
        commentService.removeComment(commentId, member);
        return "done";
    }

    @GetMapping("")
    public Page<Comment> getCommentPage(@RequestParam String tickerId,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {

        return commentService.getCommentPageWithTickerId(tickerId, pageable);
    }

}
