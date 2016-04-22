package hu.blogspot.limarapeksege.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.adapters.LoafMakingPageAdapter;
import hu.blogspot.limarapeksege.util.AnalyticsTracker;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.XmlParser;

public class LoafMakingActivity extends Activity {

    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    int[] pictures;
    ArrayList<String> texts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loafmaking_main);

        AnalyticsTracker trackerApp = (AnalyticsTracker) getApplication();

        pictures = new int[]{R.drawable.loaf0, R.drawable.loaf1,
                R.drawable.loaf2, R.drawable.loaf3, R.drawable.loaf4,
                R.drawable.loaf5, R.drawable.loaf7, R.drawable.loaf9,
                R.drawable.loaf10};

        texts = new ArrayList<>();
        XmlPullParser xpp = getResources().getXml(R.xml.loafmaking);
        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("description")) {
                        if (xpp.next() == XmlPullParser.TEXT) {
                            texts.add(xpp.getText());
                            Log.w(GlobalStaticVariables.LOG_TAG, xpp.getText());
                            xpp.nextTag();
                        }

                    }
                }

                xpp.next();
            }

        } catch (XmlPullParserException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        viewPager = (ViewPager) findViewById(R.id.pager);

        pagerAdapter = new LoafMakingPageAdapter(this, pictures, texts);

        viewPager.setAdapter(pagerAdapter);

        trackerApp.sendScreen(GlobalStaticVariables.LOAF_MAKING_CLASS);

    }

}
