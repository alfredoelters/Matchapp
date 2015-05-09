package android.clase.obligatorio1.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by alfredo on 09/05/15.
 * This class contains all auxiliary methods used by WebServiceInterface
 */
public class WebServiceUtils {
    public static JSONObject getJSONObjectFormUrl(String urlString){
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            Scanner scanner = new Scanner(connection.getInputStream(), "UTF-8");
            StringBuilder sb = new StringBuilder();

            while (scanner.hasNextLine()){
                sb.append(scanner.nextLine());
            }

           return new JSONObject(sb.toString());

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }finally {
            if(connection != null)  connection.disconnect();
        }
        return null;
    }
}
