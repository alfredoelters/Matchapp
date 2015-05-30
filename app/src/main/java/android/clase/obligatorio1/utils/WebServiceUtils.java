package android.clase.obligatorio1.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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


    public static Bitmap downloadBitmapFromUrl(String url, Context context, String auxiliaryFileName){
        URL fileUrl;
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            fileUrl = new URL(url);
            connection = (HttpURLConnection) fileUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK) { // HTTP OK 200 result code

                // get total size in bytes that we will read from the response
                int totalSize = connection.getContentLength();

                // get input stream to read bytes from
                InputStream stream = connection.getInputStream();
                // we will save the image on storage, as a jpg file
                String filePath = context.getExternalFilesDir(null) + File.separator
                        + auxiliaryFileName +".jpg";
                OutputStream outputStream = new FileOutputStream(filePath);

                byte[] bytes = new byte[1024];
                int currentSize;
                while ((currentSize = stream.read(bytes)) != -1) {
                    // read bytes until we have bytes to read from the stream
                    outputStream.write(bytes, 0, currentSize); // write bytes read to file
                }

                bitmap = BitmapFactory.decodeFile(filePath); // decode image from downloaded file

                // close and flush streams
                stream.close();
                outputStream.flush();
                outputStream.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // always close connection
            if(connection != null) connection.disconnect();
        }

        return bitmap;
    }
}
