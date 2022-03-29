package net.kdt.pojavlaunch.authenticator.microsoft.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import net.kdt.pojavlaunch.R;

public class MicrosoftLoginGUIActivity extends AppCompatActivity {
    public static final int AUTHENTICATE_MICROSOFT_REQUEST = 60;
    WebView webView;
    ProgressDialog waitDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        waitDialog = new ProgressDialog(this);
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitDialog.setIndeterminate(true);
        waitDialog.setMessage(getString(R.string.global_waiting));
        webView = new WebView(this);
        CookieManager.getInstance().removeAllCookie();
        webView.setWebViewClient(new WebViewTrackClient());
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.loadUrl("https://login.live.com/oauth20_authorize.srf" +
                "?client_id=00000000402b5328" +
                "&response_type=code" +
                "&scope=service%3A%3Auser.auth.xboxlive.com%3A%3AMBI_SSL" +
                "&redirect_url=https%3A%2F%2Flogin.live.com%2Foauth20_desktop.srf");
        setContentView(webView);
    }
    class WebViewTrackClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.startsWith("ms-xal-00000000402b5328")) {
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
