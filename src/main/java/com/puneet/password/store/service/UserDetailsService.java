package com.puneet.password.store.service;


import com.puneet.password.store.dao.UserDetailsDao;
import com.puneet.password.store.hash.HashCreator;
import com.puneet.password.store.model.UserDetailsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsService {

@Autowired
    private UserDetailsDao userDetailsDao;


@Autowired
private HashCreator hashCreator;


    public Optional<UserDetailsVo> getUserDetails(){
        UserDetailsVo userDetailsVo = userDetailsDao.findByUsernameAndPassword(getUserFromSession(),getHashedPassword());
        return userDetailsVo ==null?Optional.empty(): Optional.of(userDetailsVo);
    }

    private String getUserFromSession(){
        Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();
        return authenticationToken.getName();

    }

    public String getUserName(){
        UserDetailsVo userDetailsVo = getLoggedInUser();
        return userDetailsVo.getUsername();

    }
    private String getHashedPassword(){
        return hashCreator.createHashFrom(getPassword());
    }

    public String getPassword(){
        Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();
        return authenticationToken.getCredentials().toString();
    }

    public UserDetailsVo getLoggedInUser(){
       return getUserDetails().orElseThrow(()-> new RuntimeException("No User Exists with the user Name"));
    }


    public Optional<UserDetailsVo> getUserDetailsFrom(String username, String password) {
        return Optional.ofNullable(userDetailsDao.findByUsernameAndPassword(username, password));
    }

    public Optional<UserDetailsVo> findUserByUserName(String username) {
        return Optional.ofNullable(userDetailsDao.findByUsername(username));
    }

    public void save(UserDetailsVo userDetailsVo) {
        userDetailsDao.save(userDetailsVo);
    }
}
