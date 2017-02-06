package tooearly.com.gasapp;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DirectionService {
    private static final String TAG = "DirectionService";

    private static String param(String param) {
        try {
            return URLEncoder.encode(param, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            //noinspection deprecation
            return URLEncoder.encode(param);
        }
    }
    private static String encode_waypoints(String... waypoints) {
        if (waypoints == null || waypoints.length == 0) return "";
        StringBuffer buff = new StringBuffer();
        buff.append("&waypoints=");
        buff.append(param(waypoints[0]));
        for (int q = 1; q < waypoints.length; q++) {
            buff.append("|");
            buff.append(param(waypoints[q]));
        }
        return buff.toString();
    }

    public static NavigationDirections getDirections(Context context, String origin, String destination, String... waypoints) {
        String key = context.getResources().getString(R.string.google_maps_api_key);
        String url_part = context.getResources().getString(R.string.uri_google_maps_api);

        String request_uri = url_part + "?key=" + param(key) + "&origin=" + param(origin) + "&destination=" + param(destination) + "&mode=driving" +  encode_waypoints(waypoints);
        HttpResponse response = HttpRequest.Get(request_uri);
        if (response == null) return null;

        try {
            JSONObject obj = new JSONObject(response.data);
            JSONObject routeObj = obj.getJSONArray("routes").getJSONObject(0);
            JSONArray routeLegsArr = routeObj.getJSONArray("legs");

            JSONObject[] legs = new JSONObject[routeLegsArr.length()];
            for (int q = 0; q < routeLegsArr.length(); q++) {
                legs[q] = routeLegsArr.getJSONObject(q);
            }
            return new NavigationDirections(origin, destination, waypoints, legs);
        }
        catch (JSONException e) {
            Log.wtf(TAG, e);
            return null;
        }
    }
}
