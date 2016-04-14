package hu.blogspot.limarapeksege.asyncs;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.activity.MainActivity;
import hu.blogspot.limarapeksege.activity.MainPage;
import hu.blogspot.limarapeksege.model.Category;
import hu.blogspot.limarapeksege.model.Recipe;
import hu.blogspot.limarapeksege.model.WrongRecipeData;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.SqliteHelper;
import hu.blogspot.limarapeksege.util.XmlParser;
import hu.blogspot.limarapeksege.util.handlers.file.FileHandler;
import hu.blogspot.limarapeksege.util.handlers.image.ImageHandler;
import hu.blogspot.limarapeksege.util.handlers.recipe.RecipeActionsHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.util.DateTime;
import com.google.api.client.util.IOUtils;
import com.google.api.client.util.StringUtils;

import org.jsoup.helper.StringUtil;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class AsyncPrepareRecipeDatas extends AsyncTask {

    private Context context;
    private RecipeActionsHandler util;
    private TextView splashMessage;
    private TextView splashPercent;
    private Activity activity;
    private SharedPreferences savedSettings;
    private SqliteHelper db;

    public AsyncPrepareRecipeDatas(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.savedSettings = PreferenceManager
                .getDefaultSharedPreferences(context);
        this.db = SqliteHelper.getInstance(context);
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

        long startTime = System.currentTimeMillis();

//        preparedSavedAndFavoriteRecipes(); //TODO REMOVE!!!

//        downLoadAllRecipes();

        setupApp();
        long endTime = System.currentTimeMillis();
        Log.w(GlobalStaticVariables.LOG_TAG, "Eltelt idő " + (endTime - startTime) + " ms");

        return null;
    }

    private void setupApp() {
        ArrayList<String> loadingMessages = setLoadingMessages();
        int i = 1;
        ArrayList<Category> categories;
        int index = 0;
        util = new RecipeActionsHandler(this.context);

        if(isQuickFix()){
            clearApplicationData();
        }else if (isFirstRun() || !isDownloadedRecipesPrepared()) {


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
            publishProgress("Mindjárt kész :)", 95); // 95% of run


            if (isFirstRun() || !isDownloadedRecipesPrepared()) {
                preparedSavedAndFavoriteRecipes();
            }
            setFirstRunVariable(false);
            setDownloadedRecipesPrepared();
            setQuickFixVariable();

            db.closeDatabase();

        }
    }

    private void downLoadAllRecipes() {

        List<Recipe> recipeList = db.getAllRecipes();

        for (Recipe recipe : recipeList) {
            try {
                util.saveRecipePage(recipe, false);
                Log.w(GlobalStaticVariables.LOG_TAG, recipe.getRecipeName() + " has been saved");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.w(GlobalStaticVariables.LOG_TAG, "All recipes have been saved");

    }

    private void setDownloadedRecipesPrepared() {
        SharedPreferences.Editor editor = savedSettings.edit();
        editor.putBoolean("downloadedPrepared", true);
        editor.apply();
    }

    private boolean isDownloadedRecipesPrepared() {
        return savedSettings.getBoolean("downloadedPrepared", false);
    }

    private void preparedSavedAndFavoriteRecipes() {

        FileHandler fileHandler = new FileHandler();
        ImageHandler imageHandler = new ImageHandler();
        XmlPullParser xpp = this.context.getResources().getXml(R.xml.wrongrecipes);
        File[] savedRecipeFiles = fileHandler.getSavedRecipeFiles();
        String savedRecipePath = Environment.getExternalStorageDirectory() + GlobalStaticVariables.SAVED_RECIPE_PATH;
        File[] favoriteRecipeFiles = fileHandler.getFavoriteRecipeFiles();
        String favoriteRecipePath = Environment.getExternalStorageDirectory() + GlobalStaticVariables.FAVORITE_RECIPE_PATH;
        File[] imageFiles = fileHandler.getImageFiles();
        String imagesPath = Environment.getExternalStorageDirectory() + GlobalStaticVariables.IMAGES_PATH;

        XmlParser xmlParser = new XmlParser();
        try {
            ArrayList<WrongRecipeData> wrongRecipeDatasList = xmlParser.parseWrongRecipesXML(xpp);

            if(savedRecipeFiles != null){
                fileHandler.renameFiles(xpp, savedRecipeFiles, this.context, savedRecipePath, wrongRecipeDatasList);
            }

            if(favoriteRecipeFiles != null){
                fileHandler.renameFiles(xpp, favoriteRecipeFiles, this.context, favoriteRecipePath, wrongRecipeDatasList);
            }

            if(imageFiles != null){
                fileHandler.renameFiles(xpp, imageFiles, this.context, imagesPath, wrongRecipeDatasList);
            }

        } catch (XmlPullParserException | IOException | NullPointerException e) {
            e.printStackTrace();
        }

        //TODO RECOLLECT FILENAMES
        if(savedRecipeFiles != null){
            savedRecipeFiles = fileHandler.getSavedRecipeFiles();

            for (File currentFile : savedRecipeFiles) {
                if(!currentFile.isDirectory() && !StringUtil.isNumeric(currentFile.getName())){
                    Recipe recipe = db.getRecipeById(currentFile.getName());

                    StringBuilder text = new StringBuilder();
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(currentFile));
                        String line;
                        while ((line = br.readLine()) != null) {
                            text.append(line);
                            text.append('\n');
                        }
                        br.close();
                        String finalContent = imageHandler.replaceImageSrc(text.toString(), recipe.getId());
                        currentFile.delete();
                        fileHandler.writeToFile(finalContent, GlobalStaticVariables.SAVED_RECIPES,
                                GlobalStaticVariables.SAVED_RECIPE_PATH, recipe.getId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        }
        if(favoriteRecipeFiles != null){
            favoriteRecipeFiles = fileHandler.getFavoriteRecipeFiles();

            for (File currentFile : favoriteRecipeFiles) {
                if(!currentFile.isDirectory() && !StringUtil.isNumeric(currentFile.getName())) {
                    Recipe recipe = db.getRecipeById(currentFile.getName());

                    StringBuilder text = new StringBuilder();
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(currentFile));
                        String line;
                        while ((line = br.readLine()) != null) {
                            text.append(line);
                            text.append('\n');
                        }
                        br.close();
                        String finalContent = imageHandler.replaceImageSrc(text.toString(), recipe.getId());
                        currentFile.delete();
                        fileHandler.writeToFile(finalContent, GlobalStaticVariables.FAVORITE_RECIPES,
                                GlobalStaticVariables.FAVORITE_RECIPE_PATH, recipe.getId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

    }


    private void setFirstRunVariable(boolean value) {
        if(db.getAllCategories() != null && db.getAllCategories().size() > 0){
            SharedPreferences.Editor editor = savedSettings.edit();
            editor.putBoolean("firstRun", value);
            editor.apply();
        }
    }

    private void setQuickFixVariable() {
            SharedPreferences.Editor editor = savedSettings.edit();
            editor.putBoolean("quickFix", false);
            editor.apply();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        activity.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = new Intent(activity, MainActivity.class);
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

    private boolean isQuickFix(){
        return savedSettings.getBoolean("quickFix", true);
    }

    private void clearApplicationData() {
        File cache = this.context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i(GlobalStaticVariables.LOG_TAG, "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                }
            }
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }

        assert dir != null;
        return dir.delete();
    }


}
