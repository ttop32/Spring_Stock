package com.ttop.spring.stock.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;



@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Comment  extends BaseTimeEntity{
    
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    private StockDetail stock;

    @ManyToOne
    private Member member;

    @Column(length = 300)
    private String content;

    @Column
    private Long replyParentId;

    @Column
    private Boolean isReply;

    @Column
    private Boolean isDeleted;

    @Column
    private Boolean isModified;

    @Column
    private int upVote;

    @Column
    private int downVote;

    


    @Builder
    public Comment(StockDetail stock, Member member, String content, Long replyParentId) {
        this.stock=stock;
        this.member=member;
        this.content = content;
        this.replyParentId=replyParentId;
        this.isDeleted=false;
        this.isReply=replyParentId==null||replyParentId==0 ? false : true;
        this.isModified=false;
    }

    public void deleteContent(){
        content="deleted comment";
        member=null;
        isDeleted=true;
    }

    public Boolean checkCommentOwner(Member member){
        return this.member.getId()==member.getId();
    }

    public void updateReplyParentId(){
        if(this.replyParentId==null||this.replyParentId==0){
            this.replyParentId=this.id;
        }
    }


    public Comment changeComment(String content){
        this.isModified=true;
        this.content=content;  
        return this; 
    }

}

