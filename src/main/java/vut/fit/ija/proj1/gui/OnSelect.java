/**
 * @author xjerab25
 * File containing definition of {@link vut.fit.ija.proj1.gui.OnSelect} interface representing callback for selecting
 * and deselecting gui element.
 */
package vut.fit.ija.proj1.gui;

/**
 * Represents callback for selecting and deselecting gui elements
 * @param <T> gui element type
 */
public interface OnSelect<T> {
    /**
     * Gets called when gui element is selected
     * @param selected selected gui element
     * @return if gui should be highlighted
     */
    boolean onSelect(T selected);

    /**
     * Gets called when gui element is deselected
     * @param deselected deselected gui element
     * @return if element should be deselected
     */
    boolean onDeselect(T deselected);
}
