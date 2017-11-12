package ru.penf00k.filesharing.common;

public enum Response {
    //TODO удалить поле code
    NEED_AUTHORIZATION(1, "You need to login first"),
    AUTHORIZATION_OK(2, "You have been successfully authorised"),
    SERVER_STOP(-1, "Server stopped"),
    AUTHORIZATION_ERROR(-2, "Invalid username or password"),
    ERROR_DELETE_FILE(-3, "Couldn't delete file"),
    ERROR_RENAME_FILE(-4, "Couldn't rename file"),
    ERROR_DOWNLOAD_FILE(-5, "Couldn't download file");

    private int code;
    private String message;

    Response(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
