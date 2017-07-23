package com.puneet.password.store.hash;

import com.google.common.collect.ImmutableMap;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.substring;


@Component
public class HashCreator {

    public static String initVector= "SaltForEncryption";
    public static String passwordaSalt= "SaltForPassword";

    private Map<String,KeyPair> mapOfKeys = new HashMap<>();

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


    public String getKeyPair(String sessionId){

        try {


            KeyPair keyPair = mapOfKeys.get(sessionId);
            if(keyPair == null) {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(2048);
                keyPair = keyPairGenerator.generateKeyPair();
            }
            PublicKey publicKey = keyPair.getPublic();
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec rsaPublicKeySpec = keyFactory.getKeySpec(publicKey,RSAPublicKeySpec.class);
            mapOfKeys.put(sessionId,keyPair);
            return rsaPublicKeySpec.getModulus().toString(16);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);

        }

    }

    public String decryptFromUi(String sessionId, String dataToBeDecrypted){
        KeyPair keyPair = mapOfKeys.get(sessionId);
        Key privateKey = keyPair.getPrivate();
        Cipher cipher;
        BigInteger passwordInt = new BigInteger(dataToBeDecrypted, 16);
        try {
            cipher = javax.crypto.Cipher.getInstance("RSA");
            byte[] passwordBytes  = new byte[256];

            if (passwordInt.toByteArray().length > 256) {
                passwordBytes = ArrayUtils.subarray(passwordBytes,1,257);
            } else {
                passwordBytes = passwordInt.toByteArray();
            }
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[]  dectyptedText = cipher.doFinal(passwordBytes);
            String passwordNew = new String(dectyptedText);
            System.out.println("Password new " + passwordNew);
            return passwordNew;
        } catch(NoSuchAlgorithmException |NoSuchPaddingException | InvalidKeyException |IllegalBlockSizeException| BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public String encryptForUi(String sessionId, String dataToBeEncrypted){
        KeyPair keyPair = mapOfKeys.get(sessionId);
        Key privateKey = keyPair.getPrivate();
        Cipher cipher;

        try {
            cipher = javax.crypto.Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[]  encryptedText = cipher.doFinal(dataToBeEncrypted.getBytes());
            BigInteger passwordAsInt = new BigInteger(encryptedText);
            System.out.println("encrypted text " + passwordAsInt.toString(16));
            return passwordAsInt.toString(16);
        } catch(NoSuchAlgorithmException |NoSuchPaddingException | InvalidKeyException |IllegalBlockSizeException| BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }




}
