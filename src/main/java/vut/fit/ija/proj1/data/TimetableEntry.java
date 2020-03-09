package vut.fit.ija.proj1.data;

import vut.fit.ija.proj1.gui.elements.Stop;

import java.time.LocalTime;
import java.util.Objects;

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
