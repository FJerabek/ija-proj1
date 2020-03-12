package vut.fit.ija.proj1.data;

import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import vut.fit.ija.proj1.gui.elements.Street;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing path in the map
 */
public class Path {
    private List<Coordinates> path;

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

    /**
     * Returns neighboring streets by specifying street and coordinates of street end
     * @param coords street end coordinates
     * @param streets street to find neighbors to
     * @return street neighbors
     */
    private static List<Coordinates> getNeighbors(Coordinates coords, List<Street> streets) {
        List<Coordinates> neighbors = new ArrayList<>();
        for(Street street : streets) {
            if((street.getFrom().equals(coords) || street.getTo().equals(coords))) {
                Coordinates nextCoords = street.getFrom().equals(coords)? street.getTo() : street.getFrom();
                if(!neighbors.contains(nextCoords)) {
                    neighbors.add(nextCoords);
                }
            }
        }
        return neighbors;
    }

    /**
     * Returns path from and to specified coordinates Coordinates must be one of the ends of street or
     * stop on any of these streets.
     * @param from From coordinates
     * @param to To coordinates
     * @param streets all streets
     * @return path between from and to coordinates;
     */
    public static Path getPath(Coordinates from, Coordinates to,List<Street> streets) {
        class PathInfo {
            /**
             *
             */
            List<Coordinates> path;
            Coordinates coords;

            /**
             * Path info constructor
             * @param path path as series of coordinates
             * @param coords current coordinates
             */
            PathInfo(List<Coordinates> path, Coordinates coords) {
                this.path = path;
                this.coords = coords;
            }
        }

        List<PathInfo> found = new ArrayList<>();
        List<Coordinates> processed = new ArrayList<>();

        List<PathInfo> open = new ArrayList<>();
        open.add(new PathInfo(new ArrayList<>(), from));

        for (int i = 0; i < open.size(); i++) {
            PathInfo info = open.get(i);
            List<Coordinates> neighbors = getNeighbors(info.coords, streets);
            for (Coordinates in : neighbors) {
                if (!processed.contains(in)) {
                    processed.add(in);
                    List<Coordinates> path = new ArrayList<>(info.path);
                    path.add(info.coords);
                    PathInfo pathInfo = new PathInfo(path, in);
                    open.add(pathInfo);
                    if (in.equals(to)) {
                        found.add(pathInfo);
                    }
                }
            }
        }
        PathInfo closest = null;
        for (PathInfo info : found) {
            if(closest == null) {
                closest = info;
            } else {
                if(info.path.size() < closest.path.size()) {
                    closest = info;
                }
            }
        }
        if(closest != null) {
            closest.path.add(closest.coords);
        }
        return closest != null? new Path(closest.path) : null;
    }
}
