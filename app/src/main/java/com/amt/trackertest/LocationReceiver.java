package com.amt.trackertest;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
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


    private String INTERVAL_MILLIS = "";

    public LocationReceiver() {
    }

    // WEB: https://developers.google.com/android/reference/com/google/android/gms/location/LocationResult.html#field-summary

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        // Need to check and grab the Intent's extras like so
        sharedPref = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        session_id = sharedPref.getString("session_id", "NULL");
        INTERVAL_MILLIS = sharedPref.getString("interval_millis", "NULL");
        Log.i("LocationReceiver", "Something Received");
        Log.i("LocationReceiver", "Session ID: " + session_id);
        Log.i("LocationReceiver", "Interval : " + INTERVAL_MILLIS);

        if(LocationResult.hasResult(intent)) {
            this.mLocationResult = LocationResult.extractResult(intent);
            this.mLastLocation = mLocationResult.getLastLocation();
            Log.i("LocationReceiver", "Received Location");
            Log.i("LocationReceiver", "Lat: " +String.valueOf(mLastLocation.getLatitude())+ "Lon: " + String.valueOf(mLastLocation.getLongitude()) +
                    " Acc: " + String.valueOf(mLastLocation.getAccuracy()) + " Long: " + String.valueOf(mLocationResult.getLocations().size()));
            battery = getBatteryLevel();
            new HttpHandler() {
                @Override
                public void onResponse(String result) {
                    if(!INTERVAL_MILLIS.equals(result)) {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("interval_millis", result);
                        editor.apply();
                        Intent i2 = new Intent(mContext, BackgroundLocationService.class);
                        i2.putExtra("foo", "bar");
                        Log.e("StopSvc", "Service");
                        mContext.stopService(i2);
                        Intent i = new Intent(mContext, BackgroundLocationService.class);
                        i.putExtra("foo", "bar");
                        Log.e("StartSvc", "Service");
                        mContext.startService(i);
                    }

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
