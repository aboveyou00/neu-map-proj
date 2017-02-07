package tooearly.com.gasapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import tooearly.com.gasapp.R;
import tooearly.com.gasapp.models.FuelType;
import tooearly.com.gasapp.models.TripOptions;

public class TripOptionsActivity extends AppCompatActivity {
    public static final String TRIP_OPTIONS_EXTRA = "tooearly.com.gasapp.trip_options_extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_options);

        userGasTankPercentage = (SeekBar)findViewById(R.id.userGasTankPercentage);
        lbl_userGasTankPercentage = (TextView)findViewById(R.id.valueTxt);

        userGasTankPercentage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                lbl_userGasTankPercentage.setText(String.valueOf(progress) + " %");
            }
            @Override
            public void onStartTrackingTouch(SeekBar sb) { }
            @Override
            public void onStopTrackingTouch(SeekBar sb) { }
        });

        this.userTankSize = (EditText)findViewById(R.id.userTankSize);
        this.userMpg = (EditText)findViewById(R.id.userMPG);
        this.radioFuelType = (RadioGroup)findViewById(R.id.radioFuelType);
        this.userGasStation = (Spinner)findViewById(R.id.userGasStation);
    }

    private SeekBar userGasTankPercentage;
    private TextView lbl_userGasTankPercentage;

    private EditText userTankSize, userMpg;

    private RadioGroup radioFuelType;

    private Spinner userGasStation;

    public void submitClicked(View view) {
        float tankSize = Float.parseFloat(userTankSize.getText().toString());
        float mpg = Float.parseFloat(userMpg.getText().toString());
        float gasTankPercentage = userGasTankPercentage.getProgress() / 100.f;

        FuelType fuelType;
        switch (radioFuelType.getCheckedRadioButtonId()) {
        case R.id.radioUnleaded:
            fuelType = FuelType.Regular;
            break;
        case R.id.radioDiesel:
            fuelType = FuelType.Diesel;
            break;
        default:
            throw new IllegalStateException();
        }

        String gasStation = (String)userGasStation.getSelectedItem();

        TripOptions options = new TripOptions(tankSize, mpg, gasTankPercentage, fuelType, gasStation);

        Intent navigationIntent = new Intent(this, OriginDestinationActivity.class);
        navigationIntent.putExtra(TRIP_OPTIONS_EXTRA, options);
        startActivity(navigationIntent);
    }
}
