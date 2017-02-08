package tooearly.com.gasapp;

import java.io.Serializable;

public class TripOptions implements Serializable {
    public TripOptions(float tankSizeGallons, float mpg, float gasTankPercentage, FuelType fuelType, String stationType) {
        this.tankSizeGallons = tankSizeGallons;
        this.mpg = mpg;
        this.gasTankPercentage = gasTankPercentage;
        this.fuelType = fuelType;
        this.stationType = stationType;
    }

    float tankSizeGallons, mpg, gasTankPercentage;
    FuelType fuelType;
    String stationType;
}
