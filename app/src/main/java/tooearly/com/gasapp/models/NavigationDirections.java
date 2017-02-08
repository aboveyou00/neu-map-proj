package tooearly.com.gasapp.models;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class NavigationDirections implements Serializable {
    public NavigationDirections(String origin, String destination, String[] waypoints, NavigationLeg[] legs, String polyline) {
        this.origin = origin;
        this.destination = destination;
        this.waypoints = waypoints;
        this.legs = legs;
        this.polyline = polyline;
    }

    public NavigationLeg[] legs;
    public String origin, destination;
    public String[] waypoints;
    public String polyline;
}
