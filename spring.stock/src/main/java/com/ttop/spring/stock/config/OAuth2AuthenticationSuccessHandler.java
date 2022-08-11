package com.ttop.spring.stock.config;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

// https://jyami.tistory.com/121

@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {


        String targetUrl = determineTargetUrl(request, response, authentication);



        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        
        Cookie cookie = new Cookie("X-AUTH-TOKEN", "ddddddddd");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        // cookie.setSecure(true);
        response.addCookie(cookie);


        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);


        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new RuntimeException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());


        // String token = RequestContextHolder.currentRequestAttributes().getSessionId();

        // String token = tokenProvider.createToken(authentication);

        HttpSession session =     request.getSession(true);
        log.debug(session.getId());
        String token="";
        

        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("SESSION")) {
                log.debug(cookie.getValue());
                token=cookie.getValue();        
            }
           }

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        // httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return true;
        // getAuthorizedRedirectUris()
        //         .stream()
        //         .anyMatch(authorizedRedirectUri -> {
        //             // Only validate host and port. Let the clients use different paths if they want to
        //             URI authorizedURI = URI.create(authorizedRedirectUri);
        //             if(authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
        //                     && authorizedURI.getPort() == clientRedirectUri.getPort()) {
        //                 return true;
        //             }
        //             return false;
        //         });
    }

    private List<String> getAuthorizedRedirectUris(){
        
        return  Arrays.asList("","","");

    }
}