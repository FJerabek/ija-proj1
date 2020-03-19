package vut.fit.ija.proj1.gui.elements;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import vut.fit.ija.proj1.data.Coordinates;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Class representing single street on map
 */
@JsonDeserialize(converter=Street.StreetSanitizer.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class Street implements Drawable {
    private String name;
    private List<VehicleStop> stops;
    private Coordinates from;
    private Coordinates to;
    private boolean closed = false;
    private double traffic = 0;
    @JsonIgnore
    private List<Shape> gui;
    @JsonIgnore
    private OnStreetClosed closedListener;

    public Street() {
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

    @JsonIgnore
    @Override
    public Coordinates getCoordinates() {
        return new Coordinates((from.getX() + to.getX()) / 2, (from.getY() + to.getY()) / 2);
    }

    @Override
    public List<Shape> draw() {
        return gui;
    }

    public double getTraffic() {
        return traffic;
    }

    public void setTraffic(double traffic) {
        this.traffic = traffic;
    }

    public boolean isClosed() {
        return closed;
    }

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

    public void setClosedListener(OnStreetClosed closedListener) {
        this.closedListener = closedListener;
    }

    public void setOnSelectListener(OnStreetSelect listener) {
        for (Shape shape: draw()) {
            shape.setOnMouseClicked(event -> {
                listener.onSelect(this);
                event.consume();
            });
        }
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

    public Coordinates getCrossingCoordinates(Street street) {
        if(getTo().equals(street.getTo()) || getTo().equals(street.getFrom())) {
            return getTo();
        } else if(getFrom().equals(street.getFrom()) || getFrom().equals(street.getTo())) {
            return getFrom();
        } else {
            return null;
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

    @Override
    public String toString() {
        return name;
    }

    private void jacksonPostConstruct() {
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


    public interface OnStreetSelect{
        void onSelect(Street street);
    }

    public interface OnStreetClosed {
        void onClosed(Street street);
    }

    static class StreetSanitizer extends StdConverter<Street, Street> {

        @Override
        public Street convert(Street value) {
            value.jacksonPostConstruct();
            return value;
        }
    }
}
