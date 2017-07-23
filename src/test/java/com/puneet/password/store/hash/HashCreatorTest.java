package com.puneet.password.store.hash;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by puneet on 10/01/17.
 */
public class HashCreatorTest {

    private HashCreator hashCreator = new HashCreator();

    @Test
    public void decrypt() throws Exception {

        //System.out.println(hashCreator.decryptFromUi("Qk+0n64leG8RIvxwgsLhvA==","testing123"));
    }

    @Test
    public void encrypt() throws Exception {

       // System.out.println(  hashCreator.encrypt("test","testing123"));

    }

    @org.junit.Test
    public void createHashFrom() throws Exception {

        System.out.println(hashCreator.createHashFrom("testing123"));


    }

}