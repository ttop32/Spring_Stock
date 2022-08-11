package com.ttop.spring.stock.dto;


import lombok.*;

import java.time.LocalDateTime;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
// import java.util.regex.Pattern;

import com.ttop.spring.stock.domain.Member;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MemberEmailDto {

    @NotBlank
    @Size(max=200)
    @Email
    private String email;
    
    @NotNull
    private Boolean isFront;
    
}