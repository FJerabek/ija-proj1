/**
 * @author xjerab25
 * File containing definition of {@link vut.fit.ija.proj1.data.Timetable} class representing vehicle timetable
 */
package vut.fit.ija.proj1.data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import vut.fit.ija.proj1.gui.elements.VehicleStop;

import java.time.LocalTime;
import java.util.List;

/**
 * Class representing vehicle timetable
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class)
public class Timetable {
    private List<TimetableEntry> entries;
    private int i = 0;

    /**
     * Default constructor for jackson deserialization
     */
    private Timetable() {
    }

    /**
     * Timetable constructor
     * @param entries timetable entries
     */
    public Timetable(List<TimetableEntry> entries) {
        this.entries = entries;
    }

    /**
     * Returns all timetable entries
     * @return timetable entries
     */
    public List<TimetableEntry> getEntries() {
        return entries;
    }


    /**
     * Return closest next {@link TimetableEntry} according to time and provided stops
     * @param currentTime time to find closest next timetable entry
     * @param stops stops to find timetable entry for
     * @return closest next timetable entry on one of provided stops
     */
    @JsonIgnore
    public TimetableEntry getNextEntry(LocalTime currentTime, List<VehicleStop> stops) {
        TimetableEntry next = null;
        TimetableEntry first = null;
        for (TimetableEntry entry : entries) {
            if(first == null || entry.getTime().isBefore(first.getTime())) {
                first = entry;
            }
            if(next == null && entry.getTime().isAfter(currentTime) && stops.contains(entry.getStop())) {
                next = entry;
            } else if (next != null &&
                    entry.getTime().isAfter(currentTime) &&
                    entry.getTime().isBefore(next.getTime()) &&
                    stops.contains(entry.getStop())) {
                next = entry;
            }
        }
        if(next == null) {
            next = first;
        }
        return next;
    }

    /**
     * Returns closest previous {@link TimetableEntry} in timeline
     * @param currentTime time to find closest previous timetable entry
     * @param stops stops to find timetable entry for
     * @return closest previous timetable entry on one of provided stops
     */
    @JsonIgnore
    public TimetableEntry getPreviousEntry(LocalTime currentTime, List<VehicleStop> stops) {
        TimetableEntry previous = null;
        TimetableEntry last = null;
        for (TimetableEntry entry : entries) {
            if(last == null || entry.getTime().isBefore(last.getTime())) {
                last = entry;
            }
            if(previous == null &&
                    entry.getTime().isBefore(currentTime) &&
                    stops.contains(entry.getStop())
            ) {
                previous = entry;
            } else if (previous != null &&
                    entry.getTime().isBefore(currentTime) &&
                    entry.getTime().isAfter(previous.getTime()) &&
                    stops.contains(entry.getStop())
            ) {
                previous = entry;
            }

        }
        if(previous == null) {
            previous = last;
        }
        return previous;
    }

    /**
     * Adds new timetable entry
     * @param entry new entry
     */
    public void addEntry(TimetableEntry entry) {
        entries.add(entry);
    }
}
