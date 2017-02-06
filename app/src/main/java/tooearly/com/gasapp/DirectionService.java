package tooearly.com.gasapp;

import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DirectionService {
    private static String param(String param) {
        try {
            return URLEncoder.encode(param, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            //noinspection deprecation
            return URLEncoder.encode(param);
        }
    }

    public static String getDirections(Context context, String origin, String destination) {
        String key = context.getResources().getString(R.string.google_maps_api_key);
        String url_part = context.getResources().getString(R.string.uri_google_maps_api);

        HttpResponse response = HttpRequest.Get(url_part + "?key=" + param(key) + "&origin=" + param(origin) + "&destination=" + param(destination));
        if (response == null) return null;

        return response.data;
    }
}
