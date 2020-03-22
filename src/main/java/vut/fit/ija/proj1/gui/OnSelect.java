package vut.fit.ija.proj1.gui;

public interface OnSelect<T> {
    boolean onSelect(T selected);
    boolean onDeselect(T deselected);
}
