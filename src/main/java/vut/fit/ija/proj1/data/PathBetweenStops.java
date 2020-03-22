/**
 * @author xjerab25
 * File contains definition of class {@link vut.fit.ija.proj1.data.PathBetweenStops} which defines path between two stops
 */
package vut.fit.ija.proj1.data;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import vut.fit.ija.proj1.data.exceptions.StreetsNotConnectedException;
import vut.fit.ija.proj1.gui.elements.Street;
import vut.fit.ija.proj1.gui.elements.VehicleStop;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Defines path between two stops
 */
@JsonDeserialize(converter= PathBetweenStops.PostJacksonConstruct.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@JsonIgnoreProperties(value = {
        "stop1Path",
        "stop2Path",
        "invalid"
})
public class PathBetweenStops {
    private VehicleStop stop1;
    private VehicleStop stop2;
    @JsonProperty("streetPath")
    private List<Street> streetPath;
    private Path stop1Path;
    private Path stop2Path;
    private boolean invalid;
    private Duration delay = Duration.ofSeconds(0);

    /**
     * Default no parameter constructor for jackson deserialization
     */
    private PathBetweenStops() {
    }

    /**
     * Constructs new path between two stops with set parameters
     * @param stop1 First stop between which path is defined
     * @param stop2 Second stop between which path is defined
     * @param streetPath Streets on which is path generated. Must be connecting and in correct order
     * @param delay Path delay
     * @throws StreetsNotConnectedException when streets does not connect or are in incorrect order
     */
    public PathBetweenStops(VehicleStop stop1, VehicleStop stop2, List<Street> streetPath, Duration delay) throws StreetsNotConnectedException {
        this.stop1 = stop1;
        this.stop2 = stop2;
        this.streetPath = streetPath;
        this.delay = delay;

        stop1Path = createPathFromStop1();
        stop2Path = createPathFromStop2();
    }

    /**
     * Returns first stop
     * @return first stop
     */
    public VehicleStop getStop1() {
        return stop1;
    }

    /**
     * Return second stop
     * @return second stop
     */
    public VehicleStop getStop2() {
        return stop2;
    }

    /**
     * Returns streets from which is path generated
     * @return streets from which is path generated
     */
    public List<Street> getStreetPath() {
        return streetPath;
    }

    /**
     * Returns if path between stops is invalid.
     * Invalidation of path can be caused by closing one of it streets
     * @return if path between stops is invalid
     */
    public boolean isInvalid() {
        return invalid;
    }


    /**
     * Sets if path is invalid
     * @param invalid path invalid state
     */
    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }


    /**
     * Creates {@link Path} starting from first stop
     * @return path from first stop
     * @throws StreetsNotConnectedException when streets does not connect or are in incorrect order
     */
    private Path createPathFromStop1() throws StreetsNotConnectedException {
        return new Path(stop1.getCoordinates(), stop2.getCoordinates(), streetPath, delay);
    }

    /**
     * Creates {@link Path} starting from second stop
     * @return path from second stop
     * @throws StreetsNotConnectedException when streets does not connect or are in incorrect order
     */
    private Path createPathFromStop2() throws StreetsNotConnectedException {
        List<Street> reversedPath = new ArrayList<>(streetPath);
        Collections.reverse(reversedPath);
        return new Path(stop2.getCoordinates(), stop1.getCoordinates(), reversedPath, delay);
    }

    /**
     * Returns path delay
     * @return path delay
     */
    public Duration getDelay() {
        return delay;
    }

    /**
     * Returns {@link Path} starting from first stop
     * @return path starting from first stop
     */
    public Path getPathFromStop1() {
        return stop1Path;
    }

    /**
     * Returns {@link Path} starting from second stop
     * @return path starting from second stop
     */
    public Path getPathFromStop2() {
        return stop2Path;
    }

    /**
     * This method is called automatically after jackson completed deserialization
     * @throws StreetsNotConnectedException streets defined in data does not connect
     */
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

        return (stop1.equals(that.stop1) || stop1.equals(that.stop2)) &&
                (stop2.equals(that.stop1) || stop2.equals(that.stop2));
    }

    @Override
    public int hashCode() {
        return Objects.hash(stop1, stop2);
    }

    /**
     * Class causing call of the post constructor method after jackson completed data deserialization
     */
    public static class PostJacksonConstruct extends StdConverter<PathBetweenStops, PathBetweenStops> {
        /**
         * Gets called after data deserialization
         */
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
