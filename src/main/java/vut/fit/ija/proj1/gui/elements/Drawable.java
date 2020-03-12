package vut.fit.ija.proj1.gui.elements;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Shape;
import vut.fit.ija.proj1.data.Coordinates;

import java.util.List;

/**
 * Interface representing object drawable on map
 */
public interface Drawable {
    /**
     * Returns map object coordinates
     * @return object coordinates
     */
    Coordinates getCoordinates();

    /**
     * Returns object representation in shapes
     * @return object gui representation in shapes
     */
    List<Shape> draw();
}
