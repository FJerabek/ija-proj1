package vut.fit.ija.proj1.data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import vut.fit.ija.proj1.data.exceptions.StreetsNotConnectedException;
import vut.fit.ija.proj1.gui.elements.Street;
import vut.fit.ija.proj1.gui.elements.VehicleStop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@JsonDeserialize(converter= PathBetweenStops.PostJacksonConstruct.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class PathBetweenStops {
    private VehicleStop stop1;
    private VehicleStop stop2;
    @JsonProperty("streetPath")
    private List<Street> streetPath;
    @JsonIgnore
    private Path stop1Path;
    @JsonIgnore
    private Path stop2Path;
    @JsonIgnore
    private boolean invalid;

    public PathBetweenStops() {
    }

    public PathBetweenStops(VehicleStop stop1, VehicleStop stop2, List<Street> streetPath) throws StreetsNotConnectedException {
        this.stop1 = stop1;
        this.stop2 = stop2;
        this.streetPath = streetPath;

        stop1Path = createPathFromStop1();
        stop2Path = createPathFromStop2();
    }

    public VehicleStop getStop1() {
        return stop1;
    }

    public VehicleStop getStop2() {
        return stop2;
    }

    public List<Street> getStreetPath() {
        return streetPath;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    private Path createPathFromStop1() throws StreetsNotConnectedException {
        return new Path(stop1.getCoordinates(), stop2.getCoordinates(), streetPath);
    }

    private Path createPathFromStop2() throws StreetsNotConnectedException {
        List<Street> reversedPath = new ArrayList<>(streetPath);
        Collections.reverse(reversedPath);
        return new Path(stop2.getCoordinates(), stop1.getCoordinates(), reversedPath);
    }

    public Path getPathFromStop1() {
        return stop1Path;
    }

    public Path getPathFromStop2() {
        return stop2Path;
    }

    private void postConstruct() throws StreetsNotConnectedException {
        stop1Path = createPathFromStop1();
        stop2Path = createPathFromStop2();
    }

    @Override
    public String toString() {
        return String.format("%s <---> %s", stop1, stop2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathBetweenStops that = (PathBetweenStops) o;
        return stop1.equals(that.stop1) || stop1.equals(that.stop2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stop1, stop2);
    }

    public static class PostJacksonConstruct extends StdConverter<PathBetweenStops, PathBetweenStops> {
        @Override
        public PathBetweenStops convert(PathBetweenStops value) {
            try {
                value.postConstruct();
            } catch (StreetsNotConnectedException e) {
                System.err.println("Serialization ended with error: " + e.getMessage());
                e.printStackTrace();
            }
            return value;
        }
    }
}
