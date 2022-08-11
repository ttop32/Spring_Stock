package com.ttop.spring.stock.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

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
@Builder
@AllArgsConstructor
public class MemberSignInDto {
    
    @NotNull
    private String username;
    
    @NotNull
    private String password;



    public UsernamePasswordAuthenticationToken toAuthToken(){
        return new UsernamePasswordAuthenticationToken(username, password);
    }
    
}