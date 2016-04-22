package hu.blogspot.limarapeksege.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Collections;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.adapters.MainPageGridAdapter;
import hu.blogspot.limarapeksege.model.Recipe;
import hu.blogspot.limarapeksege.util.AnalyticsTracker;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.SqliteHelper;

@SuppressLint("NewApi")
public class MainActivity extends BaseActivity{

    private AnalyticsTracker trackerApp;
    private MainPageGridAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            setContentView(R.layout.main_page);
            super.onCreateDrawer(getLocalClassName());

            trackerApp = (AnalyticsTracker) getApplication();
            prepareMainView();

    }

    private void prepareMainView() {

        SqliteHelper db = SqliteHelper.getInstance(this);

        ArrayList<Recipe> allRecipes = (ArrayList<Recipe>) db.getAllRecipes();

        Collections.shuffle(allRecipes);

        super.setGridAdapter(allRecipes);

        db.closeDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        trackerApp.sendScreen(getString(R.string.analytics_screen_main_page_screen));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.w(GlobalStaticVariables.LOG_TAG, "onActivityResult session");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
