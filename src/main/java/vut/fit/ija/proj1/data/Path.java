package vut.fit.ija.proj1.data;

import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import vut.fit.ija.proj1.data.exceptions.StreetsNotConnectedException;
import vut.fit.ija.proj1.gui.elements.Street;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing path in the map
 */
public class Path {
    private Coordinates startCoordinates;
    private Coordinates stopCoordinates;
    private List<Coordinates> path;
    private List<Street> streets;
    private Duration delay;

    /**
     * Constructs a new path
     */
    public Path(Coordinates startCoordinates, Coordinates stopCoordinates, List<Street> streets, Duration delay) throws StreetsNotConnectedException {
        this.startCoordinates = startCoordinates;
        this.stopCoordinates = stopCoordinates;
        this.streets = streets;
        this.delay = delay;
        path = constructPath();
    }

    private List<Coordinates> constructPath() throws StreetsNotConnectedException {
        ArrayList<Coordinates> path = new ArrayList<>();
        path.add(startCoordinates);
        for(int i = 0; i < streets.size() - 1; i++) {
            Coordinates crossing = streets.get(i).getCrossingCoordinates(streets.get(i + 1));
            if(crossing != null){
                path.add(crossing);
            } else {
              throw new StreetsNotConnectedException("Provided path does not have connecting streets");
            }
        }
        path.add(stopCoordinates);
        return path;
    }

    public Duration getDelay() {
        return delay;
    }

    /**
     * Returns a coordinates on path at set distance
     * @param distance distance
     * @return coordinates on path at set distance
     */
    public PositionInfo getPathInfoByDistance(double distance) {
        if(path.size() == 1) {
            return new PositionInfo(path.get(0), streets.get(0));
        } else if(path.size() == 0){
            return null;
        }
        if(distance >= getPathLenght()) {
            return new PositionInfo(path.get(path.size() - 1), streets.get(streets.size() - 1));
        }
        Coordinates a = null;
        Coordinates b = null;
        double length = 0;
        double currentLength = 0;
        int i;
        for(i = 0; i < path.size() - 1; i++) {
            a = path.get(i);
            b = path.get(i+1);
            currentLength=Math.sqrt(Math.pow(Math.abs(a.getX() - b.getX()),2) + Math.pow(Math.abs(a.getY() - b.getY()),2));
            if(length+currentLength > distance){
                break;
            }
            length += currentLength;
        }

        double driven = (distance - length) / currentLength;
        assert a != null;
        return new PositionInfo(
                new Coordinates(a.getX() + (b.getX() - a.getX())*driven, a.getY() + (b.getY() - a.getY())*driven),
                streets.get(i)
        );
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
}
