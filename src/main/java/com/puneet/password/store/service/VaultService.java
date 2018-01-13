package com.puneet.password.store.service;


import com.puneet.password.store.dao.VaultEntryDao;
import com.puneet.password.store.dao.UserEncryptionKeysDao;
import com.puneet.password.store.dao.UserDetailsDao;
import com.puneet.password.store.hash.HashCreator;
import com.puneet.password.store.model.UserEncryptionKeys;
import com.puneet.password.store.model.VaultEntryVo;
import com.puneet.password.store.model.UserDetailsVo;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VaultService {


    @Autowired
    private UserDetailsDao userDetailsDao;

    @Autowired
    private VaultEntryDao vaultEntryDao;

    @Autowired
    private HashCreator hashCreator;

    @Autowired
    private UserEncryptionKeysDao userEncryptionKeysDao;


    public Optional<UserDetailsVo> getUserDetailsFrom(String userName, String hashedPassword){
       UserDetailsVo userDetailsVo = userDetailsDao.findByUsernameAndPassword(userName,hashedPassword);
        return userDetailsVo ==null?Optional.empty(): Optional.of(userDetailsVo);
    }

    public void save(UserDetailsVo userDetailsVo){
        userDetailsDao.save(userDetailsVo);
    }


    public void savePasswordEntry(VaultEntryVo storageDetails){

        Optional<UserDetailsVo> optionalUserDetailsVo = getUserDetailsFrom(getUserName(),getHashedPassword());
        if(optionalUserDetailsVo.isPresent()){

            UserDetailsVo userDetailsVo = optionalUserDetailsVo.get();
            VaultEntryVo vaultEntryVo = new VaultEntryVo();
            vaultEntryVo.setSiteName(storageDetails.getSiteName());
            vaultEntryVo.setUsername(storageDetails.getUsername());
            vaultEntryVo.setPassword(encrypt(storageDetails.getPassword()));
            vaultEntryVo.setSiteUrl(storageDetails.getSiteUrl());
            userDetailsVo.addPasswordStorageDetail(vaultEntryVo);
            save(userDetailsVo);
        }else{
            throw new RuntimeException("User does not exists...");
        }

    }

    private String encrypt(String password) {

       Pair<String,String> pairOfDecryptedSalt = getDecryptedValues();
        return hashCreator.encrypt(password,pairOfDecryptedSalt.getLeft(),pairOfDecryptedSalt.getRight());
    }

    private Pair<String, String> getDecryptedValues() {
        UserEncryptionKeys userEncryptionKeys = userEncryptionKeysDao.findByUserName(userDetailsDao.findByUsername(getUserName()));
        String decryptedSalt = hashCreator.decrypt(userEncryptionKeys.getSalt(), getPassword(), getUserName());
        String decryptedInitVector = hashCreator.decrypt(userEncryptionKeys.getInitVector(),getPassword(),getUserName());
        return Pair.of(decryptedSalt,decryptedInitVector);
    }


    public void updatePasswordEntry(VaultEntryVo storageDetails){


        VaultEntryVo vaultEntryVo = vaultEntryDao.findOne(storageDetails.getId());
            vaultEntryVo.setSiteName(storageDetails.getSiteName());
            vaultEntryVo.setUsername(storageDetails.getUsername());
            vaultEntryVo.setPassword(encrypt(storageDetails.getPassword()));
            vaultEntryVo.setSiteUrl(storageDetails.getSiteUrl());
            vaultEntryDao.save(vaultEntryVo);
    }


    public List<VaultEntryVo> getAllEntries() {
        Optional<UserDetailsVo> optionalUserDetailsVo = getUserDetailsFrom(getUserName(),getHashedPassword());
        if(optionalUserDetailsVo.isPresent()){

            UserDetailsVo userDetailsVo = optionalUserDetailsVo.get();
            List<VaultEntryVo> siteDetails = userDetailsVo.getSiteDetailList();
            return siteDetails.stream().map(passwordStorageDetail ->  decrypt(passwordStorageDetail)).collect(Collectors.toList());


        }else{
            throw new RuntimeException("User does not exists...");
        }

    }

    private VaultEntryVo decrypt(VaultEntryVo vaultEntryVo) {
        String encryptedPassword = vaultEntryVo.getPassword();
        Pair<String,String> pairOfDecryptedSalt = getDecryptedValues();
        vaultEntryVo.setPassword(hashCreator.decrypt(encryptedPassword,pairOfDecryptedSalt.getLeft(),pairOfDecryptedSalt.getRight()));
        return vaultEntryVo;
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
