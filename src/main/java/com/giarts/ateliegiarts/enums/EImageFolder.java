package com.giarts.ateliegiarts.enums;

public enum EImageFolder {
    PRODUCT("products"),
    EVENT("events");

    private final String folderName;

    EImageFolder(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return this.folderName;
    }
}
