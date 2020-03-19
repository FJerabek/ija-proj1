package vut.fit.ija.proj1.data;

import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import vut.fit.ija.proj1.gui.elements.VehicleStop;
import vut.fit.ija.proj1.gui.elements.Street;

import java.util.List;

/**
 * Class representing path in the map
 */
public class Path {
    private List<Coordinates> path;
    private List<Street> streets;

    /**
     * Constructs a new path
     * @param path list of path coordinates
     */
    public Path(List<Coordinates> path) {
        this.path = path;
    }

    /**
     * Returns a coordinates on path at set distance
     * @param distance distance
     * @return coordinates on path at set distance
     */
    public Coordinates getCoordinatesByDistance(double distance) {
        if(path.size() == 1) {
            return path.get(0);
        } else if(path.size() == 0){
            return null;
        }
        if(distance >= getPathLenght()) {
            return path.get(path.size() - 1);
        }
        Coordinates a = null;
        Coordinates b = null;
        double length = 0;
        double currentLength = 0;
        for(int i = 0; i < path.size() - 1; i++) {
            a = path.get(i);
            b = path.get(i+1);
            currentLength=Math.sqrt(Math.pow(Math.abs(a.getX() - b.getX()),2) + Math.pow(Math.abs(a.getY() - b.getY()),2));
            if(length+currentLength > distance){
                break;
            }
            length += currentLength;
        }

        double driven = (distance - length) / currentLength;
        return new Coordinates(a.getX() + (b.getX() - a.getX())*driven, a.getY() + (b.getY() - a.getY())*driven);
    }

    /**
     * Returns path as shape on map
     * @return shape on map
     */
    public Shape getShape() {
        Shape shape = null;
        for (int i = 0; i < path.size() - 1; i++) {
            Coordinates a = path.get(i);
            Coordinates b = path.get(i+1);

            if(shape == null) {
                Line line = new Line(a.getX(), a.getY(), b.getX(), b.getY());
                line.setStrokeWidth(3);
                shape = line;
            } else {
                Line line = new Line(a.getX(), a.getY(), b.getX(), b.getY());
                line.setStrokeWidth(3);
                shape = Shape.union(shape, line);
            }
        }
        return shape;
    }

    /**
     * Calculates the path length
     * @return path length
     */
    public double getPathLenght() {
        double length = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Coordinates a = path.get(i);
            Coordinates b = path.get(i+1);
            length+=Math.sqrt(Math.pow(Math.abs(a.getX() - b.getX()),2) + Math.pow(Math.abs(a.getY() - b.getY()),2));
        }
        return length;
    }
    private static Coordinates getNextCoordinates(Coordinates current, Street nextStreet) {
        return nextStreet.getFrom().equals(current)? nextStreet.getTo() : nextStreet.getFrom();
    }

    /**
     * Returns stop if there is a stop on specified coordinates, null otherwise;
     * @param coordinates stop coordinates
     * @param streets all streets on map
     * @return stop if there is a stop, null otherwise
     */
    private static VehicleStop getStopByCoordinates(Coordinates coordinates, List<Street> streets) {
        for(Street street : streets) {
            for(VehicleStop stop : street.getStops()) {
                if (stop.getCoordinates().equals(coordinates)) {
                    return stop;
                }
            }
        }
        return null;
    }
}
