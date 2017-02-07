package tooearly.com.gasapp.models;

import java.io.Serializable;

public class NavigationDirections implements Serializable {
    public NavigationDirections(String origin, String destination, String[] waypoints, NavigationLeg[] legs) {
        this.origin = origin;
        this.destination = destination;
        this.waypoints = waypoints;
        this.legs = legs;
    }

    public NavigationLeg[] legs;
    public String origin, destination;
    public String[] waypoints;
}
