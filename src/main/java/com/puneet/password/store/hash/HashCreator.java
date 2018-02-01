package com.puneet.password.store.hash;

import com.puneet.password.store.model.Keys;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.substring;


@Component
public class HashCreator {

    public static String initVector= "SaltForEncryption";
    public static String passwordaSalt= "SaltForPassword";
    public static final int keySize = 128 ;
    public static final int iterationCount =1000 ;



    private Map<String,KeyPair> mapOfKeys = new HashMap<>();
    private Map<String,Keys> mapOfSymetricKeys = new HashMap<>();



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


    public  String decrypt(String valueToBeDecrypted,String userSuppliedSalt,String initVector){
        try {

            IvParameterSpec iv = new IvParameterSpec(getInitVectorFrom(initVector).getBytes("UTF-8"));

            SecretKeySpec skeySpec = new SecretKeySpec(getSalt(userSuppliedSalt).getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(valueToBeDecrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;

    }

    private String getInitVectorFrom(String initVectorLocal) {

        return substring(StringUtils.join(initVectorLocal,initVector),0,16);
    }

    public  String encrypt(String valueToBeDecrypted,String salt,String initVector){
        try {

            IvParameterSpec iv = new IvParameterSpec(getInitVectorFrom(initVector).getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(getSalt(salt).getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(valueToBeDecrypted.getBytes());

            return Base64.encodeBase64String(original);
        } catch (Exception ex) {
            throw  new RuntimeException(ex);
        }

    }

    private String getSalt(String initialSalt) {
        return substring(StringUtils.join(initialSalt,passwordaSalt),0,16);
    }


    public String getKeyPair(String sessionId){

        try {

            KeyPair keyPair = mapOfKeys.get(sessionId);
            if(keyPair == null) {
                keyPair = generateKeyPair();
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

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPair keyPair;KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }

    public String decryptUsingPrivateKey(String sessionId, String dataToBeDecrypted){
        KeyPair keyPair = mapOfKeys.get(sessionId);
        Key privateKey = keyPair.getPrivate();
      //  System.out.println("public key is "+keyPair.getPublic().toString());
        Cipher cipher;
        BigInteger passwordInt = new BigInteger(dataToBeDecrypted, 16);
        try {
            cipher = javax.crypto.Cipher.getInstance("RSA");
            byte[] passwordBytes  = new byte[256];

            if (passwordInt.toByteArray().length > 256) {
                byte[] toByteArray = passwordInt.toByteArray();
                for (int i=1; i<257; i++) {
                    passwordBytes[i-1] = toByteArray[i];
        //            passwordBytes[i-1] = passwordInt.toByteArray()[i];
                }
               passwordBytes = ArrayUtils.subarray(toByteArray,1,toByteArray.length);
            } else {
                passwordBytes = passwordInt.toByteArray();
            }
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[]  dectyptedText = cipher.doFinal(passwordBytes);
            String passwordNew = new String(dectyptedText);
        //    System.out.println("Password new " + passwordNew);
            return passwordNew;
        } catch(NoSuchAlgorithmException |NoSuchPaddingException | InvalidKeyException |IllegalBlockSizeException| BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }








    public String encryptUsingSymetricKey(String plaintext,String sessionId) {
        try {
            Keys keys = mapOfSymetricKeys.get(sessionId);
            SecretKey key = generateKey(keys.getSalt(), keys.getPassPhrase());
            byte[] encrypted = doFinal(Cipher.ENCRYPT_MODE, key, keys.getIv(), plaintext.getBytes("UTF-8"));
            return base64(encrypted);
        }
        catch (UnsupportedEncodingException e) {
            throw fail(e);
        }
    }

    public String decryptUsingSymetricKey(String ciphertext,String sessionId) {
        try {
            Keys keys = mapOfSymetricKeys.get(sessionId);
            SecretKey key = generateKey(keys.getSalt(), keys.getPassPhrase());
            byte[] decrypted = doFinal(Cipher.DECRYPT_MODE, key, keys.getIv(), base64(ciphertext));
            return new String(decrypted, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw fail(e);
        }
    }

    private byte[] doFinal(int encryptMode, SecretKey key, String iv, byte[] bytes) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(encryptMode, key, new IvParameterSpec(hex(iv)));
            return cipher.doFinal(bytes);
        }
        catch (InvalidKeyException
                | InvalidAlgorithmParameterException
                | IllegalBlockSizeException
                |NoSuchPaddingException
                |NoSuchAlgorithmException
                | BadPaddingException e) {
            throw fail(e);
        }
    }

    private SecretKey generateKey(String salt, String passphrase) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), hex(salt), iterationCount, keySize);
            SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
            return key;
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw fail(e);
        }
    }



    public static String base64(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    public static byte[] base64(String str) {
        return Base64.decodeBase64(str);
    }

    public static String hex(byte[] bytes) {
        return Hex.encodeHexString(bytes);
    }

    public static byte[] hex(String str) {
        try {
            return Hex.decodeHex(str.toCharArray());
        }
        catch (DecoderException e) {
            throw new IllegalStateException(e);
        }
    }

    private IllegalStateException fail(Exception e) {
        return new IllegalStateException(e);
    }

    public void setKeys(String convertedSalt, String convertedpassPhrase, String convertedIv,String sessionId) {
        Keys keys = new Keys(convertedIv,convertedSalt,convertedpassPhrase);
        mapOfSymetricKeys.put(sessionId,keys);

    }
    public void removeKeys(String sessionId){
        mapOfSymetricKeys.remove(sessionId);
        mapOfKeys.remove(sessionId);

    }
}
