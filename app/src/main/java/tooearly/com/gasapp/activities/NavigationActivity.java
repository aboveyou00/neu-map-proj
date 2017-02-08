package tooearly.com.gasapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import tooearly.com.gasapp.R;
import tooearly.com.gasapp.models.NavigationDirections;
import tooearly.com.gasapp.models.NavigationLeg;
import tooearly.com.gasapp.models.TripOptions;

public class NavigationActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String TAG = "NavigationActivity";
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        directions = (NavigationDirections)intent.getSerializableExtra(PlanRouteActivity.DIRECTIONS_EXTRA);
        options = (TripOptions)intent.getSerializableExtra(TripOptionsActivity.TRIP_OPTIONS_EXTRA);

        setContentView(R.layout.activity_navigation);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    NavigationDirections directions;
    TripOptions options;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap = googleMap;

        System.out.println(directions);
        System.out.println(options);

        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for(int i = 0; i < directions.legs.length; i++) {
            if(i > 0 && i < directions.legs.length) {
                LatLng legStartLoc = new LatLng(directions.legs[i].startLat, directions.legs[i].startLng);
                LatLng legEndLoc = new LatLng(directions.legs[i].endLat, directions.legs[i].endLng);
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pushpin))
                        .title(directions.legs[i].endAddress)
                        .position(legStartLoc));
            }

            for(int j = 0; j < directions.legs[i].steps.length; j++) {
                PolylineOptions polylineOptions = new PolylineOptions().
                        geodesic(true).
                        color(Color.BLUE).
                        width(10);

                List<LatLng> polyline = new ArrayList<>();

                polyline = decodePolyLine(directions.legs[i].steps[j].polylinePoints);

                for (int l = 0; l < polyline.size(); l++) {
                    polylineOptions.add(polyline.get(l));
                }
                mMap.addPolyline(polylineOptions);
            }
        }

        LatLng startLoc = new LatLng(directions.legs[0].startLat, directions.legs[0].startLng);
        LatLng endLoc = new LatLng(directions.legs[directions.legs.length - 1].endLat, directions.legs[directions.legs.length - 1].endLng);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLoc, 10));

        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                .title(directions.legs[0].startAddress)
                .position(startLoc)));
        destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                .title(directions.legs[directions.legs.length - 1].endAddress)
                .position(endLoc)));



        /*Draws entire line? I'm not sure if this is different than if I break this down like above.*/
//                PolylineOptions polylineOptions = new PolylineOptions().
//                        geodesic(true).
//                        color(Color.BLUE).
//                        width(10);
//
//            List<LatLng> polyline = new ArrayList<>();
//
//            polyline = decodePolyLine(directions.polyline);
//
//            for (int i = 0; i < polyline.size(); i++) {
//                polylineOptions.add(polyline.get(i));
//            }
//
//            polylinePaths.add(mMap.addPolyline(polylineOptions));

    }

    private static List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }

    private String encodec(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, e);
            //noinspection deprecation
            return URLEncoder.encode(str);
        }
    }

    public void navigateClicked(View view) {
        String[] waypoints = new String[directions.legs.length + 1];
        float minLat = directions.legs[0].startLat,
              maxLat = minLat;
        float minLng = directions.legs[0].startLng,
              maxLng = minLng;
        for (int q = 0; q < directions.legs.length; q++) {
            NavigationLeg leg = directions.legs[q];
            waypoints[q] = encodec(leg.startAddress);
            if (leg.endLat < minLat) minLat = leg.endLat;
            if (leg.endLat > maxLat) maxLat = leg.endLat;
            if (leg.endLng < minLng) minLng = leg.endLng;
            if (leg.endLng > maxLng) maxLng = leg.endLng;
        }
        waypoints[waypoints.length - 1] = encodec(directions.legs[directions.legs.length - 1].endAddress);

        StringBuilder buff = new StringBuilder("http://www.google.com/maps/dir");
        for (String waypoint : waypoints) {
            buff.append("/");
            buff.append(waypoint);
        }
        buff.append("/@");
        buff.append(minLat + (maxLat - minLat) / 2);
        buff.append(",");
        buff.append(minLng + (maxLng - minLng) / 2);
        buff.append(",");
        buff.append("7z");
        String uriStr = buff.toString();

        Log.i(TAG, uriStr);

        Uri uri = Uri.parse(uriStr);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }


}
