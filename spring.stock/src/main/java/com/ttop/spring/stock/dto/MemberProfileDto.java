package com.ttop.spring.stock.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberProfileDto {
    @Size(min=3,max=15)
    @NotBlank
    @Pattern(regexp =  "^[a-zA-Z0-9]+$", message="only allow alphabet and numeric")
    private String nickname;

}
