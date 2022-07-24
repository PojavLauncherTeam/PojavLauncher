package net.kdt.pojavlaunch.authenticator.elyby.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import net.kdt.pojavlaunch.R;

public class ElyByLoginGUIActivity extends AppCompatActivity {
    public static final int AUTHENTICATE_ELYBY_REQUEST = 61;
    WebView webView;
    ProgressDialog waitDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CookieManager.getInstance().removeAllCookie();
        waitDialog = new ProgressDialog(this);
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitDialog.setIndeterminate(true);
        waitDialog.setMessage(getString(R.string.global_waiting));
        webView = new WebView(this);
        webView.setWebViewClient(new WebViewTrackClient());
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.loadUrl("https://account.ely.by/oauth2/v1" +
                "?client_id=pojavlauncher" +
                "&redirect_uri=pojavlauncher://ely.by/login" +
                "&response_type=code" +
                "&scope=account_info%20minecraft_server_session%20offline_access");
        setContentView(webView);
    }
    class WebViewTrackClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.startsWith("pojavlauncher")) {
                Intent data = new Intent();
                data.setData(Uri.parse(url));
                if(waitDialog.isShowing()) waitDialog.dismiss();
                setResult(Activity.RESULT_OK,data);
                finish();
                return true;
            }else{
                return super.shouldOverrideUrlLoading(view, url);
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //super.onPageStarted(view, url, favicon);
            if(!waitDialog.isShowing()) waitDialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            waitDialog.hide();
        }
    }
}
