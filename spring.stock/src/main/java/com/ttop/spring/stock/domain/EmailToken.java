package com.ttop.spring.stock.domain;


import java.time.LocalDateTime;


import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class EmailToken {

    private static final long EMAIL_TOKEN_EXPIRATION_TIME_VALUE = 30L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;
    // @Column(columnDefinition = "BINARY(16)")
    // private UUID id;

    
    @Column
    private Long userId;

    @Column
    private String type;

    @Column
    private LocalDateTime expirationDate;

    @Builder
    public EmailToken(Long userId,String type){
        expirationDate = LocalDateTime.now().plusMinutes(EMAIL_TOKEN_EXPIRATION_TIME_VALUE); // 30분후 만료
        this.userId = userId;
        this.type= type;
    }

    public boolean isEmailTokenExpired(){
        return expirationDate.isBefore(LocalDateTime.now());
    }

    public void setTestId(String id){
        this.id=id;
    }
}
