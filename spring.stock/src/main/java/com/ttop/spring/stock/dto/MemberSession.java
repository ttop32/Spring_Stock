package com.ttop.spring.stock.dto;

import java.io.Serializable;

import com.ttop.spring.stock.domain.Member;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class MemberSession implements Serializable {
    private String name;
    private String email;
    private String picture;

    public MemberSession(Member member) {
        this.name = member.getName();
        this.email = member.getEmail();
        this.picture = member.getPicture();
    }
}