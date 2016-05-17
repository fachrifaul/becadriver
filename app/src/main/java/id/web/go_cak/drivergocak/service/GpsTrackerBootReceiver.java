package id.web.go_cak.drivergocak.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import id.web.go_cak.drivergocak.session.UserSession;

public class GpsTrackerBootReceiver extends BroadcastReceiver {
    private static final String TAG = "GpsTrackerBootReceiver";
    private UserSession sessionManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        sessionManager = new UserSession(context);
        Intent intentLocation = new Intent(context, LocationService.class);
        if (sessionManager.getIdUser() != null) {
            context.startService(intentLocation);
            Log.wtf(TAG, "onReceive: start");
        } else {
            Log.wtf(TAG, "onReceive: stop");
            context.stopService(intentLocation);
        }
    }
}