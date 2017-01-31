package tooearly.com.gasapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class OriginDestinationActivity extends AppCompatActivity implements LocationListener {
//    public static final String TAG = "OriginDestinationActivity";
    public static final int LOCATION_PERMISSIONS = 0xBEA0;
    public static final String ORIGIN_EXTRA = "tooearly.com.gasapp.origin_extra";
    public static final String DESTINATION_EXTRA = "tooearly.com.gasapp.destination_extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_origin_destination);

        check_use_current = (AppCompatCheckBox)findViewById(R.id.check_use_current);
        txt_origin = (EditText)findViewById(R.id.txt_origin);
        txt_destination = (EditText)findViewById(R.id.txt_destination);

        check_use_current.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                usingCurrentLocation = isChecked;
                txt_origin.setEnabled(!usingCurrentLocation);
                getCurrentLocation();
            }
        });
        check_use_current.setChecked(true);
    }

    AppCompatCheckBox check_use_current;
    EditText txt_origin, txt_destination;

    public void submitClicked(View view) {
        String origin = txt_origin.getText().toString().trim();
        String destination = txt_destination.getText().toString().trim();
        if (origin.isEmpty() || destination.isEmpty()) {
            Toast.makeText(this, R.string.err_provide_origin_destination, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, PlanRouteActivity.class);
        intent.putExtra(ORIGIN_EXTRA, origin);
        intent.putExtra(DESTINATION_EXTRA, destination);
        startActivity(intent);
    }

    boolean usingCurrentLocation = true;

    private List<Callback<Boolean>> locationPermissionCallbacks = new ArrayList<>();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSIONS) {
            if (locationPermissionCallbacks.size() == 0) return;
            Callback<Boolean> cb = locationPermissionCallbacks.get(0);
            locationPermissionCallbacks.remove(0);
            for (int result : grantResults) {
                if (result != PERMISSION_GRANTED) {
                    cb.handle(false);
                    return;
                }
            }
            cb.handle(true);
        }
    }

    private void doWithLocationPermission(Callback<Boolean> cb) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PERMISSION_GRANTED) {
            locationPermissionCallbacks.add(cb);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, LOCATION_PERMISSIONS);
        }
        else {
            cb.handle(true);
        }
    }

    private void getCurrentLocation() {
        if (!usingCurrentLocation) return;
        doWithLocationPermission(new Callback<Boolean>() {
            @Override
            public void handle(Boolean val) {
                if (!val) {
                    Toast.makeText(OriginDestinationActivity.this, R.string.err_requires_location_permissions, Toast.LENGTH_SHORT).show();
                    return;
                }
                initializeLocationManager();
            }
        });
    }
    private void initializeLocationManager() {
        try {
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location loc = null;

            final int MIN_TIME_BW_UPDATES = 1000 * 50,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES = 500;
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, OriginDestinationActivity.this);
                loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            else if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, OriginDestinationActivity.this);
                loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            else if (lm.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, OriginDestinationActivity.this);
                loc = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }

            OriginDestinationActivity.this.onLocationChanged(loc);
        }
        catch (SecurityException e) {
            Toast.makeText(OriginDestinationActivity.this, R.string.err_requires_location_permissions, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location loc) {
        if (loc != null) txt_origin.setText(loc.getLatitude() + ", " + loc.getLongitude());
        else {
            Toast.makeText(OriginDestinationActivity.this, R.string.err_failed_to_get_location, Toast.LENGTH_SHORT).show();
            check_use_current.setChecked(false);
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
    }
}
