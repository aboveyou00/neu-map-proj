package tooearly.com.gasapp;

import java.io.Serializable;

public class PlannedTrip implements Serializable {
    public PlannedTrip(String origin, String destination, String... stops) {
        this.origin = origin;
        this.destination = destination;
        this.stops = stops;
    }

    String origin, destination;
    String[] stops;
}
