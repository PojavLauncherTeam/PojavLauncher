package net.kdt.pojavlaunch.mcfragments;

import android.os.*;
import android.view.*;
import android.webkit.*;
import androidx.annotation.*;
import androidx.fragment.app.*;
import net.kdt.pojavlaunch.*;

public class LauncherFragment extends Fragment
{
	private WebView webNews;
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.lmaintab_news, container, false);
        return view;
    }
	@Override
	public void onActivityCreated(Bundle p1)
	{
		super.onActivityCreated(p1);
		
		webNews = (WebView) getView().findViewById(R.id.lmaintabnewsNewsView);
		webNews.setWebChromeClient(new WebChromeClient(){});
		webNews.clearCache(true);
		webNews.getSettings().setJavaScriptEnabled(true);
		webNews.loadUrl(Tools.mhomeUrl + "/changelog.html");
	}
	
}
