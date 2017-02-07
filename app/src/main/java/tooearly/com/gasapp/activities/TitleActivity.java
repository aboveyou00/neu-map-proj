package tooearly.com.gasapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import tooearly.com.gasapp.R;

public class TitleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);
    }

    public void beginPlanningClicked(View view) {
        Intent navigationIntent = new Intent(this, TripOptionsActivity.class);
        startActivity(navigationIntent);
    }
}
