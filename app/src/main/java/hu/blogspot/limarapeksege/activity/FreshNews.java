package hu.blogspot.limarapeksege.activity;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.util.AnalyticsTracker;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.ourWebViewClient;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.android.gms.analytics.Tracker;

public class FreshNews extends Activity {

	private AnalyticsTracker trackerApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fresh_news);

		trackerApp = (AnalyticsTracker) getApplication();

		WebView webView = (WebView) findViewById(R.id.freshNewsWeb);
		webView.getSettings()
				.setUserAgentString(
						"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0");
		webView.setWebViewClient(new ourWebViewClient(GlobalStaticVariables.URL, FreshNews.this) {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// if (loaded == false)

				Log.w(GlobalStaticVariables.LOG_TAG, url + " load");
				if (url.equals(GlobalStaticVariables.URL) || url.equals(GlobalStaticVariables.URL + "?m=1")) {
					view.loadUrl(url);
					Log.w(GlobalStaticVariables.LOG_TAG, "url load");

				}
				Log.w(GlobalStaticVariables.LOG_TAG, "url not load");
				// loaded = true;
				return true;
			}
		});
		WebSettings settings = webView.getSettings();
		settings.setBuiltInZoomControls(true);
		webView.loadUrl(GlobalStaticVariables.URL);
		trackerApp.sendScreen(GlobalStaticVariables.NEWS_CLASS);

	}
}
