package com.rohan.ezone_sharda;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.time.LocalDate;

public class HomeScreen extends AppCompatActivity {

    FrameLayout webViewContainer;
    WebView webView;
    SharedPreferences ezone_data;
    LocalDate currentDate;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;

    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_screen);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER);

        // Initializing
        webView = findViewById(R.id.webView);
        webViewContainer = findViewById(R.id.webViewContainer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ezone_data = getSharedPreferences("EZONE_DATA", MODE_PRIVATE);
        ezone_data.registerOnSharedPreferenceChangeListener(ezone_listener);
        currentDate = LocalDate.now();
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        // Saving Keys in SharedPreference
        if (!ezone_data.contains("SYSTEM_ID")) {
            Log.d("HomeScreen", "SYSTEM_ID Key Saved");
            SharedPreferences.Editor editor = ezone_data.edit();
            editor.putString("SYSTEM_ID", "");
            editor.apply();
            Intent changeId = new Intent(this, Change_ID.class);
            startActivity(changeId);
        }
        if (!ezone_data.contains("OTP")) {
            Log.d("HomeScreen", "OTP Key Saved");
            SharedPreferences.Editor editor = ezone_data.edit();
            editor.putString("OTP", "");
            editor.apply();
        }
        if (!ezone_data.contains("CURRENT_DATE")) {
            Log.d("HomeScreen", "CURRENT_DATE Key Saved");
            SharedPreferences.Editor editor = ezone_data.edit();
            editor.putString("CURRENT_DATE", currentDate.toString());
            editor.apply();
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                Log.d("HomeScreen", currentDate.toString());
                if (!ezone_data.getString("CURRENT_DATE", "").equals(currentDate.toString()) || ezone_data.getString("OTP", "").isEmpty()) {
                    fillTextAndClickButton("system_id", ezone_data.getString("SYSTEM_ID", ""), "send_stu_otp_email");
                    SharedPreferences.Editor editor = ezone_data.edit();
                    editor.putString("CURRENT_DATE", currentDate.toString());
                    editor.apply();
                } else {
                    fillText("system_id", ezone_data.getString("SYSTEM_ID", ""));
                    fillText("otp", ezone_data.getString("OTP", ""));
                }
            }
        });
        webView.loadUrl("https://ezone.sharda.ac.in/ezone-2022/admin/studentlogin");

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });

        swipeRefreshLayout.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (webView.getScrollY() == 0) {
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });
    }

    SharedPreferences.OnSharedPreferenceChangeListener ezone_listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("OTP")) {
               fillText("otp", ezone_data.getString("OTP", ""));
               Log.d("HomeScreen", "OTP Updated");
            }
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.change_id) {
            Intent changeId = new Intent(this, Change_ID.class);
            startActivity(changeId);
        }

        if (id == R.id.change_otp) {
            Intent changeOtp = new Intent(this, Set_OTP.class);
            startActivity(changeOtp);
        }

        if (id == R.id.help) {
            Intent help = new Intent(this, HelpScreen.class);
            startActivity(help);
        }

        return false;
    }

    private void fillText(String textId, String text) {
        new Handler().postDelayed(() -> {
            String fillSystemIdScript =
                    "javascript: (function() {" +
                            "    var inputElement = document.getElementById('" + textId + "');" +
                            "    if(inputElement) {" +
                            "        inputElement.value = '" + text + "';" +
                            "        return true;" +
                            "    }" +
                            "    return false;" +
                            "})()";

            webView.evaluateJavascript(fillSystemIdScript, value -> {
                if (value.equals("false")) {
                    fillText(textId, text);
                }
            });
        }, 500);
    }

    private void fillTextAndClickButton(String textId, String text, String buttonId) {
        new Handler().postDelayed(() -> {
            String fillAndClickScript =
                    "javascript: (function() {" +
                            "    var inputElement = document.getElementById('" + textId + "');" +
                            "    var buttonElement = document.getElementById('" + buttonId + "');" +
                            "    if(inputElement && buttonElement) {" +
                            "        inputElement.value = '" + text + "';" +
                            "        buttonElement.click();" +  // Click the button
                            "        return true;" +
                            "    }" +
                            "    return false;" +
                            "})()";

            webView.evaluateJavascript(fillAndClickScript, value -> {
                if (value.equals("false")) {
                    fillTextAndClickButton(textId, text, buttonId);
                }
            });
        }, 500); // 0.5 second delay
    }
}