package com.puneet.password.store.controller;


import com.puneet.password.store.dao.UserEncryptionKeysDao;
import com.puneet.password.store.hash.HashCreator;
import com.puneet.password.store.model.ChangePasswordVo;
import com.puneet.password.store.model.UserEncryptionKeys;
import com.puneet.password.store.model.UserDetailsVo;
import com.puneet.password.store.model.UserValues;
import com.puneet.password.store.service.VaultService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.Optional;

@RestController
public class LoginController {


    @Autowired
    private HashCreator hashCreator;

    @Autowired
    private VaultService vaultService;

    @Autowired
    private UserEncryptionKeysDao encryptionKeysDao;


    @Transactional
    @RequestMapping(value = "/signup" ,consumes = MediaType.APPLICATION_JSON_VALUE )
    public @ResponseBody  String signup(@RequestBody UserValues userValues, HttpSession session){

        UserDetailsVo userDetailsVo = new UserDetailsVo();
        userDetailsVo.setUsername(userValues.getUsername());
        userDetailsVo.setPassword(hashCreator.createHashFrom(hashCreator.decryptUsingPrivateKey(session.getId(),userValues.getPassword())));
        Optional<UserDetailsVo> optionalUser = vaultService.getUserDetailsFrom(userDetailsVo.getUsername(),userDetailsVo.getPassword());
        if(optionalUser.isPresent()){
            throw new RuntimeException("User Already exists");
        }

       vaultService.save(userDetailsVo);
        String salt = generateKey();
        String initVector = generateKey();
        UserEncryptionKeys userEncryptionKeys = new UserEncryptionKeys();
        userEncryptionKeys.setSalt(hashCreator.encrypt(salt,userValues.getPassword(),userDetailsVo.getUsername()));
        userEncryptionKeys.setInitVector(hashCreator.encrypt(initVector,userValues.getPassword(),userDetailsVo.getUsername()));
        userEncryptionKeys.setUserName(userDetailsVo);
        encryptionKeysDao.save(userEncryptionKeys);
        return HttpStatus.OK.toString();
    }

    private String generateKey() {
        return RandomStringUtils.randomAlphanumeric(16);
    }

    @RequestMapping(value = "/usernameexists/{username}", method = RequestMethod.GET)
    public @ResponseBody boolean doesUserExist(@PathVariable ("username") String username){
      Optional<UserDetailsVo> voOptional =   vaultService.findUserByUserName(username);
      if(voOptional.isPresent()){
            throw new RuntimeException("User already exists");
        }
        else {
          return false;
      }


    }


    @RequestMapping(value = "/changePassword",method = RequestMethod.POST)
    public HttpStatus changePassword(@RequestBody ChangePasswordVo changePasswordVo, HttpServletRequest request){
        String username = vaultService.getUserName();
        String sessionId = request.getSession().getId();
        String oldPassword = hashCreator.decryptUsingPrivateKey(sessionId, changePasswordVo.getCurrentPassword());
        Optional<UserDetailsVo> userDetailsVoOptional  = vaultService.getUserDetailsFrom(username, hashCreator.createHashFrom(oldPassword));
        if(userDetailsVoOptional.isPresent()){
            String newPassword = hashCreator.decryptUsingPrivateKey(sessionId,changePasswordVo.getNewPassword());

            UserDetailsVo userDetailsVo = userDetailsVoOptional.get();
            userDetailsVo.setPassword(hashCreator.createHashFrom(newPassword));

            UserEncryptionKeys userEncryptionKeys = encryptionKeysDao.findByUserName(userDetailsVo);
            userEncryptionKeys.setSalt(hashCreator.encrypt(hashCreator.decrypt(userEncryptionKeys.getSalt(),oldPassword,username),newPassword,username));
            userEncryptionKeys.setInitVector(hashCreator.encrypt(hashCreator.decrypt(userEncryptionKeys.getInitVector(),oldPassword,username),newPassword,username));
            encryptionKeysDao.save(userEncryptionKeys);
            vaultService.save(userDetailsVo);
            logUserOut(request);
            return HttpStatus.OK;

        }else {
            throw new RuntimeException("Current username and password doesnot match");
        }

    }

    private void logUserOut(HttpServletRequest request) {
        try{
            request.logout();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }





}
