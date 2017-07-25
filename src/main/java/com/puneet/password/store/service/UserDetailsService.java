package com.puneet.password.store.service;


import com.puneet.password.store.dao.PasswordDetailsDao;
import com.puneet.password.store.dao.UserDetailsDao;
import com.puneet.password.store.hash.HashCreator;
import com.puneet.password.store.model.EntryDetailVo;
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


    public void savePasswordEntry(EntryDetailVo storageDetails){

        Optional<UserDetailsVo> optionalUserDetailsVo = getUserDetailsFrom(getUserName(),getHashedPassword());
        if(optionalUserDetailsVo.isPresent()){

            UserDetailsVo userDetailsVo = optionalUserDetailsVo.get();
            EntryDetailVo entryDetailVo = new EntryDetailVo();
            entryDetailVo.setSiteName(storageDetails.getSiteName());
            entryDetailVo.setUsername(storageDetails.getUsername());
            entryDetailVo.setPassword(encrypt(storageDetails.getPassword()));
            entryDetailVo.setSiteUrl(storageDetails.getSiteUrl());
            entryDetailVo.setDescription(storageDetails.getDescription());
            userDetailsVo.addPasswordStorageDetail(entryDetailVo);
            save(userDetailsVo);
        }else{
            throw new RuntimeException("User does not exists...");
        }

    }

    private String encrypt(String password) {
        return hashCreator.encrypt(password,getPassword(),getUserName());
    }


    public void updatePasswordEntry(EntryDetailVo storageDetails){


        EntryDetailVo entryDetailVo = passwordDetailsDao.findOne(storageDetails.getId());
            entryDetailVo.setSiteName(storageDetails.getSiteName());
            entryDetailVo.setUsername(storageDetails.getUsername());
            entryDetailVo.setPassword(encrypt(storageDetails.getPassword()));
            entryDetailVo.setSiteUrl(storageDetails.getSiteUrl());
            entryDetailVo.setDescription(storageDetails.getDescription());
            passwordDetailsDao.save(entryDetailVo);
    }


    public List<EntryDetailVo> getAllEntries() {
        Optional<UserDetailsVo> optionalUserDetailsVo = getUserDetailsFrom(getUserName(),getHashedPassword());
        if(optionalUserDetailsVo.isPresent()){

            UserDetailsVo userDetailsVo = optionalUserDetailsVo.get();
            List<EntryDetailVo> siteDetails = userDetailsVo.getSiteDetailList();
            return siteDetails.stream().map(passwordStorageDetail ->  decrypt(passwordStorageDetail)).collect(Collectors.toList());


        }else{
            throw new RuntimeException("User does not exists...");
        }

    }

    private EntryDetailVo decrypt(EntryDetailVo entryDetailVo) {
        String encryptedPassword = entryDetailVo.getPassword();
        entryDetailVo.setPassword(hashCreator.decrypt(encryptedPassword,getPassword(),getUserName()));
        return entryDetailVo;
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
