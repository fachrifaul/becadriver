package id.web.go_cak.drivergocak.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.utils.Const;

import id.web.go_cak.drivergocak.session.UserSessionManager;

public class Report extends Activity {

    private WebView webView;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Rekap Harian");

        // Session class instance
        sessionManager = new UserSessionManager(this);
        if (!sessionManager.isUserLoggedIn()) {
            sessionManager.checkLogin();
        }

        webView  =(WebView)findViewById(R.id.webView);
        webView.setWebViewClient(new MyBrowser());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        String getSend = "?month=01&year=2016";
        webView.loadUrl(Const.WELCOME_URL + "struk/"+sessionManager.getIdUser()+""+ getSend);

    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent sendIntent = new Intent(Report.this, MainActivity.class);
        startActivity(sendIntent);
        finish();
    }

}
