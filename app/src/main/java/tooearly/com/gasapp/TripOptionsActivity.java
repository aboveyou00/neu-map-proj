package tooearly.com.gasapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class TripOptionsActivity extends AppCompatActivity {
    private TextView titleText;
    private SeekBar sb;
    private TextView valuetxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_options);

        sb= (SeekBar) findViewById(R.id.userGasTankPercentage);
        valuetxt = (TextView) findViewById(R.id.valueTxt);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                valuetxt.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar sb) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar sb) {

            }
        });
    }
}
