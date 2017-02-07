package tooearly.com.gasapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import tooearly.com.gasapp.R;
import tooearly.com.gasapp.models.NavigationDirections;
import tooearly.com.gasapp.models.TripOptions;
import tooearly.com.gasapp.services.DirectionService;

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

            //TODO: find gas stations along route

            //TODO: navigate form origin to destination with the gas stations as waypoints

            return directions;
        }

        @Override
        protected void onPostExecute(NavigationDirections directions) {
            super.onPostExecute(directions);

            Intent intent = new Intent(PlanRouteActivity.this, NavigationActivity.class);
            intent.putExtra(DIRECTIONS_EXTRA, directions);
            intent.putExtra(TripOptionsActivity.TRIP_OPTIONS_EXTRA, PlanRouteActivity.this.options);
            startActivity(intent);
        }
    }
}
