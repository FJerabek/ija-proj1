/**
 * @author xjerab25
 * File containing Timetable creator class
 */
package vut.fit.ija.proj1.data.file;

import com.fasterxml.jackson.annotation.JsonIgnore;
import vut.fit.ija.proj1.data.Timetable;
import vut.fit.ija.proj1.data.TimetableEntry;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for generating line timetable
 */
public class TimetableCreator {
    private List<TimetableEntry> entries;
    @JsonIgnore
    private final List<TimetableEntry> timetable = new ArrayList<>();
    @JsonIgnore
    private final List<Integer> time = new ArrayList<>();
    @JsonIgnore
    private int secondsOfDay = 0;

    /**
     * Default constructor for json deserialization
     */
    private TimetableCreator() {
    }

    /**
     * Return entries from timetable creator
     * @return timetable creator entries
     */
    public List<TimetableEntry> getEntries() {
        return entries;
    }


    /**
     * Sets entries for timetable creator
     * @param entries creator entries
     */
    public void setEntries(List<TimetableEntry> entries) {
        this.entries = entries;
    }

    /**
     * Generates timetable from provided entries
     * @return timetable
     */
    public Timetable getTimetable() {
        for(int i = 1; i < entries.size(); i++) {
            TimetableEntry previous = entries.get(i-1);
            TimetableEntry actual = entries.get(i);
            time.add(actual.getTime().toSecondOfDay() - previous.getTime().toSecondOfDay());
        }

        do {
            for(int i = 0; i < entries.size() - 1 && secondsOfDay < 86399; i++) {
                TimetableEntry entry = entries.get(i);
                timetable.add(new TimetableEntry(entry.getStop(), LocalTime.ofSecondOfDay(secondsOfDay)));
                secondsOfDay += time.get(i);
            }
        } while(secondsOfDay < 86399);
        return new Timetable(timetable);
    }
}
