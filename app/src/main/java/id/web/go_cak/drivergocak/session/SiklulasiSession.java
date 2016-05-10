package id.web.go_cak.drivergocak.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


public class SiklulasiSession {

    // Sharedpref file name
    private static final String PREFER_NAME = "Sikrulasi";
    // All Shared Preferences Keys
    private static final String IS_CURRENTLY_PROC = "iscurrentlyProcessingLocation";
    // Shared Preferences reference
    SharedPreferences pref;
    // Editor reference for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    private String lats, longs,currentlyProccess;
    private boolean status;

    // Constructor
    public SiklulasiSession(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    //Create login session
    public void createUserIdSession(boolean status, String lats, String longs,String currentlyProccess) {

        //public void createUserLoginSession(String StoreUserID, String email) {
        Log.v("kumaha ", lats + "=" + longs);

        // Storing status in pref
        editor.putString(Const.LATS, lats);
        editor.putString(Const.LONGS, longs);
        editor.putString(Const.CURRENTYPROC, currentlyProccess);
        editor.putBoolean(IS_CURRENTLY_PROC, status);

        this.lats = lats;
        this.longs = longs;
        this.status = status;
        this.currentlyProccess = currentlyProccess;

        // commit changes
        editor.commit();
    }

    public void userLogoutUser() {
        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();
    }


    // Check for login
    public boolean isCurrentlyProc() {
        return pref.getBoolean(IS_CURRENTLY_PROC, status);
    }

    public String getlats() {
        return pref.getString(Const.LATS, lats);
    }

    public String getlongs() {
        return pref.getString(Const.LONGS, longs);
    }

    public String getcurrentlyProccess() {
        return pref.getString(Const.CURRENTYPROC, currentlyProccess);
    }

}