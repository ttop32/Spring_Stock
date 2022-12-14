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
        // static ??????????????? ?????? ?????? ????????? ?????? ?????? ( = ???????????? ), csrf ???????????? ???????????? ??????
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**", "/lib/**", "/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()  //post ???????????? ????????? csrf?????? ?????? ??????
                // .cors().disable()
                .httpBasic().disable()   //url??? ssh ?????? ????????? ?????????
                .exceptionHandling().accessDeniedPage("/user/denied") // ?????? ????????? ?????? ?????????
                // .authenticationEntryPoint(restAuthenticationEntryPoint) //jwt ??????
                .and()  
            // .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and() //session ????????????

            
            .authorizeRequests()  // ????????? ?????? ??????
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/myinfo").hasRole("MEMBER")


                .antMatchers("/stock").permitAll()
                .antMatchers("/stock/editor/**").hasRole("ADMIN")
                .antMatchers("/stockgraph").permitAll()
                .antMatchers("/stockgraph/editor/**").hasRole("ADMIN")
                
                .antMatchers("/**").permitAll()
                .antMatchers("/error").permitAll()
                
                //.anyRequest().authenticated() ?????? ??????????????? ?????????????????????
                .and() 
            .formLogin() // ??? ????????? ??????
                .loginPage("/user/login")
                .defaultSuccessUrl("/user/login/result")
                
                // .failureUrl("/user/login?error=true")
                // .failureHandler(authenticationFailureHandler())
                .permitAll()
                
                // .successHandler(jwtSuccessHandler)  //jwt ??????
                // .failureHandler(failureHandler)     
                .and() 
            .logout()// ???????????? ??????
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


                    

        // http.addFilterBefore(jwtAuthenticationFilter,  UsernamePasswordAuthenticationFilter.class);  jwt ?????? 

        //  ??????????????? ???????????? /login ?????? ????????????????????? ?????????????????? 
        
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