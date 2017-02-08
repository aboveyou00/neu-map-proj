package tooearly.com.gasapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
        new PlanRouteTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, origin, destination);
    }

    private class PlanRouteTask extends AsyncTask<String, Void, NavigationDirections> {
        @Override
        protected NavigationDirections doInBackground(String... params) {
            if (params == null || params.length != 2) throw new IllegalArgumentException("Can't plan route without both origin and destination");
            String origin = params[0];
            String destination = params[1];

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
            startActivity(intent);
        }
    }
}
