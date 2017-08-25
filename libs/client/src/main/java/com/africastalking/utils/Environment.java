package com.africastalking.utils;

public enum Environment {
    PRODUCTION("production"),
    SANDBOX("sandbox");

    String text;

    Environment(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
