package ru.penf00k.filesharing.common;

public class TextMessage extends AbstractMessage {

    private String text;

    public TextMessage(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
