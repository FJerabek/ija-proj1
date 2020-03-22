package vut.fit.ija.proj1.data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import vut.fit.ija.proj1.gui.elements.VehicleStop;

import java.time.LocalTime;
import java.util.List;

/**
 * Class representing timetable
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class)
public class Timetable {
    private List<TimetableEntry> entries;
    private int i = 0;

    public Timetable() {
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
     * Return next timetable entry according to time
     * @param currentTime current time
     * @return next timetable entry
     */
    @JsonIgnore
    public TimetableEntry getNextEntry(LocalTime currentTime, List<VehicleStop> stops) {
        TimetableEntry next = null;
        for (TimetableEntry entry : entries) {
            if(next == null && entry.getTime().isAfter(currentTime) && stops.contains(entry.getStop())) {
                next = entry;
            } else if (next != null &&
                    entry.getTime().isAfter(currentTime) &&
                    entry.getTime().isBefore(next.getTime()) &&
                    stops.contains(entry.getStop())) {
                next = entry;
            }
        }
        return next;
    }

    @JsonIgnore
    public TimetableEntry getPreviousEntry(LocalTime currentTime, List<VehicleStop> stops) {
        TimetableEntry previous = null;
        for (TimetableEntry entry : entries) {
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
