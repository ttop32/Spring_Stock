package com.ttop.spring.stock.service;

import com.ttop.spring.stock.domain.Comment;
import com.ttop.spring.stock.domain.Member;
import com.ttop.spring.stock.domain.StockDetail;
import com.ttop.spring.stock.dto.CommentCreateDto;
import com.ttop.spring.stock.dto.CommentEditDto;
import com.ttop.spring.stock.error.CustomException;
import com.ttop.spring.stock.error.ErrorCode;
import com.ttop.spring.stock.repo.CommentRepo;
import com.ttop.spring.stock.repo.MemberRepo;
import com.ttop.spring.stock.repo.StockDetailRepo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Service
@Transactional
public class CommentService {
    CommentRepo commentRepo;
    MemberRepo memberRepo;
    StockDetailRepo stockRepo;

    public Comment insertComment(CommentCreateDto commentCreateDto, Member member) {
        getMemberInfo(member);

        StockDetail currentStock = getStock(commentCreateDto.getTickerId());
        Comment comment=commentRepo.save(commentCreateDto.toEntity(member, currentStock));
        comment.updateReplyParentId();
        return comment;
    }

    public Comment modifyComment(CommentEditDto commentEditDto, Member member) {
        getMemberInfo(member);

        Comment comment = getComment(commentEditDto.getCommentId());
        // check comment owner is current user
        checkCommentPermission(member, comment);
        comment.changeComment(commentEditDto.getContent());
        return comment;
    }

    public void removeComment(Long commentId, Member member) {
        getMemberInfo(member);

        Comment comment = getComment(commentId);
        checkCommentPermission(member, comment);
        comment.deleteContent();
    }

    public Page<Comment> getCommentPageWithTickerId(String tickerId, Pageable page) {
        StockDetail stock=getStock(tickerId);
        return commentRepo.findByStockIdOrderByReplyParentIdAscIdAsc(stock.getId(), page);
    }

    public Page<Comment> getCommentPage(Long stockId, Pageable page) {
        return commentRepo.findByStockId(stockId, page);
    }

    private StockDetail getStock(String tickerId) {
        return stockRepo.findByTickerId(tickerId).orElseThrow(() -> new CustomException(ErrorCode.STOCK_NOT_FOUND));
    }

    private Comment getComment(Long commentId) {
        return commentRepo.findById(commentId).orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private void checkCommentPermission(Member member, Comment comment) {
        if (!comment.checkCommentOwner(member)) {
            throw new CustomException(ErrorCode.INVALID_USER_PERMISSION);
        }
    }


    private Member getMemberInfo(Member member) {
        if (member == null) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);  
        }
        return memberRepo.findById(member.getId()).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        
    }


}
