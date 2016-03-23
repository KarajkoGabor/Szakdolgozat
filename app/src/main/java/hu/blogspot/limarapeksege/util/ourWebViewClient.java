package hu.blogspot.limarapeksege.util;

import hu.blogspot.limarapeksege.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ourWebViewClient extends WebViewClient {

	private String currentUrl;
	private ProgressDialog progressDialog;
	private Context context;

	public ourWebViewClient(String currentUrl, Context context) {
		this.currentUrl = currentUrl;
		this.context = context;
		progressDialog = new ProgressDialog(context);

	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {

		Log.w("LimaraPeksege", currentUrl + "currentUrl");
		Log.w("LimaraPeksege", url + "loaded url");
		if (url.contains(currentUrl+"?m=1")) {
			view.loadUrl(url);
			Log.w("LimaraPeksege", "url load");

		}
		return true;
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);
		view.clearCache(false);
		if(progressDialog.isShowing()){
			progressDialog.dismiss();
		}
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		super.onPageStarted(view, url, favicon);
		if(!progressDialog.isShowing()){
			progressDialog = ProgressDialog.show(context, "", context
					.getResources().getString(R.string.load_recipe));
		}
	}

	@Override
	public void onLoadResource(WebView view, String url) {
		super.onLoadResource(view, url);

	}

}
