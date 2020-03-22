/**
 * @author xjerab25
 * File containing definition of {@link vut.fit.ija.proj1.gui.elements.Street} class representing street on map
 */
package vut.fit.ija.proj1.gui.elements;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import vut.fit.ija.proj1.data.Coordinates;
import vut.fit.ija.proj1.gui.Drawable;
import vut.fit.ija.proj1.gui.OnSelect;
import vut.fit.ija.proj1.gui.Selectable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Class representing single street on map
 */
@JsonIgnoreProperties(value = {
        "gui",
        "closedListener",
        "onSelectListener",
        "selectableGui",
        "selected",
        "selectable"
})
@JsonDeserialize(converter=Street.StreetSanitizer.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class Street implements Drawable, Selectable<Street> {
    private final Color SELECTED_COLOR = Color.valueOf("#d32f2f");
    private String name;
    private List<VehicleStop> stops;
    private Coordinates from;
    private Coordinates to;
    private boolean closed = false;
    private double traffic = 0;
    private List<Shape> gui;
    private OnStreetClosed closedListener;
    private OnSelect<Street> onSelectListener;
    private Line selectableGui;
    private boolean selected;
    private boolean selectable = true;

    /**
     * Default constructor for jackson deserialization
     */
    private Street() {
    }

    /**
     * Street constructor
     * @param name street name
     * @param from street from coordinates
     * @param to street to coordinates
     * @param stops stops on this street
     */
    public Street(String name, Coordinates from, Coordinates to, List<VehicleStop> stops) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.stops = stops;

        Text x = new Text(from.getX() + 10, from.getY() + 20, String.format("x: %s\ny: %s", from.getX(), from.getY()));
        Text y = new Text(to.getX() + 10, to.getY() + 20, String.format("x: %s\ny: %s", to.getX(), to.getY()));
        Font font = x.getFont();
        font = Font.font(font.getFamily(), 8);
        x.setFont(font);
        y.setFont(font);


        gui = Arrays.asList(
                new Text(getCoordinates().getX(), getCoordinates().getY(), name),
                x,
                y,
                new Line(from.getX(), from.getY(), to.getX(), to.getY())
        );
    }

    /**
     * Returns traffic on street in range from 0(lowest) 1(highest)
     * @return street traffic
     */
    public double getTraffic() {
        return traffic;
    }

    /**
     * Sets street traffic must be in range from 0 to 1
     * @param traffic street traffic
     */
    public void setTraffic(double traffic) {
        assert traffic < 1 && traffic > 0;
        this.traffic = traffic;
    }

    /**
     * Returns if street is closed
     * @return street is closed
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Sets if street is closed
     * @param closed street is closed
     */
    public void setClosed(boolean closed) {
        this.closed = closed;
        gui.forEach(shape -> {
            if (shape.getClass() == Line.class) {
                if(closed) {
                    shape.getStrokeDashArray().addAll(5d, 5d);
                } else {
                    shape.getStrokeDashArray().removeAll(5d, 5d);
                }
            }
        });
        if(closed) {
            if (closedListener != null) {
                closedListener.onClosed(this);
            }
        }
    }

    /**
     * Sets listener for street closing
     * @param closedListener street closing listener
     */
    public void setClosedListener(OnStreetClosed closedListener) {
        this.closedListener = closedListener;
    }

    /**
     * Returns stops on this street
     * @return stops on this street
     */
    public List<VehicleStop> getStops() {
        return stops;
    }

    /**
     * Returns street name
     * @return street name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns street from coordinates
     * @return street from coordinates
     */
    public Coordinates getFrom() {
        return from;
    }

    /**
     * Returns street to coordinates
     * @return street to coordinates
     */
    public Coordinates getTo() {
        return to;
    }

    /**
     * Gets coordinates of street crossing. Its either starting or ending street coordinates, null if its neither if them
     * @param street street to find crossing coordinates for
     * @return coordinates if crossing is found null otherwise
     */
    public Coordinates getCrossingCoordinates(Street street) {
        if(getTo().equals(street.getTo()) || getTo().equals(street.getFrom())) {
            return getTo();
        } else if(getFrom().equals(street.getFrom()) || getFrom().equals(street.getTo())) {
            return getFrom();
        } else {
            return null;
        }
    }

    /**
     * Sets gui properties for not selected state
     */
    private void deselectGui() {
        selectableGui.setStroke(Color.BLACK);
        selectableGui.setStrokeWidth(1);
    }

    /**
     * Sets gui properties for selected state
     */
    private void selectGui() {
        selectableGui.setStroke(SELECTED_COLOR);
        selectableGui.setStrokeWidth(3);
    }


    /**
     * Gets called after jackson finishes data deserialization
     */
    private void jacksonPostConstruct() {
        selectableGui = new Line(from.getX(), from.getY(), to.getX(), to.getY());
        gui = Arrays.asList(
                new Text(getCoordinates().getX(), getCoordinates().getY(), name),
                selectableGui
        );

        for (Shape shape : gui) {
            shape.setOnMouseClicked(mouseEvent -> {
                mouseEvent.consume();
                if(!selectable) return;
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
                        selectGui();
                        if(onSelectListener.onSelect(this)) {
                            selected = true;
                        } else {
                            deselectGui();
                        }
                    } else {
                        selected = true;
                        selectGui();
                    }
                }
            });
        }
    }

    /**
     * Returns coordinates in the middle of the street
     * @return coordinates in the middle of the street
     */
    @JsonIgnore
    @Override
    public Coordinates getCoordinates() {
        return new Coordinates((from.getX() + to.getX()) / 2, (from.getY() + to.getY()) / 2);
    }

    @Override
    public List<Shape> draw() {
        return gui;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void setOnSelect(OnSelect<Street> selectListener) {
        onSelectListener = selectListener;
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
        Street street = (Street) o;
        return name.equals(street.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * Interface representing listener, that gets called when street is closed
     */
    public interface OnStreetClosed {
        /**
         * Gets called when street is closed
         * @param street closing street
         */
        void onClosed(Street street);
    }

    /**
     * Class responsible for calling post construct
     */
    static class StreetSanitizer extends StdConverter<Street, Street> {

        /**
         * Gets called after jackson deserialization
         * @param value street which finished deserialization
         * @return street which finished deserialization
         */
        @Override
        public Street convert(Street value) {
            value.jacksonPostConstruct();
            return value;
        }
    }
}
