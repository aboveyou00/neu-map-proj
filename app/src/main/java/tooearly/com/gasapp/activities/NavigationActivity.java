//package tooearly.com.gasapp;
//
//import android.content.Intent;
//import android.graphics.Color;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.maps.model.PolylineOptions;
//
//public class NavigationActivity extends AppCompatActivity implements OnMapReadyCallback {
//    GoogleMap mMap = null;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_navigation);
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // Add a marker in Sydney, Australia, and move the camera.
//        LatLng slc = new LatLng(40, -111);
//        mMap.addMarker(new MarkerOptions().position(slc).title("Marker in slc"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(slc));
//    }
//
//    public void navigateClicked(View view) {
//        LatLng slc = new LatLng(40, -111);
//        mMap.addPolyline(new PolylineOptions().add(
//                    slc,
//                    new LatLng(50, -20)
//                )
//                .width(10)
//                .color(Color.RED)
//        );
//
////        double lat = 40.7660926;
////
////        double lng = -111.89057359999998;
////
////                String format = "geo:0,0?q=" + lat + "," + lng + "( Location title)";
////
////        Uri uri = Uri.parse(format);
////
////
////        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
////        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////        startActivity(intent);
//
//    }
//}


package tooearly.com.gasapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import tooearly.com.gasapp.R;
import tooearly.com.gasapp.models.NavigationDirections;
import tooearly.com.gasapp.models.NavigationLeg;
import tooearly.com.gasapp.models.TripOptions;

public class NavigationActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String TAG = "NavigationActivity";

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

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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
