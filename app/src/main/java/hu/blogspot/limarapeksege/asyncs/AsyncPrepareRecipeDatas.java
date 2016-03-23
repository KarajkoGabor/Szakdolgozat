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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    private RecipeActionsHandler util;
    private TextView splashMessage;
    private TextView splashPercent;
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
        splashPercent = (TextView) activity.findViewById(R.id.splashPercent);
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
        SqliteHelper db = SqliteHelper.getInstance(context);
        long startTime = System.currentTimeMillis();

        if(isFirstRun()){
            db.deleteCategoryTable();
            db.deleteRecipeTable();
        }

        if (isNetworkAvailable() && isThereNewRecipe()) { // ha m�g nincsenek a receptek
            // let�ltve

            if (db.getAllCategories().size() == 0) {
                categories = util.categoryParser();
            } else {
                categories = (ArrayList<Category>) db.getAllCategories();
            }

            for (Category category : categories) {
                if (i % 2 == 1) {
                    publishProgress(loadingMessages.get(index), (100 / categories.size() * i));
                    index++;
                }
                i++;
                int categoryID = db.getCategoryByName(category.getName()).getId();
                util.gatherRecipeData(category.getLabel(), categoryID, isThereNewRecipe());
            }

            setLatestUploadDate();
            setFirstRunVariable();

        }
        long endTime = System.currentTimeMillis();
        Log.w(GlobalStaticVariables.LOG_TAG, "Eltelt idő " + (endTime - startTime) + " ms");

        return null;
    }

    private void setFirstRunVariable() {
        SharedPreferences.Editor editor = savedSettings.edit();
        editor.putBoolean("firstRun", false);
        editor.apply();
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
        splashPercent.setText(values[1].toString() + " %");
    }

    private ArrayList<String> setLoadingMessages() {
        ArrayList<String> loadingMessages = new ArrayList<String>();
        try {
            XmlParser parser = new XmlParser();
            XmlPullParser xpp = activity.getResources().getXml(R.xml.splashloadingmessages);
            loadingMessages = (ArrayList<String>) parser.parseXml(xpp, "splash_loading_messages");
        } catch (Throwable t) {
            Toast.makeText(activity, "Request failed: " + t.toString(),
                    Toast.LENGTH_LONG).show();
        }

        return loadingMessages;
    }

    private boolean isThereNewRecipe() {
        Date latestUploaded = util.getLastUploadedRecipeDate();

//		DateTime dateTime = new DateTime("2016-01-05");
//		SharedPreferences.Editor editor = savedSettings.edit();
//		editor.putLong("last_modified", dateTime.getValue());
//		editor.apply();

        Date lastSavedDate = new Date(savedSettings.getLong("last_modified", 0));

        return latestUploaded.after(lastSavedDate);
    }

    private void setLatestUploadDate() {
        Date latestUploaded = util.getLastUploadedRecipeDate();
        SharedPreferences.Editor editor = savedSettings.edit();
        editor.putLong("last_modified", latestUploaded.getTime());
        editor.apply();
    }

    private boolean isNetworkAvailable() { // ellen�rizz�k van-e internet el�r�s
        ConnectivityManager connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private boolean isFirstRun() {
        return savedSettings.getBoolean("firstRun", true);
    }

}
