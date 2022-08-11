package com.ttop.spring.stock.config;




import com.ttop.spring.stock.service.MemberService;
import com.ttop.spring.stock.service.MemberOAuth2Service;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {



    private MemberService memberService;
    private MemberOAuth2Service memberOAuth2Service;


    // private MemberService memberService;
    // private final CustomOAuth2UserService customOAuth2UserService;
    // private JwtAuthenticationFilter jwtAuthenticationFilter;
    // private JwtSuccessHandler jwtSuccessHandler;
    // private JwtFailureHandler jwtFailureHandler ;
    // private FailureHandler failureHandler;
    // private RESTAuthenticationEntryPoint restAuthenticationEntryPoint;


    // .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
    
    
    // @Component
    // public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    //     @Override
    //     public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) throws IOException,
    //             ServletException {
    //         response.sendRedirect("/exception/entrypoint");
    //     }
    // }


    
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;



    @Override
    public void configure(WebSecurity web) throws Exception
    {
        // static 디렉터리의 하위 파일 목록은 인증 무시 ( = 항상통과 ), csrf 같은것도 상관없이 무시
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**", "/lib/**", "/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()  //post 리퀘스트 받을떄 csrf방지 체크 안함
                // .cors().disable()
                .httpBasic().disable()   //url로 ssh 처럼 로그인 하는것
                .exceptionHandling().accessDeniedPage("/user/denied") // 권한 없을떄 가는 페이지
                // .authenticationEntryPoint(restAuthenticationEntryPoint) //jwt 인증
                .and()  
            // .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and() //session 사용안함

            
            .authorizeRequests()  // 페이지 권한 설정
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/myinfo").hasRole("MEMBER")


                .antMatchers("/stock").permitAll()
                .antMatchers("/stock/editor/**").hasRole("ADMIN")
                .antMatchers("/stockgraph").permitAll()
                .antMatchers("/stockgraph/editor/**").hasRole("ADMIN")
                
                .antMatchers("/**").permitAll()
                .antMatchers("/error").permitAll()
                
                //.anyRequest().authenticated() 모든 리퀘스트는 로그인되어야함
                .and() 
            .formLogin() // 폼 로그인 설정
                .loginPage("/user/login")
                .defaultSuccessUrl("/user/login/result")
                
                // .failureUrl("/user/login?error=true")
                // .failureHandler(authenticationFailureHandler())
                .permitAll()
                
                // .successHandler(jwtSuccessHandler)  //jwt 발급
                // .failureHandler(failureHandler)     
                .and() 
            .logout()// 로그아웃 설정
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                .logoutSuccessHandler((new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)))
                // .logoutSuccessUrl("/user/logout/result")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                // .deleteCookies("X-AUTH-TOKEN")   //delete jwt token
                .and()
            .oauth2Login()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .userInfoEndpoint()
                .userService(memberOAuth2Service);


                // .oauth2Login()
                //     .authorizationEndpoint()
                //         .baseUri("/oauth2/authorize")
                //         .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
                //         .and()
                //     .userInfoEndpoint()
                //         .userService(memberOAuth2Service)
                //         .and()
                //         // .successHandler((req, res, auth) -> res.sendRedirect("http://www.google.com/" +"?accessToken=" + res.getHeader("Access-Token")))

                //         .successHandler(oAuth2AuthenticationSuccessHandler)
                //     .failureHandler(oAuth2AuthenticationFailureHandler);


                    

        // http.addFilterBefore(jwtAuthenticationFilter,  UsernamePasswordAuthenticationFilter.class);  jwt 인증 

        //  설정안하면 기본으로 /login 으로 로그인페이지로 리다이렉트됨 
        
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(memberService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
         return super.authenticationManagerBean();
    }
}