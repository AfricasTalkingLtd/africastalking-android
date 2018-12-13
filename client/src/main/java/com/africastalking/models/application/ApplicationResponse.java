package com.africastalking.models.application;

import com.google.gson.annotations.SerializedName;

public final class ApplicationResponse {

    @SerializedName("UserData")
    public UserData userData;

    public static final class UserData {
        public String balance;
    }
}
