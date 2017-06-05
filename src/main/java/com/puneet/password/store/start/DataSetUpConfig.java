package com.puneet.password.store.start;

import com.puneet.password.store.hash.HashCreator;
import com.puneet.password.store.model.SiteDetailVo;
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
        SiteDetailVo siteDetailVo = new SiteDetailVo();
        siteDetailVo.setUsername("test");
        siteDetailVo.setSiteName("tester");
        siteDetailVo.setPassword(hashCreator.encrypt("value","testing","testing"));
        userDetailsVo.addPasswordStorageDetail(siteDetailVo);
     //   userDetailsService.save(userDetailsVo);
        };
    }



}
