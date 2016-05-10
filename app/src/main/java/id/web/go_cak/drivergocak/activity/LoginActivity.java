package id.web.go_cak.drivergocak.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.model.OauthUser;
import id.web.go_cak.drivergocak.model.User;
import id.web.go_cak.drivergocak.service.ServiceLogin;
import id.web.go_cak.drivergocak.session.UserSession;


public class LoginActivity extends AppCompatActivity {
    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.phone_tel) TextInputLayout phoneTel;
    @Bind(R.id.password_tel) TextInputLayout passwordTel;
    @Bind(R.id.phone_edittext) EditText phoneEditText;
    @Bind(R.id.password_edittext) EditText passwordEditText;

    private Boolean exit = false;
    private UserSession sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        sessionManager = new UserSession(this);
    }

    @OnClick(R.id.login_textview)
    public void onClickLogin() {

        String name = phoneEditText.getText().toString().trim().toLowerCase();
        String password = passwordEditText.getText().toString().trim().toLowerCase();

        if (!validatePhone()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Loading ....");
        progressDialog.show();

        new ServiceLogin(this).fetchLogin(name, password,
                new ServiceLogin.LoginCallBack() {
                    @Override
                    public void onSuccess(OauthUser oauthUser) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        User user = oauthUser.users.get(0);
                        if (user != null) {
                            sessionManager.createUserIdSession(true, user.ID, user.nama, user.telp, user.noAnggota, user.foto);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.do_nothing, R.anim.do_nothing);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        showSnackBar(message);
                    }
                });
    }

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

    private void showSnackBar(String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private boolean validatePhone() {
        if (TextUtils.isEmpty(phoneEditText.getText())) {
            phoneTel.setError(getString(R.string.no_telp_masih_kosong));
            return false;
        } else {
            phoneTel.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        if (TextUtils.isEmpty(passwordEditText.getText())) {
            passwordTel.setError(getString(R.string.password_masih_kosong));
            return false;
//        } else if (passwordEditText.getText().toString().length() < 4) {
//            formErrorTextview.setText(getString(R.string.password_minimal));
//            return false;
        } else {
            passwordTel.setErrorEnabled(false);
            return true;
        }
    }

}
