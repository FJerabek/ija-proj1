package vut.fit.ija.proj1.data;

import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import vut.fit.ija.proj1.gui.elements.Stop;
import vut.fit.ija.proj1.gui.elements.Street;

import java.util.*;

/**
 * Represents a line that vehicle takes
 */
public class Line {
    private List<Stop> stops;
    private String name;
    private List<Street> path;

    /**
     * Creates a line with specified name and stop plan
     * @param stops Stops on line
     * @param name Line name
     */
    public Line(List<Stop> stops, List<Street> path,  String name) {
        this.stops = stops;
        this.name = name;
        this.path = path;
    }

    private Street getStopStreet(Stop stop) {
        for(Street street : path) {
            if(street.getStops().contains(stop)) {
                return street;
            }
        }
        return null;
    }

    /**
     * Returns neighboring streets by specifying street and coordinates of street end
     * @param coords street end coordinates
     * @return street neighbors
     */
    private List<Street> getNeighbors(Coordinates coords) {
        List<Street> neighbors = new ArrayList<>();
        for(Street street : path) {
            if((street.getFrom().equals(coords) || street.getTo().equals(coords))) {
                if(!neighbors.contains(street)) {
                    neighbors.add(street);
                }
            }
        }
        return neighbors;
    }

    private List<Street> getNeighbors(Street street) {
        HashSet<Street> neighbors = new HashSet<>();
        neighbors.addAll(getNeighbors(street.getTo()));
        neighbors.addAll(getNeighbors(street.getFrom()));
        return new ArrayList<>(neighbors);
    }


    /**
     * Returns path to the next stop
     * @param currentStop Current stop
     * @return path to the next stop on this line
     */
    public Path getPathToNextStop(Stop currentStop) {
        class PathInfo {
            protected List<Coordinates> path;
            protected Street currentStreet;

            public PathInfo(List<Coordinates> path, Street currentStreet) {
                this.path = path;
                this.currentStreet = currentStreet;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                PathInfo pathInfo = (PathInfo) o;
                return Objects.equals(path, pathInfo.path) &&
                        Objects.equals(currentStreet, pathInfo.currentStreet);
            }

            @Override
            public int hashCode() {
                return Objects.hash(path, currentStreet);
            }
        }

        List<Coordinates> coordinates = new ArrayList<>();
        List<PathInfo> open = new ArrayList<>();
        HashSet<PathInfo> found = new HashSet<>();

        int nextStopIndex = stops.indexOf(currentStop) + 1;
        if(nextStopIndex >= stops.size()){
            return null;
        }
        Stop nextStop = stops.get(nextStopIndex);
        Street nextStopStreet = getStopStreet(nextStop);
        coordinates.add(currentStop.getCoordinates());
        Street stopStreet = getStopStreet(currentStop);

        if(stopStreet == null || nextStopStreet == null) {
            return null;
        }

        if(nextStopStreet.equals(stopStreet)) {
            coordinates.add(nextStop.getCoordinates());
            return new Path(coordinates);
        }

        open.add(new PathInfo(new ArrayList<>(coordinates), stopStreet));
        for( int i = 0; i < open.size(); i++) {
            List<Street> neighbors = getNeighbors(open.get(i).currentStreet);
            for(Street neighbor: neighbors) {
                ArrayList<Coordinates> newCoordinates = new ArrayList<>(open.get(i).path);
                Coordinates crossing = open.get(i).currentStreet.getCrossingCoordinates(neighbor);
                if(crossing != null)
                    newCoordinates.add(crossing);
                PathInfo info = new PathInfo(newCoordinates, neighbor);
                if(info.currentStreet.equals(nextStopStreet)) {
                    info.path.add(info.currentStreet.getCrossingCoordinates(nextStopStreet));
                    found.add(info);
                } else {
                    boolean foundFlag = false;
                    for(PathInfo openInfo : open) {
                        if(openInfo.currentStreet.equals(info.currentStreet)) {
                            foundFlag = true;
                            if(openInfo.path.size() > info.path.size()) {
                                open.remove(openInfo);
                                open.add(info);
                            }
                            break;
                        }
                    }
                    if(!foundFlag) {
                        open.add(info);
                    }
                }
            }
        }

        Path closest = null;
        for(PathInfo info : found) {
            info.path.add(nextStop.getCoordinates());
            Path path = new Path(info.path);
            if(closest == null) {
                closest = path;
            } else {
                if(closest.getPathLenght() > path.getPathLenght()) {
                    closest = path;
                }
            }
        }

        return closest;
    }

    /**
     * Returns stops on line
     * @return Stops on line
     */
    public List<Stop> getStops() {
        return stops;
    }

    /**
     * Returns line name
     * @return Line name
     */
    public String getName() {
        return name;
    }

    public Shape getGui() {
        Shape shape = null;
        for (int i = 0; i < stops.size() - 1; i++) {
            Stop stop = stops.get(i);
            Path path = getPathToNextStop(stop);
            if (shape == null) {
                shape = path.getShape();
            } else {
                shape = Shape.union(shape, path.getShape());
            }
        }
        if (shape != null) {
            shape.setFill(Color.FORESTGREEN);
        }
        return shape;
    }

    @Override
    public String toString() {
        return "Line{" +
                "name='" + name + '\'' +
                '}';
    }
}
