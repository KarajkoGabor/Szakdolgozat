package hu.blogspot.limarapeksege.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.util.AnalyticsTracker;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        setFullScreen();

        AnalyticsTracker trackerApp = (AnalyticsTracker) getApplication();
        trackerApp.sendScreen(getString(R.string.analytics_about));

        setContentView(R.layout.activity_about);

        TextView textViewVersionNumber = (TextView) findViewById(R.id.version_number);

        PackageInfo pInfo = null;
        String version = "0.0";
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version = pInfo.versionName;

        textViewVersionNumber.setText("Verzió szám: " + version);

    }

}
