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

    /**
     * Method to execute an http GET to a URL and return the result as a JSONObject
     * @param urlString
     * @return response JSONObject
     */
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

    /**
     * Method to execute an http GET to a URL and return the result as a JSONArray
     * @param urlString
     * @return response JSONArray
     */
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

    /**
     * Method to execute an http GET to a URL and return the result as a String
     * @param urlString
     * @return response String
     */
    public static String getJSONStringFromUrl(String urlString){
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

            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) connection.disconnect();
        }
        return null;
    }
}
