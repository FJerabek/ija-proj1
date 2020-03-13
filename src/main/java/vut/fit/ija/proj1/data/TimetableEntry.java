package vut.fit.ija.proj1.data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import vut.fit.ija.proj1.gui.elements.Stop;

import java.time.LocalTime;
import java.util.Objects;

/**
 * Class representing single timetable entry
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class)
public class TimetableEntry {
    private Stop stop;
    private LocalTime time;

    public TimetableEntry() {
    }

    /**
     * Timietable entry constructor
     * @param stop stop
     * @param time time
     */
    public TimetableEntry(Stop stop, LocalTime time) {
        this.stop = stop;
        this.time = time;
    }

    /**
     * Returns timetable entry stop
     * @return timetable entry stop
     */
    public Stop getStop() {
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
        return "TimetableEntry{" +
                "stop=" + stop +
                ", time=" + time +
                '}';
    }
}
