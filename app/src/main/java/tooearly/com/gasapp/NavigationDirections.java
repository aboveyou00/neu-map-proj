package tooearly.com.gasapp;

import org.json.JSONObject;

import java.io.Serializable;

public class NavigationDirections implements Serializable {
    public NavigationDirections(String origin, String destination, String[] waypoints, JSONObject[] legs) {
        this.origin = origin;
        this.destination = destination;
        this.waypoints = waypoints;
        this.legs = legs;
    }

    public JSONObject[] legs;
    public String origin, destination;
    public String[] waypoints;
}
