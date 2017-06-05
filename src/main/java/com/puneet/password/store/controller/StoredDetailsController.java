package com.puneet.password.store.controller;

import com.puneet.password.store.hash.HashCreator;
import com.puneet.password.store.model.ChangePasswordVo;
import com.puneet.password.store.model.SiteDetailVo;
import com.puneet.password.store.model.UserDetailsVo;
import com.puneet.password.store.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

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
    public List<SiteDetailVo> getDashobard(){
        return storageManagementService.getAllEntries();
    }

    @RequestMapping(value = "/changePassword",method = RequestMethod.POST)
    public HttpStatus changePassword(@RequestBody ChangePasswordVo changePasswordVo,HttpServletRequest request){
        String username = storageManagementService.getUserName();
        Optional<UserDetailsVo> userDetailsVoOptional  = storageManagementService.getUserDetailsFrom(username,hashCreator.createHashFrom(changePasswordVo.getCurrentPassword()));
        if(userDetailsVoOptional.isPresent()){
           UserDetailsVo userDetailsVo = userDetailsVoOptional.get();
           userDetailsVo.setPassword(hashCreator.createHashFrom(changePasswordVo.getNewPassword()));
           List<SiteDetailVo> siteDetails = userDetailsVo.getSiteDetailList();
            siteDetails.forEach(passwordStorageDetail -> {
                passwordStorageDetail.setPassword(hashCreator.encrypt
                       (hashCreator.decrypt(
                               passwordStorageDetail.getPassword(),
                               changePasswordVo.getCurrentPassword(),
                               username),changePasswordVo.getNewPassword(),username));
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


}
