package android.clase.obligatorio1.utils;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * created by Alfredo El Ters and Mathias Cabano on 09/05/15.
 * This class contains all auxiliary methods used by WebServiceInterface
 */
public class WebServiceUtils {
    private static final String AUTHENTICATION_TOKEN = "1d0d83c9a5f24477b9b3fa460ae7410e";

    public static JSONObject getJSONObjectFromUrl(String urlString) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Auth-Token", AUTHENTICATION_TOKEN);
            connection.connect();

            Scanner scanner = new Scanner(connection.getInputStream(), "UTF-8");
            StringBuilder sb = new StringBuilder();

            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
            }

            return new JSONObject(sb.toString());

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) connection.disconnect();
        }
        return null;
    }

    public static JSONArray getJSONArrayFromUrl(String urlString) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Auth-Token", AUTHENTICATION_TOKEN);
            connection.connect();

            Scanner scanner = new Scanner(connection.getInputStream(), "UTF-8");
            StringBuilder sb = new StringBuilder();

            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
            }

            return new JSONArray(sb.toString());

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) connection.disconnect();
        }
        return null;
    }


    public static JSONObject getJSONObjectFromUrlAlt(String url) {
        // Making HTTP request
        String json = getJSONStringResponse(url);
        if (json == null)
            return null;
        JSONObject jObject = null;
        // try parse the string to a JSON object
        try {
            jObject = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        // return JSON String
        return jObject;
    }

    public static String getJSONStringResponse(String url) {
        // Making HTTP request
        InputStream is = null;
        String json = "";
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(url);
            httpget.setHeader("X-Auth-Token", AUTHENTICATION_TOKEN);
            HttpResponse httpResponse = httpClient.execute(httpget);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
            return null;
        }
        return json;
    }
}
