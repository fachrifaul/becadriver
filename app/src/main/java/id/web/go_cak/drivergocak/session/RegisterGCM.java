package id.web.go_cak.drivergocak.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class RegisterGCM {

    // Sharedpref file name
    private static final String PREFER_NAME = "GcmMode";
    // All Shared Preferences Keys
    private static final String IS_REGISTERED = "isRegistered";
    // Shared Preferences reference
    SharedPreferences pref;
    // Editor reference for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    private String RegID, SenderID;
    private boolean status;

    // Constructor
    public RegisterGCM(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createRegsiter(boolean status, String RegID) {

        //public void createUserLoginSession(String StoreUserID, String email) {
        Log.v("kumaha ", RegID);

        // Storing status in pref
        editor.putString(Const.REGID, RegID);
        editor.putString("SENDERID", Const.SENDERID);
        editor.putBoolean(IS_REGISTERED, status);

        this.RegID = RegID;
        this.SenderID = Const.SENDERID;
        this.status = status;

        // commit changes
        editor.commit();
    }

    public boolean checkRegsitered() {
        if (!this.is_registered()) {
            return false;
        } else {
            return true;
        }
    }

    public void removeRegister() {
        editor.clear();
        editor.commit();
    }

    public boolean is_registered() {
        return pref.getBoolean(IS_REGISTERED, status);
    }

    public String getRegID() {
        return pref.getString(Const.REGID, RegID);
    }

    public String getSENDERID() {
        return pref.getString("SENDERID", SenderID);
    }

}
