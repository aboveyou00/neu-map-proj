package tooearly.com.gasapp.services;

import android.app.Activity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tooearly.com.gasapp.R;
import tooearly.com.gasapp.models.FuelType;
import tooearly.com.gasapp.models.GasStation;
import tooearly.com.gasapp.util.HttpRequest;
import tooearly.com.gasapp.util.HttpResponse;

public class GasPriceService {
    public static final String TAG = "GasPriceService";

    public GasStation[] getStations(Activity context, int latitude, int longitude, int radius, FuelType type) {
        return getStations(context, latitude, longitude, radius, type, true);
    }
    public GasStation[] getStations(Activity context, int latitude, int longitude, int radius, FuelType type, boolean distanceFirst) {
        String base_uri = context.getResources().getString(R.string.uri_gas_price_api);
        String key = context.getResources().getString(R.string.gas_price_api_key);
        HttpResponse response = HttpRequest.Get(base_uri + "stations/radius/" + latitude + "/" + longitude + "/" + radius + "/" + type.value + "/" + (distanceFirst ? "distance/" : "price/") + key + ".json");
        if (response == null) return null;
        try {
            JSONObject obj = new JSONObject(response.data);
            JSONArray stationsArr = obj.getJSONArray("stations");
            GasStation[] stations = new GasStation[stationsArr.length()];
            for (int q = 0; q < stationsArr.length(); q++) {
                JSONObject stationObj = stationsArr.getJSONObject(q);
                GasStation station = jsonToGasStation(stationObj, type);
                stations[q] = station;
            }
            return stations;
        }
        catch (JSONException e) {
            Log.wtf(TAG, e);
            return null;
        }
    }

    public GasStation getGasStation(Activity context, String id, FuelType type) {
        String base_uri = context.getResources().getString(R.string.uri_gas_price_api);
        String key = context.getResources().getString(R.string.gas_price_api_key);
        HttpResponse response = HttpRequest.Get(base_uri + "stations/details/" + id + "/" + key + ".json");
        if (response == null) return null;
        try {
            JSONObject obj = new JSONObject(response.data);
            JSONObject stationObj = obj.getJSONObject("details");
            return jsonToGasStation(stationObj, type);
        }
        catch (JSONException e) {
            Log.wtf(TAG, e);
            return null;
        }
    }

    private GasStation jsonToGasStation(JSONObject stationObj, FuelType type) throws JSONException {
        double lat = Double.parseDouble(stationObj.getString("lat")),
               lng = Double.parseDouble(stationObj.getString("lng")),
               price = Double.parseDouble(stationObj.getString(type.value + "_price"));
        String stationName = stationObj.getString("station"),
               address = stationObj.getString("address"),
               priceDate = stationObj.getString(type.value + "_date"),
               id = stationObj.getString("id");
        return new GasStation(id, stationName, address, lat, lng, price, priceDate);
    }
}
