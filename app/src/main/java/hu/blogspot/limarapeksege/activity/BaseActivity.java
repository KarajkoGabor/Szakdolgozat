package hu.blogspot.limarapeksege.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.jsoup.Connection;

import java.util.ArrayList;
import java.util.List;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.adapters.NavigationDrawerListAdapter;
import hu.blogspot.limarapeksege.adapters.items.DrawerListItem;
import hu.blogspot.limarapeksege.model.Category;
import hu.blogspot.limarapeksege.util.AnalyticsTracker;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.SqliteHelper;
import hu.blogspot.limarapeksege.util.handlers.recipe.RecipeActionsHandler;

public class BaseActivity extends Activity implements AdapterView.OnItemClickListener {

    private ActionBarDrawerToggle left_actionBarDrawerToggle;
    private ActionBarDrawerToggle right_actionBarDrawerToggle;
    private AnalyticsTracker trackerApp;
    private static String currentClassName;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    protected void onCreateDrawer(List<DrawerListItem> leftDrawerItems, String currentClassName) {

        setDrawerContainerWidth(R.id.left_drawer_container);
        setDrawerContainerWidth(R.id.right_drawer_container);

        BaseActivity.currentClassName = currentClassName;

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        left_actionBarDrawerToggle = setActionBarDrawerToggle(drawerLayout);
        right_actionBarDrawerToggle = setActionBarDrawerToggle(drawerLayout);

        drawerLayout.addDrawerListener(left_actionBarDrawerToggle);
        drawerLayout.addDrawerListener(right_actionBarDrawerToggle);

        trackerApp = (AnalyticsTracker) getApplication();

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        ListView leftDrawerList = (ListView) findViewById(R.id.left_drawer);
        ListView rightDrawerList = (ListView) findViewById(R.id.right_drawer);

        NavigationDrawerListAdapter leftDrawerAdapter = new NavigationDrawerListAdapter(this, leftDrawerItems, R.layout.custom_drawer_item);
        NavigationDrawerListAdapter rightDrawerAdapter = new NavigationDrawerListAdapter(this, getRightDrawerListItems(), R.layout.custom_drawer_item);

        leftDrawerList.setAdapter(leftDrawerAdapter);
        leftDrawerList.setOnItemClickListener(this);

        rightDrawerList.setAdapter(rightDrawerAdapter);
        rightDrawerList.setOnItemClickListener(this);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        left_actionBarDrawerToggle.syncState();
        right_actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return right_actionBarDrawerToggle.onOptionsItemSelected(item) || left_actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        left_actionBarDrawerToggle.onConfigurationChanged(newConfig);
        right_actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void setDrawerContainerWidth(int viewId) {
        DisplayMetrics metrics = new DisplayMetrics();
        LinearLayout drawerContainer = (LinearLayout) findViewById(viewId);
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) drawerContainer.getLayoutParams();
        params.width = (int) (metrics.widthPixels * 0.7);
        drawerContainer.setLayoutParams(params);
    }

    private ActionBarDrawerToggle setActionBarDrawerToggle(DrawerLayout drawerLayout) {
        return new ActionBarDrawerToggle((Activity) this, drawerLayout, 0, 0) {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(R.string.limara);
                trackerApp.sendTrackerEvent(getString(R.string.analytics_close_nav_drawer), BaseActivity.currentClassName);
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(R.string.limara);
                trackerApp.sendTrackerEvent(getString(R.string.analytics_open_nav_drawer), BaseActivity.currentClassName);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                if (newState == DrawerLayout.STATE_DRAGGING) {
                    trackerApp.sendTrackerEvent(getString(R.string.analytics_slide_nav_drawer), BaseActivity.currentClassName);
                }
            }

        };
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int drawerItemPosition, long id) {
        Intent intent = null;
        Bundle sendData = new Bundle();
        if(parent.getId() == R.id.left_drawer){
            Log.w(GlobalStaticVariables.LOG_TAG, "Left drawer");
            switch (drawerItemPosition) {
                case 0:
                    intent = new Intent(BaseActivity.this, MainPage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    break;
                case 1:
                    intent = new Intent(BaseActivity.this, SavedRecipes.class);
                    break;
                case 2:
                    intent = new Intent(BaseActivity.this, SavedRecipes.class);
                    break;
                case 3:
                    intent = new Intent(BaseActivity.this, RecipeSearch.class);
                    break;
                case 4:
                    intent = new Intent(BaseActivity.this, LoafMakingActivity.class);
                    break;
                case 5:
                    intent = new Intent(BaseActivity.this, AboutActivity.class);
                    break;

                default:
                    intent = new Intent(BaseActivity.this, MainPage.class); // MainPage as default
                    break;
            }
        }else if(parent.getId() == R.id.right_drawer){
            Log.w(GlobalStaticVariables.LOG_TAG, "Right drawer");
            switch (drawerItemPosition) {
                case 0:
                    intent = new Intent(BaseActivity.this, MainPage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    break;
                case 1:
                    intent = new Intent(BaseActivity.this, AboutActivity.class);
                    break;

                default:
                    intent = new Intent(BaseActivity.this, MainPage.class); // MainPage as default
                    break;
            }

        }

        assert intent != null;
        sendData.putInt("position", drawerItemPosition);
        intent.putExtras(sendData);

        trackerApp.sendTrackerEvent(getString(R.string.analytics_category_choose_nav_drawer), BaseActivity.currentClassName);
        startActivity(intent);
    }

    private List<DrawerListItem> getRightDrawerListItems(){

        List<DrawerListItem> drawerListItems = new ArrayList<>();
        SqliteHelper db = SqliteHelper.getInstance(BaseActivity.this);

        List<Category> categoryList = db.getAllCategories();

        TypedArray icons = this.getResources().obtainTypedArray(
                R.array.category_icons);

        for(int i = 0; i < categoryList.size(); i++){
            DrawerListItem drawerListItem = new DrawerListItem(categoryList.get(i).getName(), icons.getResourceId(i, -1));
            drawerListItems.add(drawerListItem);
        }

        return drawerListItems;

    }

}
