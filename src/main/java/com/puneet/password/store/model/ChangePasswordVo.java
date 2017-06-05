package com.puneet.password.store.model;

/**
 * Created by puneet on 31/05/17.
 */
public class ChangePasswordVo {


    private String currentPassword;
    private String newPassword;


    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
