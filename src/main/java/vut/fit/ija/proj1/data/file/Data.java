/**
 * @author xjerab25
 * File containing deserialization data structure
 */
package vut.fit.ija.proj1.data.file;

import vut.fit.ija.proj1.data.VehicleLine;
import vut.fit.ija.proj1.gui.elements.Street;
import vut.fit.ija.proj1.gui.elements.Vehicle;
import vut.fit.ija.proj1.gui.elements.VehicleStop;

import java.util.List;

/**
 * Deserialization file structure class
 */
public class Data {
    private List<VehicleStop> stops;
    private List<Street> streets;
    private List<VehicleLine> lines;
    private List<Vehicle> vehicles;


    /**
     * Default constructor for jackson deserialization
     */
    private Data() {}

    /**
     * Return vehicle lines lines
     * @return vehicle lines
     */
    public List<VehicleLine> getLines() {
        return lines;
    }

    /**
     * Return streets
     * @return streets
     */
    public List<Street> getStreets() {
        return streets;
    }

    /**
     * Return vehicle stops
     * @return vehicle stops
     */
    public List<VehicleStop> getStops() {
        return stops;
    }


    /**
     * Return vehicles
     * @return vehicles
     */
    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    @Override
    public String toString() {
        return "Data{" +
                "stops=" + stops +
                ", streets=" + streets +
                ", lines=" + lines +
                ", vehicles=" + vehicles +
                '}';
    }
}
