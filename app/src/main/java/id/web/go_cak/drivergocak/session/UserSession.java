package id.web.go_cak.drivergocak.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class UserSession {

    // Sharedpref file name
    private static final String PREFER_NAME = "Grabspanet";
    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "isUserLoggedIn";
    // Shared Preferences reference
    SharedPreferences pref;
    // Editor reference for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    private String idUser, username,telp,no_anggota,Foto;
    private boolean status;

    // Constructor
    public UserSession(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    //Create login session
    public void createUserIdSession(boolean status, String idUser, String username,String telp,String no_anggota,String Foto) {

        //public void createUserLoginSession(String StoreUserID, String email) {
        Log.v("kumaha ", idUser + "=" + username);

        // Storing status in pref
        editor.putString(Const.id, idUser);
        editor.putString(Const.username, username);
        editor.putString(Const.TELP, telp);
        editor.putString(Const.ANGGOTA, no_anggota);
        editor.putString(Const.FOTO, Foto);
        editor.putBoolean(IS_USER_LOGIN, status);

        this.idUser = idUser;
        this.username = username;
        this.status = status;
        this.telp = telp;
        this.no_anggota = no_anggota;
        this.Foto = Foto;

        // commit changes
        editor.commit();
    }

    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * Else do anything
     */
    public boolean checkLogin() {
        if (!this.isUserLoggedIn()) {
            return true;
        } else {
            return false;
        }
    }

    public void userLogoutUser() {
        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();
    }


    // Check for login
    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, status);
    }

    public String getIdUser() {
        return pref.getString(Const.id, idUser);
    }

    public String getUsername() {
        return pref.getString(Const.username, username);
    }

    public String getTelp() {
        return pref.getString(Const.TELP, telp);
    }

    public String getAnggota() {
        return pref.getString(Const.ANGGOTA, no_anggota);
    }

    public String getFoto() {
        return pref.getString(Const.FOTO, Foto);
    }

}