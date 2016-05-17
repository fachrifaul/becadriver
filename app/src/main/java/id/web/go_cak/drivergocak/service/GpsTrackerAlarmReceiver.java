package id.web.go_cak.drivergocak.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import id.web.go_cak.drivergocak.session.UserSession;

// make sure we use a WakefulBroadcastReceiver so that we acquire a partial wakelock
public class GpsTrackerAlarmReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = "GpsTrackerAlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        UserSession sessionManager = new UserSession(context);
        if (sessionManager.getIdUser() != null) {
            context.startService(new Intent(context, LocationService.class));
            context.startService(new Intent(context, GPSTracker.class));
        }
    }
}
