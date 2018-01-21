package com.puneet.password.store.controller;

import com.puneet.password.store.hash.HashCreator;
import com.puneet.password.store.model.*;
import com.puneet.password.store.service.VaultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class VaultController {

    @Autowired
    private VaultService vaultService;

    @Autowired
    private HashCreator hashCreator;




    @RequestMapping(value = "/addNewEntry", method = RequestMethod.POST,consumes = APPLICATION_JSON_VALUE)
    public @ResponseBody HttpStatus addNewEntry(@RequestBody VaultEntryVo storageEntry, HttpSession session){
       storageEntry.setPassword(hashCreator.decryptUsingSymetricKey(storageEntry.getPassword(),session.getId()));
        vaultService.savePasswordEntry(storageEntry);
        return HttpStatus.OK;
    }

    @RequestMapping(value = "/saveData", method = RequestMethod.POST,consumes = APPLICATION_JSON_VALUE)
    public @ResponseBody HttpStatus savePasswordChanges(@RequestBody VaultEntryVo vaultEntry, HttpSession session){
        vaultEntry.setPassword(hashCreator.decryptUsingSymetricKey(vaultEntry.getPassword(),session.getId()));
       vaultService.updatePasswordEntry(vaultEntry);
        return HttpStatus.OK;
    }

    @RequestMapping(value = "/dashboard",method = RequestMethod.GET)
    public List<VaultEntryVo> getDashobard(HttpSession session){
        List<VaultEntryVo> allSiteDetails = vaultService.getAllEntries();
        return allSiteDetails.stream().map(siteDetailVo -> {
            siteDetailVo.setPassword(hashCreator.encryptUsingSymetricKey(siteDetailVo.getPassword(),session.getId()));
            return siteDetailVo;
        }).collect(Collectors.toList());
    }

    @RequestMapping(value = "/publicKey",method = RequestMethod.GET,produces = MediaType.TEXT_HTML_VALUE)
    public String getPublicKeyModulus(HttpSession session){
        String sessionId = session.getId();
       return hashCreator.getKeyPair(sessionId);
    }

    @RequestMapping(value = "/saveKeys", method =RequestMethod.POST)
    public ResponseEntity<?> saveKeys(@RequestBody Keys keys, HttpSession session){
        try {
          //  System.out.println("Keys are " + keys);
            String convertedSalt = hashCreator.decryptUsingPrivateKey(session.getId(), keys.getSalt());
           // System.out.println("Converted salt");
            String convertedIv = hashCreator.decryptUsingPrivateKey(session.getId(), keys.getIv());
          //  System.out.println("Converted initial vector");
            String convertedpassPhrase = hashCreator.decryptUsingPrivateKey(session.getId(), keys.getPassPhrase());
          //  System.out.println("Converted Passphrase");
            hashCreator.setKeys(convertedSalt, convertedpassPhrase, convertedIv, session.getId());

            return ResponseEntity.ok("");
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }






}
