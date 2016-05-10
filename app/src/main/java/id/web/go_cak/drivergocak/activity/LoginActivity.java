package id.web.go_cak.drivergocak.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.session.UserSessionManager;
import id.web.go_cak.drivergocak.utils.Const;


public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button btnLogin;
    private EditText txtUsername,txtPassword;
    private TextView warning;

    private TextView titleToolbar;
    private String mTitle;
    private NavigationView navigationView;

    //private static final String REGISTER_URL = "http://192.168.43.49/servers/php/api/logindriver.php";
    //private static final String REGISTER_URL = "http://go-cak.web.id/gmc/index.php/Welcome/logindriver";
    private static final String REGISTER_URL = Const.WELCOME_URL+"logindriver";
    //{"users":{"ID":"5","nama":"Radi","email":"1Radi@radi.com","telp":"1081392380333"}}
    private static final String TAG_USERS = "users";

    // contacts JSONArray
    JSONArray USERS = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;

    // Session Manager Class
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        sessionManager = new UserSessionManager(this);
        //sessionManager.checkMain();

        btnLogin    = (Button) findViewById(R.id.btnLogin);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        warning     = (TextView) findViewById(R.id.warning);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProcess();
            }
        });

    }

    private void cekLogin(String name,String password) {

        warning.setVisibility(View.INVISIBLE);

        String urlSuffix = "?name="+name+"&password="+password;

        class RegisterUser extends AsyncTask<String, Void, String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                warning.setVisibility(View.VISIBLE);
                loading = ProgressDialog.show(LoginActivity.this, "Tunggu Beberapa saat","Pengecekan database", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                warning.setVisibility(View.VISIBLE);
                //warning.setText(s);
                Log.d("hasil", s);

                if (s != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(s);
                        USERS = jsonObj.getJSONArray(TAG_USERS);
                        for(int i=0; i < USERS.length(); i++) {
                            JSONObject c = USERS.getJSONObject(i);

                            if (c.getString("code").equals("12")) {
                                String cID = c.getString("ID");
                                String cname = c.getString("nama");
                                String no_anggota = c.getString("no_anggota");
                                String ctelp = c.getString("telp");
                                String foto  = c.getString("foto");
                               // warning.setText(c.getString("ID") + " (" + c.getString("telp") + " )"+c.getString("nama") + " (" + c.getString("email") + " )");
                               sessionManager.createUserIdSession(true,cID, cname, ctelp, no_anggota,foto);
                               //sessionManager.createUserIdSession(true, "1", "radi@radi.com");


                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            } else {
                                warning.setText("Terjadi Kesalahan Login, Periksa Inputan Anda");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        warning.setText("Login Gagal "+e);
                    }

                } else {
                    warning.setText(s);
                }


            }

            @Override
            protected String doInBackground(String... params) {
                String s = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(REGISTER_URL+s);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String result;

                    result = bufferedReader.readLine();

                    return result;
                }catch(Exception e){
                    return null;
                }
            }

        }

        RegisterUser ru = new RegisterUser();
        ru.execute(urlSuffix);

    }

    public void loginProcess() {

        warning.setVisibility(View.INVISIBLE);

        //if(password1.getText().equals(password2.getText())) {

        String name = txtUsername.getText().toString().trim().toLowerCase();
        String password = txtPassword.getText().toString().trim().toLowerCase();

        if(name.length() > 0  && password.length() > 0)
            cekLogin(name, password);
        else {
            warning.setVisibility(View.VISIBLE);
            warning.setText("Input ada yang kosong");
        }
        /*} else {
            warning.setVisibility(View.VISIBLE);
            warning.setText("Pengisian Kata sandi tidak sama");
        }*/

    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }


}
