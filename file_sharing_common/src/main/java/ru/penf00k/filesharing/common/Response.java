package ru.penf00k.filesharing.common;

public enum Response {
    //TODO удалить поле code
    NEED_AUTHORIZATION(1, "You need to login first"),
    AUTHORIZATION_OK(2, "You have been successfully authorised"),
    AUTHORIZATION_ERROR(-2, "Invalid username or password"),
    SERVER_STOP(-1, "Server stopped");

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
