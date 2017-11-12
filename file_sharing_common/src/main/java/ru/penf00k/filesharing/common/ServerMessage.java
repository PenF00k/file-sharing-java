package ru.penf00k.filesharing.common;

public class ServerMessage extends AbstractMessage {

    private Response response;
    private String message;

    public ServerMessage(Response response) {
        this.response = response;
    }

    public ServerMessage(Response response, String message) {
        this.response = response;
        this.message = message;
    }

    public Response getResponse() {
        return response;
    }

    public String getMessage() {
        return message;
    }
}
