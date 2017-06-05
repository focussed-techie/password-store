package com.puneet.password.store.config;

import com.puneet.password.store.hash.HashCreator;
import com.puneet.password.store.model.UserDetailsVo;
import com.puneet.password.store.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Component
public class PasswordStoreAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private HashCreator hashCreator;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

    }


    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        Optional<UserDetailsVo> userDetailsOptional = userDetailsService.getUserDetailsFrom(authentication.getName(),hashCreator.createHashFrom(authentication.getCredentials().toString()));
        if(userDetailsOptional.isPresent()) {
            Collection<GrantedAuthority> grantedAuthorityCollection = new ArrayList<>();
            grantedAuthorityCollection.add(() -> "ROLE_USER");
            UserDetails userDetails = new User(username, authentication.getCredentials().toString(), grantedAuthorityCollection);
            return userDetails;
        }else {
            throw new BadCredentialsException("Username or password does not match");
        }
    }

}
