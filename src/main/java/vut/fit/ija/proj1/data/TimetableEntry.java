package vut.fit.ija.proj1.data;

import vut.fit.ija.proj1.gui.elements.Stop;

import java.time.LocalTime;

public class TimetableEntry {
    private Stop stop;
    private LocalTime time;

    public TimetableEntry(Stop stop, LocalTime time) {
        this.stop = stop;
        this.time = time;
    }

    public Stop getStop() {
        return stop;
    }

    public LocalTime getTime() {
        return time;
    }
}
