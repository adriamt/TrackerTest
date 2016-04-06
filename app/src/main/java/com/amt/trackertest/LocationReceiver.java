package com.amt.trackertest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.BatteryManager;
import android.util.Log;

import com.amt.trackertest.httpTask.HttpHandler;
import com.google.android.gms.location.LocationResult;

public class LocationReceiver extends BroadcastReceiver {

    private String session_id = "";
    float battery = 0;

    private LocationResult mLocationResult;
    private Location mLastLocation;

    public static final String PREFS_NAME = "GPS_PREFS";
    private SharedPreferences sharedPref;

    private Context mContext;

    public LocationReceiver() {
    }

    // WEB: https://developers.google.com/android/reference/com/google/android/gms/location/LocationResult.html#field-summary

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        // Need to check and grab the Intent's extras like so
        sharedPref = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        session_id = sharedPref.getString("session_id", "NULL");
        Log.i("LocationReceiver", "Something Received");

        if(LocationResult.hasResult(intent)) {
            this.mLocationResult = LocationResult.extractResult(intent);
            this.mLastLocation = mLocationResult.getLastLocation();
            Log.i("LocationReceiver", "Received Location");
            battery = getBatteryLevel();
            new HttpHandler() {
                @Override
                public void onResponse(String result) {

                }
            }.sendLocation(String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()), session_id, String.valueOf(battery));
        }
    }

    public float getBatteryLevel() {
        Intent batteryIntent = mContext.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50.0f;
        }
        return ((float)level / (float)scale) * 100.0f;
    }
}
