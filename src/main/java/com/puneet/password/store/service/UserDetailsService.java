package com.puneet.password.store.service;


import com.puneet.password.store.dao.PasswordDetailsDao;
import com.puneet.password.store.dao.UserDetailsDao;
import com.puneet.password.store.hash.HashCreator;
import com.puneet.password.store.model.SiteDetailVo;
import com.puneet.password.store.model.UserDetailsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserDetailsService {


    @Autowired
    private UserDetailsDao userDetailsDao;

    @Autowired
    private PasswordDetailsDao passwordDetailsDao;

    @Autowired
    private HashCreator hashCreator;


    public Optional<UserDetailsVo> getUserDetailsFrom(String userName, String hashedPassword){
       UserDetailsVo userDetailsVo = userDetailsDao.findByUsernameAndPassword(userName,hashedPassword);
        return userDetailsVo ==null?Optional.empty(): Optional.of(userDetailsVo);
    }

    public void save(UserDetailsVo userDetailsVo){
        userDetailsDao.save(userDetailsVo);
    }


    public void savePasswordEntry(SiteDetailVo storageDetails){

        Optional<UserDetailsVo> optionalUserDetailsVo = getUserDetailsFrom(getUserName(),getHashedPassword());
        if(optionalUserDetailsVo.isPresent()){

            UserDetailsVo userDetailsVo = optionalUserDetailsVo.get();
            SiteDetailVo siteDetailVo = new SiteDetailVo();
            siteDetailVo.setSiteName(storageDetails.getSiteName());
            siteDetailVo.setUsername(storageDetails.getUsername());
            siteDetailVo.setPassword(encrypt(storageDetails.getPassword()));
            siteDetailVo.setSiteUrl(storageDetails.getSiteUrl());
            userDetailsVo.addPasswordStorageDetail(siteDetailVo);
            save(userDetailsVo);
        }else{
            throw new RuntimeException("User does not exists...");
        }

    }

    private String encrypt(String password) {
        return hashCreator.encrypt(password,getPassword(),getUserName());
    }


    public void updatePasswordEntry(SiteDetailVo storageDetails){


        SiteDetailVo siteDetailVo = passwordDetailsDao.findOne(storageDetails.getId());
            siteDetailVo.setSiteName(storageDetails.getSiteName());
            siteDetailVo.setUsername(storageDetails.getUsername());
            siteDetailVo.setPassword(encrypt(storageDetails.getPassword()));
            siteDetailVo.setSiteUrl(storageDetails.getSiteUrl());
            passwordDetailsDao.save(siteDetailVo);
    }


    public List<SiteDetailVo> getAllEntries() {
        Optional<UserDetailsVo> optionalUserDetailsVo = getUserDetailsFrom(getUserName(),getHashedPassword());
        if(optionalUserDetailsVo.isPresent()){

            UserDetailsVo userDetailsVo = optionalUserDetailsVo.get();
            List<SiteDetailVo> siteDetails = userDetailsVo.getSiteDetailList();
            return siteDetails.stream().map(passwordStorageDetail ->  decrypt(passwordStorageDetail)).collect(Collectors.toList());


        }else{
            throw new RuntimeException("User does not exists...");
        }

    }

    private SiteDetailVo decrypt(SiteDetailVo siteDetailVo) {
        String encryptedPassword = siteDetailVo.getPassword();
        siteDetailVo.setPassword(hashCreator.decrypt(encryptedPassword,getPassword(),getUserName()));
        return siteDetailVo;
    }

    public String getUserName(){
        Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();
        return authenticationToken.getName();

    }
    public String getHashedPassword(){
        Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();
        return hashCreator.createHashFrom(authenticationToken.getCredentials().toString());
    }
    public String getPassword(){
        Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();
        return authenticationToken.getCredentials().toString();
    }


    public Optional<UserDetailsVo> findUserByUserName(String username){
        UserDetailsVo  userDetailsVo = userDetailsDao.findByUsername(username);

        return userDetailsVo == null ? Optional.empty():Optional.of(userDetailsVo);
    }
}
