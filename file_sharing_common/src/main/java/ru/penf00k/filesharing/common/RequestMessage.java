package ru.penf00k.filesharing.common;

public class RequestMessage extends AbstractMessage {

    private Request request;

    public RequestMessage(Request request) {
        setType(Messages.REQUEST);
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }
}
