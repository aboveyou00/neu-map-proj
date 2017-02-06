package tooearly.com.gasapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PlanRouteActivity extends AppCompatActivity {
    public static final String TAG = "PlanRouteActivity";
    public static final String PLANNED_TRIP_EXTRA = "tooearly.com.gasapp.planned_trip_extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_route);

        Intent intent = getIntent();
        String origin = intent.getStringExtra(OriginDestinationActivity.ORIGIN_EXTRA);
        String destination = intent.getStringExtra(OriginDestinationActivity.DESTINATION_EXTRA);
        new PlanRouteTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, origin, destination);
    }

    private class PlanRouteTask extends AsyncTask<String, Void, PlannedTrip> {
        @Override
        protected PlannedTrip doInBackground(String... params) {
            if (params == null || params.length != 2) throw new IllegalArgumentException("Can't plan route without both origin and destination");
            String origin = params[0];
            String destination = params[1];

            String directions = DirectionService.getDirections(PlanRouteActivity.this, origin, destination);

            return new PlannedTrip(origin, destination);
        }

        @Override
        protected void onPostExecute(PlannedTrip trip) {
            super.onPostExecute(trip);

            Intent intent = new Intent(PlanRouteActivity.this, NavigationActivity.class);
            intent.putExtra(PLANNED_TRIP_EXTRA, trip);
            startActivity(intent);
        }
    }
}
