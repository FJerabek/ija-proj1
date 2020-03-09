package vut.fit.ija.proj1.data;

import vut.fit.ija.proj1.gui.elements.Stop;

import java.time.LocalTime;
import java.util.List;

public class Timetable {
    private List<TimetableEntry> entries;

    public Timetable(List<TimetableEntry> entries) {
        this.entries = entries;
    }

    public List<TimetableEntry> getEntries() {
        return entries;
    }

    public TimetableEntry getPreviousEntry(LocalTime currentTime) {
        TimetableEntry nearest = null;
        for(TimetableEntry entry : entries) {
            if(nearest == null && entry.getTime().isBefore(LocalTime.now())) {
                nearest = entry;
            } else {
                if(nearest != null && entry.getTime().isAfter(nearest.getTime()) && entry.getTime().isBefore(currentTime)) {
                    nearest = entry;
                }
            }
        }
        return nearest;
    }

    public TimetableEntry getNextEntry(LocalTime currentTime) {
        TimetableEntry nearest = null;
        for(TimetableEntry entry : entries) {
            if(nearest == null && entry.getTime().isAfter(LocalTime.now())) {
                nearest = entry;
            } else {
                if(nearest != null && entry.getTime().isBefore(nearest.getTime()) && entry.getTime().isAfter(currentTime)) {
                    nearest = entry;
                }
            }
        }
        return nearest;
    }

    public void addEntry(TimetableEntry entry) {
        entries.add(entry);
    }
}
