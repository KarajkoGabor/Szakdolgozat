package hu.blogspot.limarapeksege.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ListView drawerList;
    private NavigationDrawerListAdapter adapter;
    private Tracker tracker;
    private static String currentClassName;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
    protected void onCreateDrawer(List<DrawerListItem> items, String currentClassName) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_base2);

        this.currentClassName = currentClassName;

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle((Activity) this, drawerLayout, 0, 0)
        {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            public void onDrawerClosed(View view)
            {
                getActionBar().setTitle(R.string.limara);
                sendTrackEvent(getString(R.string.analytics_close_nav_drawer));
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            public void onDrawerOpened(View drawerView)
            {
                getActionBar().setTitle(R.string.limara);
                sendTrackEvent(getString(R.string.analytics_open_nav_drawer));
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                if(newState == DrawerLayout.STATE_DRAGGING){
                    sendTrackEvent(getString(R.string.analytics_slide_nav_drawer));
                }
            }

        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        AnalyticsTracker trackerApp = (AnalyticsTracker) getApplication();
        tracker = trackerApp.getDefaultTracker();

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

//        layers = getResources().getStringArray(R.array.nav_drawer_items);
        drawerList = (ListView) findViewById(R.id.left_drawer);
//        View header = getLayoutInflater().inflate(R.layout.title, null);
//        drawerList.addHeaderView(header, null, false);
//        DrawerListItem drawerListItem = new DrawerListItem("Kezd?lap", R.drawable.ic_menu_home);
//        List<DrawerListItem> items = new ArrayList<>();
//        items.add(drawerListItem);
        adapter = new NavigationDrawerListAdapter(this, items, R.layout.custom_drawer_item);
        drawerList.setAdapter(adapter);
//        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.custom_drawer_item,
//                layers));
//        View footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
//                R.layout.drawer_list_footer, null, false);
//        drawerList.addFooterView(footerView);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                Intent intent = null;
                switch(pos) {
                    case 0:
                        intent = new Intent(BaseActivity.this, MainPage.class);
                        break;
                    case 1:
                        intent = new Intent(BaseActivity.this, RecipeCategory.class);
                        break;
                    case 4:
                        intent = new Intent(BaseActivity.this, RecipeSearch.class);
                        break;

                    default :
                        intent = new Intent(BaseActivity.this, MainPage.class); // Activity_0 as default
                        break;
                }

                sendTrackEvent(getString(R.string.analytics_choose_nav_drawer));
                startActivity(intent);
            }
        });



    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        actionBarDrawerToggle.syncState();
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
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void sendTrackEvent(String actionName){
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(BaseActivity.currentClassName)
                .setAction(actionName)
                .build());
    }

}
