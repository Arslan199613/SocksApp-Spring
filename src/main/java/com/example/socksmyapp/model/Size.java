package com.example.socksmyapp.model;

public enum Size {

    S("23"),
    M("25"),
    L("27"),
    XL("29"),
    XXL("31");

    private final String size;

    Size(String size) {
        this.size = size;
    }

    public String getSize() {
        return size;
    }
}

