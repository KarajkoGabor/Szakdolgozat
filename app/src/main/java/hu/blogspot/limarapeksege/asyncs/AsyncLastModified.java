package hu.blogspot.limarapeksege.asyncs;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

//for the recipe update
public class AsyncLastModified extends AsyncTask<Void, String, Boolean> {

	private Context context;

	public AsyncLastModified(Context context) {
		this.context = context;
	}

	@Override
	protected Boolean doInBackground(Void... params) {

		SharedPreferences savedSettings = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = savedSettings.edit();
		long lastPageModified = getPageLastModified();
		long lastSavedModifiedDate = savedSettings.getLong("last_modified",
				lastPageModified);
		Log.w("LimaraPéksége", lastSavedModifiedDate + "last saved modified ");
		Log.w("LimaraPéksége", lastPageModified + "last modified ");

		if (lastSavedModifiedDate < lastPageModified) {
			editor.putLong("last_modified", lastPageModified);
			editor.putBoolean("is_new_recipe", true);
			editor.commit();
			Log.w("LimaraPéksége", "there is new recipe");
			return true;
		} else {
			editor.putBoolean("is_new_recipe", false);
			editor.commit();
			Log.w("LimaraPéksége", "there isn't new recipe");
			return false;
		}

	}

	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}

	private Long getPageLastModified() {
		HttpURLConnection httpCon = null;
		URL url;
		try {
			url = new URL("http://limarapeksegetartalom.blogspot.hu/");
			httpCon = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		long date = httpCon.getLastModified();
		Date dateReal = new Date(date);
		Log.w("LimaraPéksége", dateReal.toString() + " last modified date");
		return date;
	}

}
