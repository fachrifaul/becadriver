package id.web.go_cak.drivergocak.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import id.web.go_cak.drivergocak.tracker.GpsTrackerAlarmReceiver;

/**
 * Created by fachrifebrian on 5/10/16.
 */
public class Utils {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("PLAY_SERVICES", "This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }

    public static void startAlarmManager(Context context) {
        Log.d("AlarmManager", "hasil startAlarmManager");

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                30000, // 60000 = 1 minute, 5000 = 5 seconds
                pendingIntent);
        Log.d("AlarmManager", "Update GPS location");

    }

    public static void cancelAlarmManager(Context context) {
        Log.d("AlarmManager", "cancelAlarmManager");
        Intent gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }


}
