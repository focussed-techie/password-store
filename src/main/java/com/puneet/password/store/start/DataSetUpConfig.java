package com.puneet.password.store.start;

import com.puneet.password.store.hash.HashCreator;
import com.puneet.password.store.model.VaultEntryVo;
import com.puneet.password.store.model.UserDetailsVo;
import com.puneet.password.store.service.VaultService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DataSetUpConfig {



    @Bean
    public CommandLineRunner setUpData(VaultService vaultService, HashCreator hashCreator){
        return (args)-> {
        UserDetailsVo userDetailsVo = new UserDetailsVo();
        userDetailsVo.setUsername("testing");
        userDetailsVo.setPassword(hashCreator.createHashFrom("testing"));
        VaultEntryVo vaultEntryVo = new VaultEntryVo();
        vaultEntryVo.setUsername("test");
        vaultEntryVo.setSiteName("tester");
        vaultEntryVo.setPassword(hashCreator.encrypt("value","testing","testing"));
        userDetailsVo.addPasswordStorageDetail(vaultEntryVo);
     //   userDetailsService.save(userDetailsVo);
        };
    }



}
