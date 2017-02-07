package tooearly.com.gasapp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class NavigationLeg implements Serializable {
    public NavigationLeg(String startAddress, String endAddress, float startLat, float startLng, float endLat, float endLng, NavigationStep... steps) {
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.startLat = startLat;
        this.startLng = startLng;
        this.endLat = endLat;
        this.endLng = endLng;
        this.steps = steps;
    }

    public static NavigationLeg fromJson(JSONObject obj) throws JSONException {
        String startAddr = obj.getString("start_address");
        JSONObject startObj = obj.getJSONObject("start_location");
        float startLat = (float)startObj.getDouble("lat");
        float startLng = (float)startObj.getDouble("lng");

        String endAddr = obj.getString("end_address");
        JSONObject endObj = obj.getJSONObject("end_location");
        float endLat = (float)endObj.getDouble("lat");
        float endLng = (float)endObj.getDouble("lng");

        JSONArray stepsArr = obj.getJSONArray("steps");
        NavigationStep[] steps = new NavigationStep[stepsArr.length()];
        for (int q = 0; q < steps.length; q++) {
            steps[q] = NavigationStep.fromJson(stepsArr.getJSONObject(q));
        }

        return new NavigationLeg(startAddr, endAddr, startLat, startLng, endLat, endLng, steps);
    }

    public final String startAddress, endAddress;
    public final float startLat, startLng, endLat, endLng;
    public final NavigationStep[] steps;
}
