package tooearly.com.gasapp;

import java.util.Locale;

public class HttpResponse {
    public int responseCode;
    public String responseMessage;
    public String data;

    public HttpResponse(int responseCode, String responseMessage, String data) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.data = data;
    }

    public String asString(){
        return String.format(Locale.US, "%d - %s - %s", responseCode, responseMessage, data);
    }
}
