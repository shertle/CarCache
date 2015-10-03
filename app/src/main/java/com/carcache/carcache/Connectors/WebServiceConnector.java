package com.carcache.carcache.Connectors;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.carcache.carcache.Models.CCuser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zizhouzhai on 10/3/15.
 */
public class WebServiceConnector {

    private static String webServiceURL = "http://52.24.88.114/sendCarMoveLocation.php";
    private static String TAG = "WebServiceConnector";

    /**
     * Send a location object to the web service.
     * @param locationToSend - location to send
     * @return true if post request successful, false else
     */
    public void sendLocation(CCuser locationToSend){

        SendLocationTask sendLocationTask = new SendLocationTask();
        sendLocationTask.execute(locationToSend);

    }

    public class SendLocationTask extends AsyncTask<CCuser,Void,Void>{

        @Override
        protected Void doInBackground(CCuser... params) {
            URL url = null;

            try{

                // try establish connection to the web service
                url = new URL(webServiceURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput (true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.connect();

                // create JSON object.
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("timestamp",params[0].getDate().getTime()/1000); //getTime() returns milliseconds since epoch
                Location location = params[0].getLocation();

                jsonParam.put("latitude",location.getLatitude());
                jsonParam.put("longitude", location.getLongitude());

                //convert JSON to string and get bytes encoded in UTF-8
                String str = jsonParam.toString();
                byte[] data=str.getBytes("UTF-8");

                DataOutputStream printout = new DataOutputStream(connection.getOutputStream ());
                printout.write(data);
                printout.flush();
                printout.close();

                // read response from server.
                int HttpResult = connection.getResponseCode();
                if(HttpResult ==HttpURLConnection.HTTP_OK) {

                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            connection.getInputStream(),"utf-8"));
                    String line = null;
                    StringBuilder sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    // print out response.
                    Log.e(TAG,""+sb.toString());

                    JSONObject serverResponseJSON = new JSONObject(sb.toString());
                    int responseCode = serverResponseJSON.getInt("resultCode");
                    Log.e(TAG,"Server response code was: " + responseCode);
                }
                else{
                    Log.e(TAG,"HttpResult was not OK");
                }


            }catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }

}
