package vut.fit.ija.proj1.data;

import vut.fit.ija.proj1.gui.elements.Stop;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;

public class Timetable {
    private List<TimetableEntry> entries;

    public Timetable(List<TimetableEntry> entries) {
        this.entries = entries;
    }

    public List<TimetableEntry> getEntries() {
        return entries;
    }

    public Stop getNextStop(LocalTime currentTime) {
        TimetableEntry nearest = null;
        for(TimetableEntry entry : entries) {
            if(nearest == null) {
                nearest = entry;
            } else {
                if(nearest.getTime().isAfter(entry.getTime())) {
                    nearest = entry;
                }
            }
        }
        return nearest != null ? nearest.getStop() : null;
    }

    public void addEntry(TimetableEntry entry) {
        entries.add(entry);
    }
}
