package com.ttop.spring.stock.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum OAuth {
    NATIVE("NATIVE", "일반 로그인"),
    GOOGLE("GOOGLE", "구글 로그인");

    private final String key;
    private final String title;
}