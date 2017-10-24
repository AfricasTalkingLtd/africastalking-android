package com.africastalking.models.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by jay on 7/31/17.
 */

public class AccountResponse {

    @SerializedName("UserData")
    @Expose
    private UserData userData;

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

}