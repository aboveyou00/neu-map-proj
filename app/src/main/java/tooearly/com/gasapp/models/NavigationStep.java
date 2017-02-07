package tooearly.com.gasapp.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class NavigationStep implements Serializable {
    NavigationStep(int distanceMeters, float startLat, float startLng, float endLat, float endLng, String polylinePoints, String htmlInstructions) {
        this(distanceMeters / METERS_PER_MILE, startLat, startLng, endLat, endLng, polylinePoints, htmlInstructions);
    }
    NavigationStep(float distanceMiles, float startLat, float startLng, float endLat, float endLng, String polylinePoints, String htmlInstructions) {
        this.distanceMiles = distanceMiles;
        this.startLat = startLat;
        this.startLng = startLng;
        this.endLat = endLat;
        this.endLng = endLng;
        this.polylinePoints = polylinePoints;
        this.htmlInstructions = htmlInstructions;
    }

    public static NavigationStep fromJson(JSONObject obj) throws JSONException {
        int distanceMeters = obj.getJSONObject("distance").getInt("value");

        JSONObject startObj = obj.getJSONObject("start_location");
        float startLat = (float)startObj.getDouble("lat");
        float startLng = (float)startObj.getDouble("lng");

        JSONObject endObj = obj.getJSONObject("end_location");
        float endLat = (float)endObj.getDouble("lat");
        float endLng = (float)endObj.getDouble("lng");

        String polyline = obj.getJSONObject("polyline").getString("points");
        String htmlInstructions = obj.getString("html_instructions");

        return new NavigationStep(distanceMeters, startLat, startLng, endLat, endLng, polyline, htmlInstructions);
    }

    public static final float METERS_PER_MILE = 1609.34f;

    public final float distanceMiles;
    public final float startLat, startLng, endLat, endLng;
    public final String polylinePoints, htmlInstructions;
}
