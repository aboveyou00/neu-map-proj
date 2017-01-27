package tooearly.com.gasapp;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// Performs a synchronous http request
@SuppressWarnings("unused")
public class HttpRequest {
    public static final String TAG = "HttpRequest";

    public static final int UNAUTHORIZED = 0;
    public static final int AUTHORIZED = 1;
    public static final int AUTHORIZED_IGNORE_EXPIRATION = 2;

    public static HttpResponse Request(String method, String urlString, String content) {
        HttpURLConnection client = null;
        try {
            URL url = new URL(urlString);
            client = (HttpURLConnection) url.openConnection();
            client.setConnectTimeout(4000);
            client.setRequestMethod(method);
//            client.setRequestProperty("Content-Type", "application/json");
            client.setRequestProperty("Accept", "text/html");
//            client.setRequestProperty("Connection", "keep-alive");

            Log.d(TAG, "HTTP " + method + " to " + urlString);
            Log.d(TAG, content);

            client.setDoInput(true);

            if (!content.isEmpty()) {
                client.setDoOutput(true);

                byte[] contentBytes = content.getBytes();
                client.setFixedLengthStreamingMode(contentBytes.length);

                BufferedOutputStream out = new BufferedOutputStream(client.getOutputStream());
                out.write(contentBytes);
                out.flush();
                out.close();
            }

            int responseCode = client.getResponseCode();
            String response = client.getResponseMessage();
            String result;

            try {
                BufferedReader in;
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                StringBuilder sb = new StringBuilder();

                char[] buffer = new char[1024];
                int count;
                while ((count = in.read(buffer, 0, 1024)) != -1) {
                    sb.append(buffer, 0, count);
                }

                result = sb.toString();
            }
            catch (FileNotFoundException e) {
                result = null;
            }

            Log.i(TAG, responseCode + "|" + response + "|" + result);

            return new HttpResponse(responseCode, response, result);
        }
        catch (IOException e) {
            return null;
        }
        catch (Exception e) {
            Log.e(TAG, e + Log.getStackTraceString(e));
            throw new RuntimeException(e + Log.getStackTraceString(e));
        }
        finally {
            if (client != null) {
                client.disconnect();
            }
        }
    }

    public static HttpResponse Get(String urlString, String content) {
        return Request("GET", urlString, content);
    }
    public static HttpResponse Get(String urlString) {
        return Get(urlString, "");
    }

    public static HttpResponse Post(String urlString, String content) {
        return Request("POST", urlString, content);
    }
    public static HttpResponse Post(String urlString) {
        return Post(urlString, "");
    }

    public static HttpResponse Put(String urlString, String content) {
        return Request("PUT", urlString, content);
    }
    public static HttpResponse Put(String urlString) {
        return Post(urlString, "");
    }

    public static HttpResponse Delete(String urlString, String content) {
        return Request("DELETE", urlString, content);
    }
    public static HttpResponse Delete(String urlString) {
        return Delete(urlString, "");
    }
}
