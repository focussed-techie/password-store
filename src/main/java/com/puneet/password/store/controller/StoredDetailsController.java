package com.puneet.password.store.controller;

import com.puneet.password.store.hash.HashCreator;
import com.puneet.password.store.model.ChangePasswordVo;
import com.puneet.password.store.model.SiteDetailVo;
import com.puneet.password.store.model.UserDetailsVo;
import com.puneet.password.store.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @RequestMapping(value = "/addNewEntry", method = RequestMethod.POST,consumes = APPLICATION_JSON_VALUE)
    public @ResponseBody HttpStatus addNewEntry(@RequestBody SiteDetailVo storageEntry){
       storageManagementService.savePasswordEntry(storageEntry);
        return HttpStatus.OK;
    }

    @RequestMapping(value = "/saveData", method = RequestMethod.POST,consumes = APPLICATION_JSON_VALUE)
    public @ResponseBody HttpStatus savePasswordChanges(@RequestBody SiteDetailVo storageEntry){

       storageManagementService.updatePasswordEntry(storageEntry);
        return HttpStatus.OK;
    }

    @RequestMapping(value = "/dashboard",method = RequestMethod.GET)
    public List<SiteDetailVo> getDashobard(HttpSession session){
        List<SiteDetailVo> allSiteDetails = storageManagementService.getAllEntries();
        return allSiteDetails.stream().map(siteDetailVo -> {
            siteDetailVo.setPassword(hashCreator.encryptForUi(session.getId(),siteDetailVo.getPassword()));
            return siteDetailVo;
        }).collect(Collectors.toList());
    }

    @RequestMapping(value = "/changePassword",method = RequestMethod.POST)
    public HttpStatus changePassword(@RequestBody ChangePasswordVo changePasswordVo,HttpServletRequest request){
        String username = storageManagementService.getUserName();
        String sessionId = request.getSession().getId();
        Optional<UserDetailsVo> userDetailsVoOptional  = storageManagementService.getUserDetailsFrom(username,hashCreator.createHashFrom(hashCreator.decryptFromUi(sessionId,changePasswordVo.getCurrentPassword())));
        if(userDetailsVoOptional.isPresent()){
            String decryptedPassword = hashCreator.decryptFromUi(sessionId,changePasswordVo.getNewPassword());

           UserDetailsVo userDetailsVo = userDetailsVoOptional.get();
           userDetailsVo.setPassword(hashCreator.createHashFrom(decryptedPassword));
           List<SiteDetailVo> siteDetails = userDetailsVo.getSiteDetailList();
            siteDetails.forEach(passwordStorageDetail -> {
                passwordStorageDetail.setPassword(hashCreator.encrypt
                       (hashCreator.decrypt(
                               passwordStorageDetail.getPassword(),
                               changePasswordVo.getCurrentPassword(),
                               username),decryptedPassword,username));
            });
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





}
