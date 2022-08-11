package com.ttop.spring.stock.dto;

import com.ttop.spring.stock.domain.Role;



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
public class MemberRoleDto {

    private String targetMemberEmail;
    private Role role;

}
