package com.leonardus.irfan;

import android.support.annotation.NonNull;

public class SimpleObjectModel {
    private String id;
    private String value;

    public SimpleObjectModel(String id, String value){
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    @NonNull
    @Override
    public String toString() {
        return getValue();
    }
}
