package com.ttop.spring.stock.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import com.ttop.spring.stock.domain.Comment;
import com.ttop.spring.stock.domain.Member;
import com.ttop.spring.stock.domain.StockDetail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateDto {

    @NotBlank
    private String content;

    @NotBlank
    private String tickerId;

    private Long replyParentId;

    public Comment toEntity(Member member, StockDetail stock) {
        return Comment.builder()
            .member(member)
            .stock(stock)
            .content(content)
            .replyParentId(replyParentId)
            .build();
    }
}
