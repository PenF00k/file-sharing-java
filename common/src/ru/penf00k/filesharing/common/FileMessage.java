package ru.penf00k.filesharing.common;

import java.io.File;

public class FileMessage extends AbstractMessage {

    private File file;
    private String name;
    private String path;

    public FileMessage(File file, String name) {
        type = Messages.FILE;
        this.file = file;
        this.name = name;
    }
}
