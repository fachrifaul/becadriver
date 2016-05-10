package id.web.go_cak.drivergocak.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.tracker.GpsTrackerAlarmReceiver;

public class TransaksiActivity extends TabActivity {

    private static final String INCOMP = "Incomplete";
    private static final String COMP = "complete";

    static final String TAG = "TransaksiActivity";

    private Intent gpsTrackerIntent;
    private PendingIntent pendingIntent;
    private boolean currentlyTracking;
    private int intervalInMinutes = 1;
    private AlarmManager alarmManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi);

        TabHost tabHost = getTabHost();

        // Incomplete Tab
        TabSpec incomp = tabHost.newTabSpec(INCOMP);
        // Tab Icon
        incomp.setIndicator(INCOMP, getResources().getDrawable(R.drawable.incmplote));
        Intent InCompIntent = new Intent(this, IncompleteActivity.class);
        // Tab Content
        incomp.setContent(InCompIntent);

        // Ccomplete Tab
        TabSpec comp = tabHost.newTabSpec(COMP);
        // Tab Icon
        comp.setIndicator(COMP, getResources().getDrawable(R.drawable.done));
        Intent CompIntent = new Intent(this, CompleteActivity.class);
        // Tab Content
        comp.setContent(CompIntent);

        // Adding all TabSpec to TabHost
        tabHost.addTab(incomp); // Adding Incomplete tab
        tabHost.addTab(comp); // Adding Complete tab

        startAlarmManager();

    }

    public void startAlarmManager() {
        Log.d(TAG, "hasil startAlarmManager");
        currentlyTracking = true;

        Context context = getBaseContext();
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);

        // SharedPreferences sharedPreferences = this.getSharedPreferences("com.websmithing.gpstracker.prefs", Context.MODE_PRIVATE);
        // intervalInMinutes = sharedPreferences.getInt("intervalInMinutes", 1);
        intervalInMinutes = 3;

       /* alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                intervalInMinutes * 60000, // 60000 = 1 minute
                pendingIntent);*/

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                5000, // 60000 = 1 minute
                pendingIntent);


        //Toast.makeText(this,"Update GPS location",Toast.LENGTH_LONG).show();
        Log.d("hasil", "Update GPS location");

    }

    public void cancelAlarmManager() {
        Log.d(TAG, "cancelAlarmManager");

        Context context = getBaseContext();
        Intent gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent sendIntent = new Intent(TransaksiActivity.this, MainActivity.class);
        startActivity(sendIntent);
        finish();
    }

}
