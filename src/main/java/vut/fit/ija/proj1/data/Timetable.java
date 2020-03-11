package vut.fit.ija.proj1.data;

import vut.fit.ija.proj1.gui.elements.Stop;

import java.time.LocalTime;
import java.util.List;

/**
 * Class representing timetable
 */
public class Timetable {
    private List<TimetableEntry> entries;
    private int i = 0;

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
    public TimetableEntry getNextEntry(LocalTime currentTime) {
        TimetableEntry next = null;
        for (TimetableEntry entry : entries) {
            if(next == null && entry.getTime().isAfter(currentTime)) {
                next = entry;
            } else if (next != null && entry.getTime().isAfter(currentTime) && entry.getTime().isBefore(next.getTime())) {
                next = entry;
            }

        }
        return next;
    }

    /**
     * Adds new timetable entry
     * @param entry new entry
     */
    public void addEntry(TimetableEntry entry) {
        entries.add(entry);
    }
}
