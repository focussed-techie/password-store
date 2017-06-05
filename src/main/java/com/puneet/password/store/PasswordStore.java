package com.puneet.password.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;


@SpringBootApplication
public class PasswordStore extends SpringBootServletInitializer {


    public static void main(String[] args) {
        SpringApplication.run(PasswordStore.class,args);
    }


}