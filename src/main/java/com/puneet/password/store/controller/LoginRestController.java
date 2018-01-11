package com.puneet.password.store.controller;


import com.puneet.password.store.dao.SaltAssociationDao;
import com.puneet.password.store.hash.HashCreator;
import com.puneet.password.store.model.SaltAssocation;
import com.puneet.password.store.model.UserDetailsVo;
import com.puneet.password.store.service.UserDetailsService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.Optional;

@RestController
public class LoginRestController {


    @Autowired
    private HashCreator hashCreator;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private SaltAssociationDao saltAssocationDao;


    @Transactional
    @RequestMapping(value = "/signup" ,consumes = MediaType.APPLICATION_JSON_VALUE )
    public @ResponseBody  String signup(@RequestBody UserValues userValues,HttpSession session){

        UserDetailsVo userDetailsVo = new UserDetailsVo();
        userDetailsVo.setUsername(userValues.getUsername());
        //String decryptedPassword = hashCreator.decryptUsingPrivateKey(session.getId(),userValues.getPassword());
        userDetailsVo.setPassword(hashCreator.createHashFrom(userValues.getPassword()));
        Optional<UserDetailsVo> optionalUser = userDetailsService.getUserDetailsFrom(userDetailsVo.getUsername(),userDetailsVo.getPassword());
        if(optionalUser.isPresent()){
            throw new RuntimeException("User Already exists");
        }

       userDetailsService.save(userDetailsVo);
        String salt = RandomStringUtils.randomAlphanumeric(16);
        String initVector = RandomStringUtils.randomAlphanumeric(16);
        System.out.println("Salt = "+salt);
        System.out.println("IV = "+initVector);
        SaltAssocation saltAssocation = new SaltAssocation();
        saltAssocation.setSalt(hashCreator.encrypt(salt,userValues.getPassword(),userDetailsVo.getUsername()));
        saltAssocation.setInitVector(hashCreator.encrypt(initVector,userValues.getPassword(),userDetailsVo.getUsername()));
        saltAssocation.setUserName(userDetailsVo);
        saltAssocationDao.save(saltAssocation);

        return HttpStatus.OK.toString();
    }
    @RequestMapping(value = "/usernameexists/{username}", method = RequestMethod.GET)
    public @ResponseBody boolean doesUserExist(@PathVariable ("username") String username){
      Optional<UserDetailsVo> voOptional =   userDetailsService.findUserByUserName(username);
      if(voOptional.isPresent()){
            throw new RuntimeException("User already exists");
        }
        else {
          return false;
      }


    }





}
