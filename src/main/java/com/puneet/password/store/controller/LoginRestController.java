package com.puneet.password.store.controller;


import com.puneet.password.store.hash.HashCreator;
import com.puneet.password.store.model.UserDetailsVo;
import com.puneet.password.store.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginRestController {


    @Autowired
    private HashCreator hashCreator;

    @Autowired
    private UserDetailsService userDetailsService;


    @RequestMapping(value = "/signup" ,consumes = MediaType.APPLICATION_JSON_VALUE )
    public @ResponseBody  String signup(@RequestBody UserValues userValues){

        UserDetailsVo userDetailsVo = new UserDetailsVo();
        userDetailsVo.setUsername(userValues.getUsername());
        userDetailsVo.setPassword(hashCreator.createHashFrom(userValues.getPassword()));

        userDetailsService.save(userDetailsVo);

        return HttpStatus.OK.toString();
    }





}
