package com.erp.zup.api.enums;

public enum Role {
    USER("User"),
    ADMIN("Admin");

    private String value;

    public String getValue() {
        return value;
    }

    Role(String value){
        this.value = value;
    }
}
