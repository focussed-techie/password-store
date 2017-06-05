package com.puneet.password.store.hash;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.apache.commons.lang3.StringUtils.substring;


@Component
public class HashCreator {

    public static String initVector= "SaltForEncryption";
    public static String passwordaSalt= "SaltForPassword";

    public  String createHashFrom(String masterPassword) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] digestAsByte = messageDigest.digest(masterPassword.getBytes("UTF-8"));
            String hashedPassword = HexBin.encode(digestAsByte);
            return hashedPassword;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
             throw new RuntimeException(e);

        }
    }


    public  String decrypt(String value,String password,String initVector){
        try {

            IvParameterSpec iv = new IvParameterSpec(getInitVectorFrom(initVector).getBytes("UTF-8"));

            SecretKeySpec skeySpec = new SecretKeySpec(getSalt(password).getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(value));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;

    }

    private String getInitVectorFrom(String initVectorLocal) {

        return substring(StringUtils.join(initVectorLocal,initVector),0,16);
    }

    public  String encrypt(String value,String password,String initVector){
        try {

            IvParameterSpec iv = new IvParameterSpec(getInitVectorFrom(initVector).getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(getSalt(password).getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(value.getBytes());

            return Base64.encodeBase64String(original);
        } catch (Exception ex) {
            throw  new RuntimeException(ex);
        }

    }

    private String getSalt(String password) {
        return substring(StringUtils.join(password,passwordaSalt),0,16);
    }

}
