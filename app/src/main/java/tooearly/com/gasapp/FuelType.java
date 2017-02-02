package tooearly.com.gasapp;

public enum FuelType {
    Regular("reg"),
    Middle("mid"),
    Premium("pre"),
    Diesel("diesel");

    public final String value;

    FuelType(String val) {
        value = val;
    }
}
