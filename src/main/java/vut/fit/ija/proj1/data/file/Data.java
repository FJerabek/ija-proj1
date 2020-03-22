package vut.fit.ija.proj1.data.file;

import vut.fit.ija.proj1.data.VehicleLine;
import vut.fit.ija.proj1.gui.elements.VehicleStop;
import vut.fit.ija.proj1.gui.elements.Street;
import vut.fit.ija.proj1.gui.elements.Vehicle;

import java.util.List;

public class Data {
    private List<VehicleStop> stops;
    private List<Street> streets;
    private List<VehicleLine> lines;
    private List<Vehicle> vehicles;


    public Data() {}

    public Data(List<Street> streets, List<VehicleStop> stops, List<VehicleLine> lines, List<Vehicle> vehicles) {
        this.streets = streets;
        this.stops = stops;
        this.lines = lines;
        this.vehicles = vehicles;
    }

    public List<VehicleLine> getLines() {
        return lines;
    }

    public List<Street> getStreets() {
        return streets;
    }

    public List<VehicleStop> getStops() {
        return stops;
    }

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
