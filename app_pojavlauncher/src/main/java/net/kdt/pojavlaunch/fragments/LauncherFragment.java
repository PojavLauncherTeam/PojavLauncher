package net.kdt.pojavlaunch.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class LauncherFragment extends Fragment {

	private WebView mNewsWebview;
	private View mRootView;
	private Thread mValidUrlSelectorThread;
	private String mValidChangelog = "/changelog.html";
	private boolean mInterruptLoad = false;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
		mRootView = inflater.inflate(R.layout.fragment_news, container, false);
        return mRootView;
    }

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		mNewsWebview = (WebView) getView().findViewById(R.id.lmaintabnewsNewsView);
		mNewsWebview.setWebViewClient(new WebViewClient(){

			// API < 23
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Log.i("WebNews",failingUrl + ": "+description);
				if(mNewsWebview == null) return;

				if(mValidUrlSelectorThread.isAlive()) mValidUrlSelectorThread.interrupt();
				removeWebView();
				//Change the background to match the other pages.
				//We change it only when the webView is removed to avoid huge overdraw.
				LauncherFragment.this.mRootView.setBackgroundColor(Color.parseColor("#44000000"));
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(!url.equals(Tools.URL_HOME + mValidChangelog)){
					Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(i);
					return true;
				}
				return false;
			}

			@RequiresApi(23) //API 23+
			@Override
			public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
				Log.i("WebNews",error.getDescription()+"");
				if(mNewsWebview == null) return;

				if(mValidUrlSelectorThread.isAlive()) mValidUrlSelectorThread.interrupt();
				removeWebView();
				LauncherFragment.this.mRootView.setBackgroundColor(Color.parseColor("#44000000"));
			}

			@RequiresApi(23)
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
				if(!request.getUrl().toString().equals(Tools.URL_HOME + mValidChangelog)){
					Intent i = new Intent(Intent.ACTION_VIEW, request.getUrl());
					startActivity(i);
					return true;
				}
				return false;
			}
		});

		mNewsWebview.clearCache(true);
		mNewsWebview.getSettings().setJavaScriptEnabled(true);
		mValidUrlSelectorThread = new Thread(this::selectValidUrl);
		mValidUrlSelectorThread.start();
		if(!mInterruptLoad) mNewsWebview.loadUrl(Tools.URL_HOME + mValidChangelog);
	}

	private void selectValidUrl() {
		String lang = LauncherPreferences.PREF_LANGUAGE;
		if(lang.equals("default")) lang = Locale.getDefault().getLanguage();
		final String localizedUrl = "/changelog-" + lang + ".html";

		if(!tryUrl(Tools.URL_HOME+localizedUrl)) return;

		new Handler(Looper.getMainLooper()).post(() -> {
			mInterruptLoad = true;
			mValidChangelog = localizedUrl;
			mNewsWebview.loadUrl(Tools.URL_HOME + mValidChangelog);
		});
	}

	private boolean tryUrl(String url) {
		Log.i("ChangelogLocale","Trying localized url: "+url);
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.connect();
			Log.i("ChangelogLocale","Code: "+conn.getResponseCode());
			return ("" + conn.getResponseCode()).startsWith("2");
		}catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void removeWebView() {
		//Removing the parent which contain the webView crashes the viewPager.
		//So I just try to "minimize" its impact on memory instead

		mNewsWebview.clearHistory();
		mNewsWebview.clearCache(true);

		// Loading a blank page is optional, but will ensure that the WebView isn't doing anything when you destroy it.
		mNewsWebview.loadUrl("about:blank");

		mNewsWebview.onPause();
		mNewsWebview.removeAllViews();
		mNewsWebview.destroyDrawingCache();

		// make sure to call webNews.resumeTimers().
		mNewsWebview.pauseTimers();

		mNewsWebview.setVisibility(View.GONE);

		mNewsWebview.destroy();

		// Null out the reference so that you don't end up re-using it.
		mNewsWebview = null;
	}


}
