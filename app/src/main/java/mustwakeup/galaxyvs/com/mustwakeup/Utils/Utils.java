package mustwakeup.galaxyvs.com.mustwakeup.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by viral.thakkar on 04-03-2015.
 */
public class Utils {

    static String resp = null;
    private static final String SHARED_PREF = "MUSTWAKEUP";

    public static void putValue(Context context, String key, String value) {
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getValue(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        return pref.getString(key, null);
    }

    public String sendRequest() {
        String resp = null;


        return resp;
    }


    public static String sendRequest(String url, String jsonParameter) {
        String response = null;
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            // add request header
            con.setRequestMethod("GET");
            // con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            // Send post request
            //con.setDoOutput(true);

            if (jsonParameter != null && !jsonParameter.isEmpty()) {
                DataOutputStream wr = new DataOutputStream(
                        con.getOutputStream());
                wr.writeBytes(jsonParameter);
                wr.flush();
                wr.close();
            }

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuilder responseBuffer = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    responseBuffer.append(inputLine);
                }
                in.close();
                // print result
                response = responseBuffer.toString();
            }
        } catch (MalformedURLException e) {
            Log.e("Errorrr","MalformedURLException",e);
        } catch (IOException e) {
            Log.e("Errorrr","IOException",e);
        }
        return response;
    }

}
