/**
 * @authors xjerab25
 * File containing definition of {@link vut.fit.ija.proj1.data.TimetableEntry}  class representing entry in
 * {@link vut.fit.ija.proj1.data.Timetable}
 */
package vut.fit.ija.proj1.data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import vut.fit.ija.proj1.gui.elements.VehicleStop;

import java.time.LocalTime;
import java.util.Objects;

/**
 * Class representing single entry in {@link Timetable}
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class)
public class TimetableEntry {
    private VehicleStop stop;
    private LocalTime time;

    /**
     * Default constructor for jackson deserialization
     */
    private TimetableEntry() {
    }

    /**
     * Constructs new timetable entry
     * @param stop stop
     * @param time time
     */
    public TimetableEntry(VehicleStop stop, LocalTime time) {
        this.stop = stop;
        this.time = time;
    }

    /**
     * Returns timetable entry stop
     * @return timetable entry stop
     */
    public VehicleStop getStop() {
        return stop;
    }

    /**
     * Returns timetable entry time
     * @return timetable entry time
     */
    public LocalTime getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimetableEntry entry = (TimetableEntry) o;
        return Objects.equals(stop, entry.stop) &&
                Objects.equals(time, entry.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stop, time);
    }

    @Override
    public String toString() {
        return stop + "\t" + time;
    }
}
