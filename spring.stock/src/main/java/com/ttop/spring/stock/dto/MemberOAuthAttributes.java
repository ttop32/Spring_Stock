package com.ttop.spring.stock.dto;




import java.util.Map;

import com.ttop.spring.stock.domain.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MemberOAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;
    private String registrationId;

    public static MemberOAuthAttributes of(String registrationId, String userNameAttributeName,
            Map<String, Object> attributes) {


        // if("naver".equals(registrationId)) {
        //     return ofNaver("id", attributes);
        // }

        return ofGoogle(registrationId,userNameAttributeName, attributes);
    }

    private static MemberOAuthAttributes ofGoogle(String registrationId,String userNameAttributeName, Map<String, Object> attributes) {
        return MemberOAuthAttributes.builder().name((String) attributes.get("name")).email((String) attributes.get("email"))
                .picture((String) attributes.get("picture")).attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .registrationId(registrationId).build();
    }

    public Member toEntity() {
        return Member.builder().name(name).email(email).picture(picture).password("oauth2pw")
        .authType(registrationId).emailVerified(true).build();
    }
}