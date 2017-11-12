package ru.penf00k.filesharing.common;

import java.io.File;

public class FileMessage extends AbstractMessage {

    private File file; // Хранит путь к файлу. работает getName(). Сам файл не сериализуется
    private int length;

    public FileMessage(File file, int length) {
        this.file = file;
        this.length = length;

    }

    public File getFile() {
        return file;
    }

    public int getLength() {
        return length;
    }
}
