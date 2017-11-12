package ru.penf00k.filesharing.common;

public enum Request {
    //TODO удалить поле message
    GET_FILES_LIST("Get all files"),
    RENAME_FILE("Rename file"),
    DELETE_FILE("Delete file");

    private String message;

    Request(String message) {
        this.message = message;
    }
}
