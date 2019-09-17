package com.leonardus.irfan;

public class SimpleSelectableObjectModel extends SimpleObjectModel {
    private boolean selected = false;

    public SimpleSelectableObjectModel(String id, String value){
        super(id, value);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}
