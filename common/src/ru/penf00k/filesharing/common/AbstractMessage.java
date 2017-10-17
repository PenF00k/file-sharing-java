package ru.penf00k.filesharing.common;

import java.io.Serializable;

public abstract class AbstractMessage implements Serializable {

    private int type;

    void setType(int value) {
        type = value;
    }

    public int getType() {
        return type;
    }
}
