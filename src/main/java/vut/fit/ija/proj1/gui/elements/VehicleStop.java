/**
 * @author xjerab25
 * File containing definition of {@link vut.fit.ija.proj1.gui.elements.VehicleStop} class representing stop on map
 */
package vut.fit.ija.proj1.gui.elements;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import vut.fit.ija.proj1.data.Coordinates;
import vut.fit.ija.proj1.gui.Drawable;
import vut.fit.ija.proj1.gui.OnSelect;
import vut.fit.ija.proj1.gui.Selectable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Class representing single stop on map
 */
@JsonIgnoreProperties(value = {
        "selected",
        "selectable",
        "onSelectListener",
        "selectableGui"
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class VehicleStop implements Drawable, Selectable<VehicleStop> {
    private static final Color SELECTED_COLOR = Color.valueOf("#263238");
    private String id;
    private Coordinates coordinates;
    private boolean selected = false;
    private boolean selectable = false;
    private OnSelect<VehicleStop> onSelectListener;
    private Circle selectableGui;

    /**
     * Default constructor for jackson deserialization
     */
    private VehicleStop() {
    }

    /**
     * Stop constructor
     * @param id stop id/name
     * @param coordinates stop coordinates
     */
    public VehicleStop(String id, Coordinates coordinates) {
        this.id = id;
        this.coordinates = coordinates;
    }

    /**
     * Returns stop ID
     * @return stop ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets gui properties for selected state
     */
    private void selectGui() {
        selectableGui.setRadius(8);
        selectableGui.setFill(SELECTED_COLOR);
    }


    /**
     * Sets gui properties for deselected state
     */
    private void deselectGui() {
        selectableGui.setRadius(5);
        selectableGui.setFill(Color.RED);
    }

    @Override
    public Coordinates getCoordinates() {
        return coordinates;
    }

    @Override
    public List<Shape> draw() {
        Text text = new Text(coordinates.getX() + 10, coordinates.getY(), id);
        Circle circle = new Circle(coordinates.getX(), coordinates.getY(), 5, Color.RED);
        List<Shape> gui = Arrays.asList(text, circle);
        selectableGui = circle;

        for(Shape shape : gui) {
            shape.setOnMouseClicked(mouseEvent -> {
                if(!selectable) return;
                mouseEvent.consume();
                if(selected) {
                    if(onSelectListener != null) {
                        if (onSelectListener.onDeselect(this)) {
                            selected = false;
                            deselectGui();
                        }
                    } else {
                        selected = false;
                        deselectGui();
                    }

                } else {
                    if(onSelectListener != null) {
                        if (onSelectListener.onSelect(this)) {
                            selected = true;
                            selectGui();
                        }
                    } else {
                        selected = true;
                        selectGui();
                    }
                }
            });
        }
        return gui;
    }

    @Override
    public void setOnSelect(OnSelect<VehicleStop> selectListener) {
        this.onSelectListener = selectListener;
    }

    @Override
    public Shape getSelectableGui() {
        return selectableGui;
    }

    @Override
    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    @Override
    public boolean isSelectable() {
        return selectable;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
        if(selected) {
            selectGui();
        } else {
            deselectGui();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VehicleStop stop = (VehicleStop) o;
        return id.equals(stop.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s", id);
    }
}
