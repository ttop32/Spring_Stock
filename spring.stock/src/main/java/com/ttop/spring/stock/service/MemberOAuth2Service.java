package com.ttop.spring.stock.service;

import javax.servlet.http.HttpSession;

import com.ttop.spring.stock.domain.Member;
import com.ttop.spring.stock.dto.MemberOAuthAttributes;
import com.ttop.spring.stock.dto.MemberSession;
import com.ttop.spring.stock.repo.MemberRepo;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberOAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
        // https://velog.io/@swchoi0329/%EC%8A%A4%ED%94%84%EB%A7%81-%EC%8B%9C%ED%81%90%EB%A6%AC%ED%8B%B0%EC%99%80-OAuth-2.0%EC%9C%BC%EB%A1%9C-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EA%B8%B0%EB%8A%A5-%EA%B5%AC%ED%98%84

        private final MemberRepo memberRepo;
        private final HttpSession httpSession;

        @Override
        public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

                // userRequest to OAuth2User
                OAuth2UserService delegate = new DefaultOAuth2UserService();
                OAuth2User oAuth2User = delegate.loadUser(userRequest);

                // resource server id, user id, user detail
                String registrationId = userRequest.getClientRegistration().getRegistrationId();
                String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                                .getUserInfoEndpoint().getUserNameAttributeName();

                MemberOAuthAttributes attributes = MemberOAuthAttributes.of(registrationId, userNameAttributeName,
                                oAuth2User.getAttributes());

                // db 저장
                Member member = saveOrUpdate(attributes);

                // session 저장
                httpSession.setAttribute("user", new MemberSession(member));

                // user entity to DefaultOAuth2User
                return member;// new DefaultOAuth2User(member.getAuthorities(), attributes.getAttributes(),
                              // attributes.getNameAttributeKey());
        }

        @Transactional
        private Member saveOrUpdate(MemberOAuthAttributes attributes) {
                Member member = memberRepo.findByEmail(attributes.getEmail())
                                .map(entity -> entity.updateProfile(attributes.getName(), attributes.getPicture()))
                                .orElse(attributes.toEntity());

                return memberRepo.save(member);
        }
}