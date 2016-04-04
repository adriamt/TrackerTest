package com.amt.trackertest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

public class LocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Location location = (Location) intent.getExtras().get(LocationClient.KEY_LOCATION_CHANGED);
        JsonObject json = new JsonObject();
        json.addProperty("lat", location.getLatitude());
        json.addProperty("lon", location.getLongitude());
        ForgeApp.event("background_geolocation.locationChanged", json);
    }
}
