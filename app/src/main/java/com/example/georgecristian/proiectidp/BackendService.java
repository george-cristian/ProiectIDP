package com.example.georgecristian.proiectidp;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class BackendService extends IntentService {

    public static final String MY_ACTION = "MY_ACTION";
    public static final String NOTIFICATION = "com.example.georgecristian.proiectidp";

    public static final int OPERATION_UPDATE_LOCATION = 1;
    public static final int OPERATION_INSERT_USER = 2;

    public static final String PARAM_OPERATION_TYPE = "OPERATION_TYPE";

    public static final String PARAM_USER_ID = "USER_ID";
    public static final String PARAM_USER_FIRST_NAME = "USER_FIRST_NAME";
    public static final String PARAM_USER_LAST_NAME = "USER_LAST_NAME";

    public static final String PARAM_UPDATE_LONG = "UPDATE_LONG";
    public static final String PARAM_UPDATE_LAT= "UPDATE_LAT";
    public static final String PARAM_UPDATE_ALT = "UPDATE_ALT";

    public static final String PARAM_FRIEND_LIST = "FRIEND_LIST";

    public static final String TAG_LONG = "longitude";
    public static final String TAG_LAT = "latitude";
    public static final String TAG_ALT = "altitude";
    public static final String TAG_USER_ID = "user_id";
    public static final String TAG_USER_FIRST_NAME = "user_first_name";
    public static final String TAG_USER_LAST_NAME = "user_last_name";
    public static final String TAG_USERS_ARRAY = "friends_list";
    public static final String TAG_ELAPSED_MINUTES = "elapsed";
    public static final String TAG_DISTANCE = "distance";

    private static final String UPDATE_LOCATION_SERVLET_URL = "http://10.0.2.2:8080/ServerIDP/UpdateLocationServlet";
    private static final String INSERT_USER_SERVLET_URL = "http://10.0.2.2:8080/ServerIDP/InsertUserServlet";

    private static final String HARDCODED_JSON = "{\"friends_list\":[{\"elapsed\":220,\"user_first_name\":\"Andrei\",\"distance\":0,\"user_last_name\":\"Tudor\"},{\"elapsed\":197,\"user_first_name\":\"Silviu\",\"distance\":9157.93807746121,\"user_last_name\":\"Popescu\"},{\"elapsed\":196,\"user_first_name\":\"Ana\",\"distance\":12142.38259941096,\"user_last_name\":\"Badea\"},{\"elapsed\":196,\"user_first_name\":\"Alexandra\",\"distance\":12657.96736396313,\"user_last_name\":\"Alexandru\"}]}";

    public BackendService() {
        super("BackendService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {

        int operationType = workIntent.getIntExtra(PARAM_OPERATION_TYPE, OPERATION_UPDATE_LOCATION);

        String userID;

        switch (operationType) {
            case OPERATION_UPDATE_LOCATION:
                userID = workIntent.getStringExtra(PARAM_USER_ID);
                double newLongitude = workIntent.getDoubleExtra(PARAM_UPDATE_LONG, 0);
                double newLatitude = workIntent.getDoubleExtra(PARAM_UPDATE_LAT, 0);
                double newAltitude = workIntent.getDoubleExtra(PARAM_UPDATE_ALT, 0);

                JSONObject updateLocationJSON = new JSONObject();

                try {
                    updateLocationJSON.put(TAG_USER_ID, userID);
                    updateLocationJSON.put(TAG_LONG, newLongitude);
                    updateLocationJSON.put(TAG_LAT, newLatitude);
                    updateLocationJSON.put(TAG_ALT, newAltitude);
                } catch (JSONException e) {
                    Log.e(BackendService.class.getCanonicalName(), "Something went wrong when creating JSON");
                }

                try {
                    URL servletURL = new URL(UPDATE_LOCATION_SERVLET_URL);
                    URLConnection connection = servletURL.openConnection();

                    connection.setDoOutput(true);
                    OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                    out.write(updateLocationJSON.toString());
                    out.close();

                    Log.d("JSON Sent", updateLocationJSON.toString());

                    String returnString;
                    String receivedString = "";
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    while ((returnString = in.readLine()) != null) {
                        receivedString.concat(returnString);
                    }
                    in.close();
                    receivedString = HARDCODED_JSON;
                    Log.d("Received JSON", "Serverul mi-a trimis :" + receivedString);

                    Intent sendBackIntent = new Intent(NOTIFICATION);
                    sendBackIntent.putExtra(PARAM_FRIEND_LIST, receivedString);

                    sendBroadcast(sendBackIntent);

                } catch (Exception e) {
                    Log.d(BackendService.class.getCanonicalName(), "Exception when sending to servlet");
                }

                break;
            case OPERATION_INSERT_USER:
                userID = workIntent.getStringExtra(PARAM_USER_ID);
                String firstName = workIntent.getStringExtra(PARAM_USER_FIRST_NAME);
                String lastName = workIntent.getStringExtra(PARAM_USER_LAST_NAME);

                JSONObject insertUserJSON = new JSONObject();

                try {
                    insertUserJSON.put(TAG_USER_ID, userID);
                    insertUserJSON.put(TAG_USER_FIRST_NAME, firstName);
                    insertUserJSON.put(TAG_USER_LAST_NAME, lastName);
                } catch (JSONException e) {
                    Log.e(BackendService.class.getCanonicalName(), "Something went wrong when creating JSON");
                }

                try {
                    URL servletURL = new URL(INSERT_USER_SERVLET_URL);
                    URLConnection connection = servletURL.openConnection();

                    connection.setDoOutput(true);
                    OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                    out.write(insertUserJSON.toString());
                    out.close();

                    Log.d("JSON Sent", insertUserJSON.toString());

                    String returnString;
                    String receivedString = "";
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    while ((returnString = in.readLine()) != null) {
                        receivedString.concat(returnString);
                    }
                    in.close();

                    Log.d("Received JSON", "Herpderp");


                } catch (Exception e) {
                    Log.d(BackendService.class.getCanonicalName(), "Exception when sending to servlet");
                }

                break;
            default:
                break;
        }



    }

}
