package tooearly.com.gasapp.services;

import android.app.Activity;
import android.support.annotation.Nullable;
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
    public static double lastPriceUsed = 3.50;

    @Nullable
    public static GasStation[] getStations(Activity context, float latitude, float longitude, float radiusMiles, FuelType type) {
        return getStations(context, latitude, longitude, radiusMiles, type, true);
    }
    @Nullable
    public static GasStation[] getStations(Activity context, float latitude, float longitude, float radiusMiles, FuelType type, boolean distanceFirst) {
        String base_uri = context.getResources().getString(R.string.uri_gas_price_api);
        String key = context.getResources().getString(R.string.gas_price_api_key);
        String requestUrl = base_uri + "stations/radius/" + latitude + "/" + longitude + "/" + radiusMiles + "/" + type.value + "/" + (distanceFirst ? "distance/" : "price/") + key + ".json";
        HttpResponse response = HttpRequest.Get(requestUrl);
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

    public static GasStation getGasStation(Activity context, String id, FuelType type) {
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

    private static GasStation jsonToGasStation(JSONObject stationObj, FuelType type) throws JSONException {
        double lat = Double.parseDouble(stationObj.getString("lat")),
                lng = Double.parseDouble(stationObj.getString("lng"));

        double price;

        String priceStr = stationObj.getString(type.value + "_price");

        if(priceStr.equals("N/A")) {
            price = lastPriceUsed;
        } else {

            price = Double.parseDouble(priceStr);
        }
        lastPriceUsed = price;
        String stationName = stationObj.getString("station"),
               address = stationObj.getString("address"),
               priceDate = stationObj.getString(type.value + "_date"),
               id = stationObj.getString("id");
        return new GasStation(id, stationName, address, lat, lng, price, priceDate);
    }
}
