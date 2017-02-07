package tooearly.com.gasapp.models;

public class GasStation {
    public GasStation(String id, String stationName, String address, double lat, double lng, double price, String priceDate) {
        this.id = id;
        this.stationName = stationName;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.price = price;
        this.priceDate = priceDate;
    }

    public final String id, stationName, address, priceDate;
    public final double lat, lng, price;
}
