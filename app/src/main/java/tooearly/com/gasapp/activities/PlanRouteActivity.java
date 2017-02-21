package tooearly.com.gasapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tooearly.com.gasapp.R;
import tooearly.com.gasapp.models.GasStation;
import tooearly.com.gasapp.models.NavigationDirections;
import tooearly.com.gasapp.models.NavigationLeg;
import tooearly.com.gasapp.models.NavigationStep;
import tooearly.com.gasapp.models.TripOptions;
import tooearly.com.gasapp.services.DirectionService;
import tooearly.com.gasapp.services.GasPriceService;

public class PlanRouteActivity extends AppCompatActivity {
    public static final String TAG = "PlanRouteActivity";
    public static final String DIRECTIONS_EXTRA = "tooearly.com.gasapp.directions_extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_route);

        Intent intent = getIntent();
        String origin = intent.getStringExtra(OriginDestinationActivity.ORIGIN_EXTRA);
        String destination = intent.getStringExtra(OriginDestinationActivity.DESTINATION_EXTRA);
        options = (TripOptions)getIntent().getSerializableExtra(TripOptionsActivity.TRIP_OPTIONS_EXTRA);

        new PlanRouteTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, origin, destination);
    }

    TripOptions options;

    private class PlanRouteTask extends AsyncTask<String, Void, NavigationDirections> {
        @Override
        protected NavigationDirections doInBackground(String... params) {
            if (params == null || params.length != 2) throw new IllegalArgumentException("Can't plan route without both origin and destination");
            String origin = params[0];
            String destination = params[1];
            TripOptions options = PlanRouteActivity.this.options;

            NavigationDirections directions = DirectionService.getDirections(PlanRouteActivity.this, origin, destination);
            if (directions == null) return null;

            float milesPerTank = options.tankSizeGallons * options.mpg;
            float milesLeft = options.gasTankPercentage * milesPerTank;
            List<GasStation> stations = new ArrayList<>();
            for (int q = 0; q < directions.legs.length; q++) {
                NavigationLeg leg = directions.legs[q];
                for (int w = 0; w < leg.steps.length; w++) {
                    NavigationStep step = leg.steps[w];
                    if (step.distanceMiles > milesLeft) {
                        float distanceDelta = milesLeft / step.distanceMiles;
                        float lat = mix(step.startLat, step.endLat, distanceDelta), lng = mix(step.startLng, step.endLng, distanceDelta);
                        GasStation station = findBestGasStation(lat, lng);
                        if (station == null)
                            return null;
                        stations.add(station);
                        milesLeft += milesPerTank;
                        w--;
                    }
                    else milesLeft -= step.distanceMiles;
                }
            }

            String[] waypoints = new String[stations.size()];
            for (int q = 0; q < stations.size(); q++) {
                waypoints[q] = stations.get(q).address;
            }

            directions = DirectionService.getDirections(PlanRouteActivity.this, origin, destination, waypoints);
            return directions;
        }

        private float mix(float one, float two, float delta) {
            return (one * (1 - delta)) + (two * delta);
        }
        private GasStation findBestGasStation(float lat, float lng) {
            TripOptions options = PlanRouteActivity.this.options;

            GasStation[] stations = GasPriceService.getStations(PlanRouteActivity.this, lat, lng, 50, options.fuelType, true);
            if (stations == null || stations.length == 0) return null;
            if (options.stationType.equals("Any")) return stations[0];

            String type = options.stationType;
            for (GasStation station : stations) {
                if (station.stationName.equals(type)) return station;
            }
            return stations[0]; //Failed to find a station within 10 miles that matches the required type; just return the closest
        }

        @Override
        protected void onPostExecute(NavigationDirections directions) {
            super.onPostExecute(directions);

            if (directions == null) {
                Toast.makeText(PlanRouteActivity.this, R.string.err_failed_to_plan_route, Toast.LENGTH_LONG);
                return;
            }

            Intent intent = new Intent(PlanRouteActivity.this, NavigationActivity.class);
            intent.putExtra(DIRECTIONS_EXTRA, directions);
            intent.putExtra(TripOptionsActivity.TRIP_OPTIONS_EXTRA, PlanRouteActivity.this.options);
            startActivity(intent);
        }
    }
}
