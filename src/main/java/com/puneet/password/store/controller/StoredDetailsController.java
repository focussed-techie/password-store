package com.puneet.password.store.controller;

import com.puneet.password.store.dao.SaltAssociationDao;
import com.puneet.password.store.hash.HashCreator;
import com.puneet.password.store.model.*;
import com.puneet.password.store.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class StoredDetailsController {

    @Autowired
    private UserDetailsService storageManagementService;

    @Autowired
    private HashCreator hashCreator;

    @Autowired
    private SaltAssociationDao saltAssociationDao;

    @RequestMapping(value = "/addNewEntry", method = RequestMethod.POST,consumes = APPLICATION_JSON_VALUE)
    public @ResponseBody HttpStatus addNewEntry(@RequestBody SiteDetailVo storageEntry,HttpSession session){
       storageEntry.setPassword(hashCreator.decryptUsingSymetricKey(storageEntry.getPassword(),session.getId()));
        storageManagementService.savePasswordEntry(storageEntry);
        return HttpStatus.OK;
    }

    @RequestMapping(value = "/saveData", method = RequestMethod.POST,consumes = APPLICATION_JSON_VALUE)
    public @ResponseBody HttpStatus savePasswordChanges(@RequestBody SiteDetailVo storageEntry,HttpSession session){
        storageEntry.setPassword(hashCreator.decryptUsingSymetricKey(storageEntry.getPassword(),session.getId()));
       storageManagementService.updatePasswordEntry(storageEntry);
        return HttpStatus.OK;
    }

    @RequestMapping(value = "/dashboard",method = RequestMethod.GET)
    public List<SiteDetailVo> getDashobard(HttpSession session){
        List<SiteDetailVo> allSiteDetails = storageManagementService.getAllEntries();
        return allSiteDetails.stream().map(siteDetailVo -> {
            siteDetailVo.setPassword(hashCreator.encryptUsingSymetricKey(siteDetailVo.getPassword(),session.getId()));
            return siteDetailVo;
        }).collect(Collectors.toList());
    }

    @RequestMapping(value = "/changePassword",method = RequestMethod.POST)
    public HttpStatus changePassword(@RequestBody ChangePasswordVo changePasswordVo,HttpServletRequest request){
        String username = storageManagementService.getUserName();
        String sessionId = request.getSession().getId();
        String oldPassword = hashCreator.decryptUsingPrivateKey(sessionId, changePasswordVo.getCurrentPassword());
        Optional<UserDetailsVo> userDetailsVoOptional  = storageManagementService.getUserDetailsFrom(username,
                hashCreator.createHashFrom(oldPassword));
        if(userDetailsVoOptional.isPresent()){
            String decryptedPassword = hashCreator.decryptUsingPrivateKey(sessionId,changePasswordVo.getNewPassword());

           UserDetailsVo userDetailsVo = userDetailsVoOptional.get();
           userDetailsVo.setPassword(hashCreator.createHashFrom(decryptedPassword));

           SaltAssocation saltAssocation = saltAssociationDao.findByUserName(userDetailsVo);
           saltAssocation.setSalt(hashCreator.encrypt(hashCreator.decrypt(saltAssocation.getSalt(),oldPassword,username),decryptedPassword,username));
           saltAssocation.setInitVector(hashCreator.encrypt(hashCreator.decrypt(saltAssocation.getInitVector(),oldPassword,username),decryptedPassword,username));
            saltAssociationDao.save(saltAssocation);
           storageManagementService.save(userDetailsVo);

           logUserOut(request);
            logoutUserOtherWay();
            return HttpStatus.OK;

        }else {
           throw new RuntimeException("Current username and password doesnot match");
        }

    }

    private void logUserOut(HttpServletRequest request) {
        try{
            request.logout();
        }catch (Exception e){
            logoutUserOtherWay();
            throw new RuntimeException(e);
        }
    }

    private void logoutUserOtherWay() {
        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);

    }

    @RequestMapping(value = "/publicKey",method = RequestMethod.GET,produces = MediaType.TEXT_HTML_VALUE)
    public String getPublicKeyModulus(HttpSession session){
        //String user = storageManagementService.getUserName();
        String sessionId = session.getId();
       return hashCreator.getKeyPair(sessionId);
    }

    @RequestMapping(value = "/saveKeys", method =RequestMethod.POST)
    public ResponseEntity<?> saveKeys(@RequestBody Keys keys, HttpSession session){
        try {
            System.out.println("Keys are " + keys);
            String convertedSalt = hashCreator.decryptUsingPrivateKey(session.getId(), keys.getSalt());
            System.out.println("Converted salt");
            String convertedIv = hashCreator.decryptUsingPrivateKey(session.getId(), keys.getIv());
            System.out.println("Converted initial vector");
            String convertedpassPhrase = hashCreator.decryptUsingPrivateKey(session.getId(), keys.getPassPhrase());
            System.out.println("Converted Passphrase");
            hashCreator.setKeys(convertedSalt, convertedpassPhrase, convertedIv, session.getId());

            return ResponseEntity.ok("");
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }






}
