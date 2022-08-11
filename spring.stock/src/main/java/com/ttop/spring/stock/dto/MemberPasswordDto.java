package com.ttop.spring.stock.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;



@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberPasswordDto {    
    @NotEmpty( groups={PwCheck.class, EmailCheck.class})
    @Size(min = 8, max = 100, groups={PwCheck.class, EmailCheck.class})
    private String password;

    @NotEmpty(groups = {PwCheck.class})
    private String previousPassword;

    @NotEmpty(groups = {EmailCheck.class})
    private String emailTokenId;


    public interface EmailCheck {
    }
    public interface PwCheck {
    }
    
}


