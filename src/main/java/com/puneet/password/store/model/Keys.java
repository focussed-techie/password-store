package com.puneet.password.store.model;

/**
 * Created by puneet on 06/01/18.
 */
public class Keys {

    private String iv;
    private String salt;
    private String passPhrase;

    public Keys(String iv, String salt, String passPhrase) {
        this.iv = iv;
        this.salt = salt;
        this.passPhrase = passPhrase;
    }

    public Keys() {
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getPassPhrase() {
        return passPhrase;
    }

    public void setPassPhrase(String passPhrase) {
        this.passPhrase = passPhrase;
    }

    @Override
    public String toString() {
        return "Keys{" +
                "iv='" + iv + '\'' +
                ", salt='" + salt + '\'' +
                ", passPhrase='" + passPhrase + '\'' +
                '}';
    }
}
