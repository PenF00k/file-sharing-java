package ru.penf00k.filesharing.common;

import java.io.File;

public class RequestMessage extends AbstractMessage {

    private Request request;
    private File file;
    private String newFileName;

    public RequestMessage(Request request) {
        this.request = request;
    }

    public RequestMessage(Request request, File file) {
        this.request = request;
        this.file = file;
    }

    public RequestMessage(Request request, File file, String newFileName) {
        this.request = request;
        this.file = file;
        this.newFileName = newFileName;
    }

    public Request getRequest() {
        return request;
    }

    public File getFile() {
        return file;
    }

    public String getNewFileName() {
        return newFileName;
    }
}
