package ru.penf00k.filesharing.common;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.List;

public class FileListMessage extends AbstractMessage {

    private List<File> files;

    public FileListMessage(List<File> files) {
        this.files = files;
    }

    public List<File> getFiles() {
        return files;
    }

//    private List<Path> paths;
//
//    public FileListMessage(List<Path> paths) {
//        this.paths = paths;
//    }
//
//    public List<Path> getPaths() {
//        return paths;
//    }

//    private DirectoryStream<Path> directoryStream;
//
//    public FileListMessage(DirectoryStream directoryStream) {
//        this.directoryStream = directoryStream;
//    }
//
//    public DirectoryStream<Path> getDirectoryStream() {
//        return directoryStream;
//    }
}
