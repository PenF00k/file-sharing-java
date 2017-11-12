package ru.penf00k.filesharing.common;

import java.io.File;

public class FileMessage extends AbstractMessage {

    private File file; // Хранит путь к файлу. работает getName(). Сам файл не сериализуется

    public FileMessage(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
