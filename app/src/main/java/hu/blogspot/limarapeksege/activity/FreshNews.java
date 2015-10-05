package hu.blogspot.limarapeksege.activity;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.util.ourWebViewClient;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class FreshNews extends Activity {

	private static String URL = "http://www.limarapeksege.hu/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fresh_news);

		WebView webView = (WebView) findViewById(R.id.freshNewsWeb);
		webView.getSettings()
				.setUserAgentString(
						"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0");
		webView.setWebViewClient(new ourWebViewClient(URL, FreshNews.this) {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// if (loaded == false)

				Log.w("LimaraPeksege", url + " load");
				if (url.equals(URL) || url.equals(URL + "?m=1")) {
					view.loadUrl(url);
					Log.w("LimaraPeksege", "url load");

				}
				Log.w("LimaraPeksege", "url not load");
				// loaded = true;
				return true;
			}
		});
		WebSettings settings = webView.getSettings();
		settings.setBuiltInZoomControls(true);
		webView.loadUrl(URL);

	}
}
