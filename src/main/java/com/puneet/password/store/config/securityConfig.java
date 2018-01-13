package com.puneet.password.store.config;

import com.puneet.password.store.hash.HashCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class securityConfig extends WebSecurityConfigurerAdapter {



    @Autowired
    private PasswordStoreAuthenticationProvider passwordStoreAuthenticationProvider;

    @Autowired
    HashCreator hashCreator;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.authorizeRequests()
                .antMatchers("/signup").permitAll()
                .antMatchers("/publicKey").permitAll()
                .antMatchers("/usernameexists/*").permitAll()
                .antMatchers( "/css/**" , "/**/js/**", "/**/*.js","/images/**" ,"/app/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                    .loginPage("/login").permitAll()
                .and().logout().logoutUrl("/logout").addLogoutHandler((request, response, authentication) -> hashCreator.removeKeys(request.getSession().getId())).invalidateHttpSession(true).permitAll()
                .permitAll();
               /* .and().logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")).invalidateHttpSession(true).permitAll();*/
    }


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.eraseCredentials(false);
        auth.authenticationProvider(passwordStoreAuthenticationProvider);
    }


}
