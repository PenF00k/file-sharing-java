package ru.penf00k.filesharing.common;

public class ServerMessage extends AbstractMessage {

    private Response response;

    public ServerMessage(Response response) {
        setType(Messages.SERVER);
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }
}
