package vut.fit.ija.proj1.data;

import vut.fit.ija.proj1.gui.elements.Street;

import java.util.ArrayList;
import java.util.List;

public class Path {
    private List<Coordinates> path;

    public Path(List<Coordinates> path) {
        this.path = path;
    }

    public List<Coordinates> getPath() {
        return path;
    }

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

    public double getPathLenght() {
        double length = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Coordinates a = path.get(i);
            Coordinates b = path.get(i+1);
            length+=Math.sqrt(Math.pow(Math.abs(a.getX() - b.getX()),2) + Math.pow(Math.abs(a.getY() - b.getY()),2));
        }
        return length;
    }

    public static List<Coordinates> getNeighbors(Coordinates coords, List<Street> streets) {
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

    public static Path getPath(Coordinates from, Coordinates to,List<Street> streets) {
        class PathInfo {
            List<Coordinates> path;
            Coordinates coords;

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
