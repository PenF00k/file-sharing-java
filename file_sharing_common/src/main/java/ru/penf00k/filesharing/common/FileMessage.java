package ru.penf00k.filesharing.common;

import java.io.File;

public class FileMessage extends AbstractMessage {

    private File file; // Хранит путь к файлу. работает getName(). Сам файл не сериализуется
    private long length;

    public FileMessage(File file, long length) {
        this.file = file;
        this.length = length;

    }

    public File getFile() {
        return file;
    }

    public long getLength() {
        return length;
    }
}
