package hu.blogspot.limarapeksege.util;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import hu.blogspot.limarapeksege.R;

public class AnalyticsTracker extends Application {

    private Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    public void sendTrackerEvent(String eventCategoryName, String eventActionName){
        getDefaultTracker().send(new HitBuilders.EventBuilder()
                .setCategory(eventCategoryName)
                .setAction(eventActionName)
                .build());
    }

    public void sendScreen(String screenName){
        getDefaultTracker().setScreenName(screenName);
        getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }


}
