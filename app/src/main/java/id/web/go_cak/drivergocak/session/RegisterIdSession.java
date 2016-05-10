package id.web.go_cak.drivergocak.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import id.web.go_cak.drivergocak.activity.MainActivity;

/**
 * Created by fachrifebrian on 5/10/16.
 */
public class RegisterIdSession {

    private static final String PREFER_NAME = MainActivity.class.getSimpleName();
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;
    private String TAG = "RegisterIdSession";


    public RegisterIdSession(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void storeRegistrationId(String regId) {
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    public String getRegistrationId() {
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }

        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }


    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
