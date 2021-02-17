package net.kdt.pojavlaunch.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.*;

public class LauncherFragment extends Fragment
{
	private WebView webNews;
	private View view;
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.lmaintab_news, container, false);
        return view;
    }
	@Override
	public void onActivityCreated(Bundle p1)
	{
		super.onActivityCreated(p1);

		webNews = (WebView) getView().findViewById(R.id.lmaintabnewsNewsView);
		webNews.setWebViewClient(new WebViewClient(){

			// API < 23
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				if(webNews != null){
					removeWebView();
					//Change the background to match the other pages.
					//We change it only when the webView is removed to avoid huge overdraw.
					LauncherFragment.this.view.setBackgroundColor(Color.parseColor("#44000000"));
				}
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(!url.equals(Tools.URL_HOME + "/changelog.html")){
					Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(i);
					return true;
				}
				return false;
			}

			@RequiresApi(23) //API 23+
			@Override
			public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
				if(webNews != null){
					removeWebView();
					LauncherFragment.this.view.setBackgroundColor(Color.parseColor("#44000000"));
				}
			}

			@RequiresApi(23)
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
				if(!request.getUrl().toString().equals(Tools.URL_HOME + "/changelog.html")){
					Intent i = new Intent(Intent.ACTION_VIEW, request.getUrl());
					startActivity(i);
					return true;
				}
				return false;
			}
		});
		webNews.clearCache(true);
		webNews.getSettings().setJavaScriptEnabled(true);
		webNews.loadUrl(Tools.URL_HOME + "/changelog.html");
	}

	private void removeWebView() {
		//Removing the parent which contain the webView crashes the viewPager.
		//So I just try to "minimize" its impact on memory instead

		webNews.clearHistory();
		webNews.clearCache(true);

		// Loading a blank page is optional, but will ensure that the WebView isn't doing anything when you destroy it.
		webNews.loadUrl("about:blank");

		webNews.onPause();
		webNews.removeAllViews();
		webNews.destroyDrawingCache();

		// make sure to call webNews.resumeTimers().
		webNews.pauseTimers();

		webNews.setVisibility(View.GONE);

		webNews.destroy();

		// Null out the reference so that you don't end up re-using it.
		webNews = null;
	}


}
