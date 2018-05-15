package com.example.georgecristian.proiectidp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 9000;

    private TextView longitudeTextView;
    private TextView latitudeTextView;
    private TextView altitudeTextView;

    private String userID;

    private FriendsAdapter mAdapter;
    private RecyclerView mFriendsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        longitudeTextView = findViewById(R.id.tv_longitude);
        latitudeTextView = findViewById(R.id.tv_latitude);
        altitudeTextView = findViewById(R.id.tv_altitude);

        mFriendsList = (RecyclerView) findViewById(R.id.rv_friends);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mFriendsList.setLayoutManager(layoutManager);
        mAdapter = new FriendsAdapter(new ArrayList<String>());
        mFriendsList.setAdapter(mAdapter);

        userID = "960079534157785";

        IntentFilter intentFilter = new IntentFilter(BackendService.NOTIFICATION);
        registerReceiver(broadcastReceiver, intentFilter);

        FacebookSdk.sdkInitialize(getApplicationContext());

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = getLocationListener();

        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Handle no permission
            Log.d(TAG, "No permission for GPS");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, locationListener);

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String receivedString = intent.getStringExtra(BackendService.PARAM_FRIEND_LIST);

            Log.d(TAG, "+++++++++++++++Am primit doamne la MainActivity: " + receivedString);
            updateUIInformation(receivedString);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(BackendService.NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void updateUIInformation(String receivedString) {

        try {
            JSONObject receivedJSON = new JSONObject(receivedString);

            JSONArray friendsArray = receivedJSON.getJSONArray(BackendService.TAG_USERS_ARRAY);

            List<String> friendsStringList = new ArrayList<String>();

            for (int i = 0 ; i < friendsArray.length(); ++i) {
                JSONObject friendJSON = friendsArray.getJSONObject(i);

                StringBuilder friendTextBuilder = new StringBuilder();

                friendTextBuilder.append(friendJSON.getString(BackendService.TAG_USER_FIRST_NAME));
                friendTextBuilder.append(" ");
                friendTextBuilder.append(friendJSON.getString(BackendService.TAG_USER_LAST_NAME));
                friendTextBuilder.append("\n\nTe afli la distanta de: ");
                friendTextBuilder.append(friendJSON.getString(BackendService.TAG_DISTANCE));
                friendTextBuilder.append("km. Ultimul update s-a inregistrat in urma cu ");
                friendTextBuilder.append(friendJSON.getString(BackendService.TAG_ELAPSED_MINUTES));
                friendTextBuilder.append(" minute.");

                friendsStringList.add(friendTextBuilder.toString());
            }

            mFriendsList.setAdapter(new FriendsAdapter(friendsStringList));

        } catch (JSONException e) {

        }
    }

    private LocationListener getLocationListener() {
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                longitudeTextView.setText(String.valueOf(location.getLongitude()));
                latitudeTextView.setText(String.valueOf(location.getLatitude()));
                altitudeTextView.setText(String.valueOf(location.getAltitude()));
                Toast.makeText(MainActivity.this, R.string.location_changed, Toast.LENGTH_LONG).show();

                //Trimit noua locatie la service pentru a fi trimisa la server
                Intent updateLocationIntent = new Intent(MainActivity.this, BackendService.class);

                updateLocationIntent.putExtra(BackendService.PARAM_USER_ID, userID);
                updateLocationIntent.putExtra(BackendService.PARAM_OPERATION_TYPE, BackendService.OPERATION_UPDATE_LOCATION);
                updateLocationIntent.putExtra(BackendService.PARAM_UPDATE_LONG, location.getLongitude());
                updateLocationIntent.putExtra(BackendService.PARAM_UPDATE_LAT, location.getLatitude());
                updateLocationIntent.putExtra(BackendService.PARAM_UPDATE_ALT, location.getAltitude());
                startService(updateLocationIntent);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        return locationListener;
    }
}
