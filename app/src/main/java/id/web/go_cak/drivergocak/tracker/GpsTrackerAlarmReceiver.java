package id.web.go_cak.drivergocak.tracker;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import id.web.go_cak.drivergocak.session.UserSession;

// make sure we use a WakefulBroadcastReceiver so that we acquire a partial wakelock
public class GpsTrackerAlarmReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = "GpsTrackerAlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {

        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable() || mobile.isAvailable()) {
            UserSession sessionManager = new UserSession(context);
            if (sessionManager.getIdUser() != null) {
                context.startService(new Intent(context, LocationService.class));
                Log.d(TAG, "hasil menjalankan LocationService.class ");
                context.startService(new Intent(context, GPSTracker.class));
                Log.d(TAG, "hasil menjalankan GPSTracker.class ");
            }
        }
    }
}
