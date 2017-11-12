package ru.penf00k.filesharing.common.util;

import java.io.File;

public class Utils {

    public static FileNameExtension fileNameToFNM(String name) {
        String nameNoExtension = name.substring(0, name.lastIndexOf("."));
        String extension = name.substring(name.lastIndexOf(".") + 1);
        return new FileNameExtension(nameNoExtension, extension);
    }

    public static FileNameExtension fileToFNM(File file) {
        return fileNameToFNM(file.getName());
    }

    public static String getFileNameFromFNM(FileNameExtension fnm) {
        return String.format("%s.%s", fnm.getName(), fnm.getExtension());
    }

    public static class FileNameExtension {
        private String name;
        private String extension;

        public FileNameExtension(String name, String extension) {
            this.name = name;
            this.extension = extension;
        }

        public String getName() {
            return name;
        }

        public String getExtension() {
            return extension;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setExtension(String extension) {
            this.extension = extension;
        }

        public String toFileName() {
            return Utils.getFileNameFromFNM(this);
        }
    }
}
