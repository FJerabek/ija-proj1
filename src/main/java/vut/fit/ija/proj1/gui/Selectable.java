package vut.fit.ija.proj1.gui;

import javafx.scene.shape.Shape;

public interface Selectable<T> {
    void setOnSelect(OnSelect<T> selectListener);
    Shape getSelectableGui();
    void setSelectable(boolean selectable);
    boolean isSelectable();
    void setSelected(boolean selected);
}
