package com.puneet.password.store.service;


import com.puneet.password.store.dao.VaultEntryDao;
import com.puneet.password.store.dao.UserEncryptionKeysDao;
import com.puneet.password.store.dao.UserDetailsDao;
import com.puneet.password.store.hash.HashCreator;
import com.puneet.password.store.model.UserEncryptionKeys;
import com.puneet.password.store.model.VaultEntryVo;
import com.puneet.password.store.model.UserDetailsVo;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
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
    private UserDetailsService userDetailsService;

    @Autowired
    private VaultEntryDao vaultEntryDao;

    @Autowired
    private HashCreator hashCreator;

    @Autowired
    private UserEncryptionKeysDao userEncryptionKeysDao;



    public void savePasswordEntry(VaultEntryVo storageDetails){

            VaultEntryVo vaultEntryVo = new VaultEntryVo();
            vaultEntryVo.setSiteName(storageDetails.getSiteName());
            vaultEntryVo.setUsername(userDetailsService.getUserName());
            vaultEntryVo.setPassword(encrypt(storageDetails.getPassword()));
            vaultEntryVo.setSiteUrl(storageDetails.getSiteUrl());
            vaultEntryVo.setUser(userDetailsService.getLoggedInUser());
            vaultEntryDao.save(vaultEntryVo);
    }

    public List<VaultEntryVo> getAllEntries() {

        List<VaultEntryVo> siteDetails = vaultEntryDao.findByUser_Id(userDetailsService.getLoggedInUser().getId());
        return transformPassword(siteDetails);
    }

    private List<VaultEntryVo> transformPassword(List<VaultEntryVo> siteDetails) {
        List<VaultEntryVo> allEntries =  siteDetails.stream().map(passwordStorageDetail ->  createTransformedObj(passwordStorageDetail)).collect(Collectors.toList());
        return allEntries;
    }

    private  VaultEntryVo createTransformedObj(VaultEntryVo passwordStorageDetail) {
        VaultEntryVo newPasswordStorageDetail = new VaultEntryVo();
        BeanUtils.copyProperties(passwordStorageDetail,newPasswordStorageDetail);
        newPasswordStorageDetail= (decrypt(newPasswordStorageDetail));
        return newPasswordStorageDetail;
    }


    public void updatePasswordEntry(VaultEntryVo storageDetails){


        VaultEntryVo vaultEntryVo = vaultEntryDao.findOne(storageDetails.getId());
        vaultEntryVo.setSiteName(storageDetails.getSiteName());
        vaultEntryVo.setUsername(storageDetails.getUsername());
        vaultEntryVo.setPassword(encrypt(storageDetails.getPassword()));
        vaultEntryVo.setSiteUrl(storageDetails.getSiteUrl());
        vaultEntryVo.setUser(userDetailsService.getLoggedInUser());
        vaultEntryDao.save(vaultEntryVo);
    }


    private Pair<String, String> getDecryptedValuesForSaltAndIV() {
        UserEncryptionKeys userEncryptionKeys = userEncryptionKeysDao.findByUserName(userDetailsService.getLoggedInUser());
        String decryptedSalt = hashCreator.decrypt(userEncryptionKeys.getSalt(), userDetailsService.getPassword(), userDetailsService.getUserName());
        String decryptedInitVector = hashCreator.decrypt(userEncryptionKeys.getInitVector(),userDetailsService.getPassword(), userDetailsService.getUserName());
        return Pair.of(decryptedSalt,decryptedInitVector);
    }



    private String encrypt(String password) {

        Pair<String,String> pairOfDecryptedSaltAndIV = getDecryptedValuesForSaltAndIV();
        return hashCreator.encrypt(password,pairOfDecryptedSaltAndIV.getLeft(),pairOfDecryptedSaltAndIV.getRight());
    }

    private VaultEntryVo decrypt(VaultEntryVo vaultEntryVo) {

        String encryptedPassword = vaultEntryVo.getPassword();
        Pair<String,String> pairOfDecryptedSalt = getDecryptedValuesForSaltAndIV();
        vaultEntryVo.setPassword(hashCreator.decrypt(encryptedPassword,pairOfDecryptedSalt.getLeft(),pairOfDecryptedSalt.getRight()));
        return vaultEntryVo;
    }





}
