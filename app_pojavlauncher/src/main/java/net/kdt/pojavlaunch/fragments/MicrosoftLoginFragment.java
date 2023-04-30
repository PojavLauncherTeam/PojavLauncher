package net.kdt.pojavlaunch.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.extra.ExtraCore;

public class MicrosoftLoginFragment extends Fragment {
    public static final String TAG = "MICROSOFT_LOGIN_FRAGMENT";
    private WebView mWebview;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mWebview = (WebView) inflater.inflate(R.layout.fragment_microsoft_login, container, false);
        CookieManager.getInstance().removeAllCookies(this::onCookiesRemoved);
        return mWebview;
    }

    @SuppressLint("SetJavaScriptEnabled") // required for Microsoft log-in
    public void onCookiesRemoved(Boolean b) {
        WebSettings settings = mWebview.getSettings();

        settings.setJavaScriptEnabled(true);
        mWebview.clearHistory();
        mWebview.clearCache(true);
        mWebview.clearFormData();
        mWebview.clearHistory();
        mWebview.setWebViewClient(new WebViewTrackClient());

        mWebview.loadUrl("https://login.live.com/oauth20_authorize.srf" +
                "?client_id=00000000402b5328" +
                "&response_type=code" +
                "&scope=service%3A%3Auser.auth.xboxlive.com%3A%3AMBI_SSL" +
                "&redirect_url=https%3A%2F%2Flogin.live.com%2Foauth20_desktop.srf");
    }

    /* Expose webview actions to others */
    public boolean canGoBack(){ return mWebview.canGoBack();}
    public void goBack(){ mWebview.goBack();}

    /** Client to track when to sent the data to the launcher */
    class WebViewTrackClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.startsWith("ms-xal-00000000402b5328")) {
                // Should be captured by the activity to kill the fragment and get
                ExtraCore.setValue(ExtraConstants.MICROSOFT_LOGIN_TODO, Uri.parse(url));
                Toast.makeText(view.getContext(), "Login started !", Toast.LENGTH_SHORT).show();
                Tools.removeCurrentFragment(requireActivity());

                return true;
            }

            // Sometimes, the user just clicked cancel
            if(url.contains("res=cancel")){
                requireActivity().onBackPressed();
                return true;
            }


            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {}

        @Override
        public void onPageFinished(WebView view, String url) {}
    }


}
