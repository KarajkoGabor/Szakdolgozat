package hu.blogspot.limarapeksege.asyncs;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.activity.MainPage;
import hu.blogspot.limarapeksege.model.Category;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.SqliteHelper;
import hu.blogspot.limarapeksege.util.XmlParser;
import hu.blogspot.limarapeksege.util.handlers.recipe.RecipeActionsHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.util.DateTime;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;

public class AsyncPrepareRecipeDatas extends AsyncTask {

	private Context context;
	private SqliteHelper db;
	private RecipeActionsHandler util;
	private ProgressDialog progDialog;
	private TextView splashMessage;
	private Activity activity;
	private SharedPreferences savedSettings;

	public AsyncPrepareRecipeDatas(Context context, Activity activity) {
		this.context = context;
		this.activity = activity;
		this.savedSettings = PreferenceManager
				.getDefaultSharedPreferences(context);
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		splashMessage = (TextView) activity.findViewById(R.id.splashMessage);
		activity.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	protected Boolean doInBackground(Object... params) {
		// TODO Auto-generated method stub
		ArrayList<Category> categories;
		ArrayList<String> loadingMessages = setLoadingMessages();
		int i = 1;
		int index = 0;
		util = new RecipeActionsHandler(this.context);
		db = SqliteHelper.getInstance(context);
		boolean isThereNewRecipe = isThereNewRecipe();
		long startTime = System.currentTimeMillis();

		if (db.getAllCategories().size() == 0 || isThereNewRecipe ) { // ha m�g nincsenek a receptek
													// let�ltve
			categories = util.categoryParser();

			for(Category category : categories){
				if(i%2 == 1){
					publishProgress(loadingMessages.get(index));
					index++;
				}
				i++;
				int categoryID = db.getCategoryByName(category.getName()).getId();
				util.gatherRecipeData(category.getLabel(), categoryID, isThereNewRecipe);
			}

			setLatestUploadDate();
		}
		long endTime = System.currentTimeMillis();
		Log.w(GlobalStaticVariables.LOG_TAG, "Eltelt idő " + (endTime-startTime) + " ms");

		return null;
	}

	@Override
	protected void onPostExecute(Object o) {
		super.onPostExecute(o);
		activity.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		Intent intent = new Intent(activity, MainPage.class);
		activity.startActivity(intent);
		activity.finish();
	}

	@Override
	protected void onProgressUpdate(Object[] values) {
		super.onProgressUpdate(values);
		splashMessage.setText(values[0].toString());
	}

	private ArrayList<String> setLoadingMessages() {
		ArrayList<String> loadingMessages = new ArrayList<String>();
		try {
			XmlParser parser = new XmlParser();
			XmlPullParser xpp = activity.getResources().getXml(R.xml.splashloadingmessages);
			loadingMessages = (ArrayList<String>) parser.parseXml(xpp,"splash_loading_messages");
		} catch (Throwable t) {
			Toast.makeText(activity, "Request failed: " + t.toString(),
					Toast.LENGTH_LONG).show();
		}

		return loadingMessages;
	}

	private boolean isThereNewRecipe(){
		Date latestUploaded = util.getLastUploadedRecipeDate();

//		DateTime dateTime = new DateTime("2016-01-05");
//		SharedPreferences.Editor editor = savedSettings.edit();
//		editor.putLong("last_modified", dateTime.getValue());
//		editor.apply();

		Date lastSavedDate = new Date(savedSettings.getLong("last_modified", 0));

		return latestUploaded.after(lastSavedDate);
	}

	private void setLatestUploadDate(){
		Date latestUploaded = util.getLastUploadedRecipeDate();
		SharedPreferences.Editor editor = savedSettings.edit();
		editor.putLong("last_modified", latestUploaded.getTime());
		editor.apply();
	}

}
