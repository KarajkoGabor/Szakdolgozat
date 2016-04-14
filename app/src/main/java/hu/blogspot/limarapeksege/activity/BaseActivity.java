package hu.blogspot.limarapeksege.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.adapters.NavigationDrawerListAdapter;
import hu.blogspot.limarapeksege.adapters.items.DrawerListItem;
import hu.blogspot.limarapeksege.util.AnalyticsTracker;

public class BaseActivity extends Activity {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private AnalyticsTracker trackerApp;
    private static String currentClassName;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    protected void onCreateDrawer(List<DrawerListItem> items, String currentClassName) {

        setDrawerContainerWidth();

        BaseActivity.currentClassName = currentClassName;

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle((Activity) this, drawerLayout, 0, 0)
        {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            public void onDrawerClosed(View view)
            {
                getActionBar().setTitle(R.string.limara);
                trackerApp.sendTrackerEvent(getString(R.string.analytics_close_nav_drawer), BaseActivity.currentClassName);
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            public void onDrawerOpened(View drawerView)
            {
                getActionBar().setTitle(R.string.limara);
                trackerApp.sendTrackerEvent(getString(R.string.analytics_open_nav_drawer), BaseActivity.currentClassName);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                if(newState == DrawerLayout.STATE_DRAGGING){
                    trackerApp.sendTrackerEvent(getString(R.string.analytics_slide_nav_drawer), BaseActivity.currentClassName);
                }
            }

        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        trackerApp = (AnalyticsTracker) getApplication();

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        ListView drawerList = (ListView) findViewById(R.id.left_drawer);
        NavigationDrawerListAdapter adapter = new NavigationDrawerListAdapter(this, items, R.layout.custom_drawer_item);
        drawerList.setAdapter(adapter);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                Intent intent = null;
                switch (pos) {
                    case 0:
                        intent = new Intent(BaseActivity.this, MainPage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        break;
                    case 1:
                        intent = new Intent(BaseActivity.this, AboutActivity.class);
                        break;

                    default:
                        intent = new Intent(BaseActivity.this, MainPage.class); // Activity_0 as default
                        break;
                }

                trackerApp.sendTrackerEvent(getString(R.string.analytics_category_choose_nav_drawer), BaseActivity.currentClassName);
                startActivity(intent);
            }
        });



    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
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
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void setDrawerContainerWidth(){
        DisplayMetrics metrics = new DisplayMetrics();
        LinearLayout drawerContainer  = (LinearLayout) findViewById(R.id.drawer_container);
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) drawerContainer.getLayoutParams();
        params.width = (int) (metrics.widthPixels*0.7);
        drawerContainer.setLayoutParams(params);
    }

}
