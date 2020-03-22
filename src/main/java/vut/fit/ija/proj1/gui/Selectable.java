/**
 * @author xjerab25
 * File containing definition of {@link vut.fit.ija.proj1.gui.Selectable} interface representing selectable gui element
 */
package vut.fit.ija.proj1.gui;

import javafx.scene.shape.Shape;

/**
 * interface representing selectable gui element
 * @param <T> gui element type
 */
public interface Selectable<T> {
    /**
     * Sets on select listener for element
     * @param selectListener on select listener
     */
    void setOnSelect(OnSelect<T> selectListener);

    /**
     * Returns selectable part of gui element
     * @return selectable part of gui element
     */
    Shape getSelectableGui();

    /**
     * Sets if gui element is selectable
     * @param selectable gui element is selectable
     */
    void setSelectable(boolean selectable);

    /**
     * Returns if gui element is selectable
     * @return gui element is selectable
     */
    boolean isSelectable();

    /**
     * Sets if gui element is selected and changes its gui to specified state
     * @param selected gui element is selected
     */
    void setSelected(boolean selected);
}
