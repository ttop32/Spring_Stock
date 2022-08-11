package com.ttop.spring.stock.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class CustomPageImpl<T> extends PageImpl<T> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CustomPageImpl(@JsonProperty("content") List<T> content,
        @JsonProperty("number") int page,
        @JsonProperty("size") int size,
        @JsonProperty("totalElements") long total) {

        super(content, PageRequest.of(page, size), total);
    }

    @Override
    public Pageable getPageable(){
        return super.getPageable();
    }
}
