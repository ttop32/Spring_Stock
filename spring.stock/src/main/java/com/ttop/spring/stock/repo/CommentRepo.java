package com.ttop.spring.stock.repo;

import com.ttop.spring.stock.domain.Comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepo extends JpaRepository<Comment, Long> {
    
    Page<Comment> findByStockId(Long stockId, Pageable pageable );


    Page<Comment> findByStockIdOrderByReplyParentIdAscIdAsc(Long stockId, Pageable pageable );


}