package hu.blogspot.limarapeksege.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.analytics.Tracker;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.util.AnalyticsTracker;

public class AboutActivity extends Activity {

    private AnalyticsTracker trackerApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setFullScreen();

//        DrawerListItem drawerListItemHome = new DrawerListItem(getString(R.string.nav_drawer_item_kezdolap), R.drawable.ic_menu_home);
//        DrawerListItem drawerListItemAbout = new DrawerListItem(getString(R.string.nav_drawer_item_about), R.drawable.about_icon);
//        List<DrawerListItem> items = new ArrayList<>();
//        items.add(drawerListItemHome);
//        items.add(drawerListItemAbout);
//
//        super.onCreateDrawer(items, getLocalClassName());

        trackerApp = (AnalyticsTracker) getApplication();
        trackerApp.sendScreen(getString(R.string.analytics_about));

        setContentView(R.layout.activity_about);

    }

    private void setFullScreen() {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            ActionBar actionBar = getActionBar();
            actionBar.hide();
        }
    }
}
