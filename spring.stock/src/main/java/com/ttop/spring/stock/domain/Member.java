
package com.ttop.spring.stock.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member extends BaseTimeEntity implements UserDetails, OAuth2User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(length = 320, nullable = false,unique=true)
    private String email;

    @Column(length = 50)
    private String authType;

    @Column(length = 30)
    private String name;
    
    @Column(length = 20)
    private String nickname;
    
    @Column
    private String picture;

    @JsonIgnore
    @Column(length = 100)
    private String password;

    @JsonIgnore
    @Column
    private Boolean emailVerified;

    @JsonIgnore
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @JsonIgnore
    @OneToMany(mappedBy = "member")
    List<Comment> comments;

    @Builder
    public Member(String name, String nickname, String email, String password, String picture, String authType, Boolean emailVerified) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.picture = picture;
        this.emailVerified = emailVerified==null||emailVerified==false ? false: true;
        this.authType = authType;
        this.roles =  new HashSet<>();
        this.roles.add(Role.MEMBER);

        if(this.authType=="base" || this.authType==null){
            updatePassword(password);
        }else{
            this.password=password;
        }
        grantPermissionHoldBasedOnEmail(email); 
    }

    
    private void grantPermissionHoldBasedOnEmail(String email){

        if(email.equals("ttop324@gmail.com")){
            this.roles.add(Role.ADMIN);
            this.emailVerified=true;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(k -> k.getKey()).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;// return authType +email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return emailVerified;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    public Member updateProfile(String nickname, String picture) {
        this.nickname = (nickname == null) ? this.nickname : nickname;
        this.picture = (picture == null) ? this.picture : picture;

        return this;
    }

    public Member updatePassword(String password) {
        this.password = encryptPassword(password);
        return this;
    }



    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public void verifyEmail() {
        this.emailVerified = true;
    }

    public Boolean isSameMember(Member member) {
        return this.getId() == member.getId();
    }

    public boolean checkMemberHasAdminRole() {
        return this.roles.contains(Role.ADMIN);
    }

    public void setTestId(Long id){
        this.id=id;
    }


    public boolean verifyPassword(String password){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches( password,this.password);
    }

    
    private String encryptPassword(String password){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }
}