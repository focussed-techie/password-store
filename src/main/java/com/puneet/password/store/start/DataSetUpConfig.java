package com.puneet.password.store.start;

import com.puneet.password.store.hash.HashCreator;
import com.puneet.password.store.model.EntryDetailVo;
import com.puneet.password.store.model.UserDetailsVo;
import com.puneet.password.store.service.UserDetailsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DataSetUpConfig {



    @Bean
    public CommandLineRunner setUpData(UserDetailsService userDetailsService, HashCreator hashCreator){
        return (args)-> {
        UserDetailsVo userDetailsVo = new UserDetailsVo();
        userDetailsVo.setUsername("testing");
        userDetailsVo.setPassword(hashCreator.createHashFrom("testing"));
        EntryDetailVo entryDetailVo = new EntryDetailVo();
        entryDetailVo.setUsername("test");
        entryDetailVo.setSiteName("tester");
        entryDetailVo.setPassword(hashCreator.encrypt("value","testing","testing"));
        userDetailsVo.addPasswordStorageDetail(entryDetailVo);
     //   userDetailsService.save(userDetailsVo);
        };
    }



}
