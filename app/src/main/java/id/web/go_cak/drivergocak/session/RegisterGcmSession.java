package id.web.go_cak.drivergocak.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class RegisterGcmSession {

    private static final String PREFER_NAME = "GcmMode";
    private static final String IS_REGISTERED = "isRegistered";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    private int PRIVATE_MODE = 0;
    private String RegID, SenderID;
    private boolean status;

    // Constructor
    public RegisterGcmSession(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createRegsiter(boolean status, String RegID) {
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
        if (!this.isRegistered()) {
            return false;
        } else {
            return true;
        }
    }

    public void removeRegister() {
        editor.clear();
        editor.commit();
    }

    public boolean isRegistered() {
        return pref.getBoolean(IS_REGISTERED, status);
    }

    public String getRegID() {
        return pref.getString(Const.REGID, RegID);
    }

    public String getSENDERID() {
        return pref.getString("SENDERID", SenderID);
    }

}
