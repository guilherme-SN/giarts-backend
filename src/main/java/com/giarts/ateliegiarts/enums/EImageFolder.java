package com.giarts.ateliegiarts.enums;

import lombok.Getter;

@Getter
public enum EImageFolder {
    PRODUCT("products"),
    EVENT("events");

    private final String folderName;

    EImageFolder(String folderName) {
        this.folderName = folderName;
    }
}
