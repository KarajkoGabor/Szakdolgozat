package hu.blogspot.limarapeksege.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.adapters.items.DrawerListItem;
import hu.blogspot.limarapeksege.util.AnalyticsTracker;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.XmlParser;

@SuppressLint("NewApi")
public class MainPage extends BaseActivity implements OnClickListener {

    private AnalyticsTracker trackerApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        DrawerListItem drawerListItemHome = new DrawerListItem(getString(R.string.nav_drawer_item_kezdolap), R.drawable.ic_menu_home, 0);
        DrawerListItem drawerListItemAbout = new DrawerListItem(getString(R.string.nav_drawer_item_about), R.drawable.ic_info_black_24dp, 1);
        DrawerListItem drawerListItemSavedRecipes = new DrawerListItem("Lementett receptek", R.drawable.ic_sd_card_black_24dp, 2);
        DrawerListItem drawerListItemFavoriteRecipes = new DrawerListItem("Kedvenc receptek", R.drawable.ic_favorite_black_24dp, 3);
        DrawerListItem drawerListItemFindRecipes = new DrawerListItem("Recept keresése", R.drawable.ic_search_black_24dp, 4);
        DrawerListItem drawerListItemLoafMaking = new DrawerListItem("Vekni formázása", R.drawable.loaf_icon, 5);

        List<DrawerListItem> items = new ArrayList<>();
        items.add(drawerListItemHome);
        items.add(drawerListItemSavedRecipes);
        items.add(drawerListItemFavoriteRecipes);
        items.add(drawerListItemFindRecipes);
        items.add(drawerListItemLoafMaking);
        items.add(drawerListItemAbout);

        super.onCreateDrawer(items, getLocalClassName());
        List<String> mainMenuList; // men� lista

        mainMenuList = setMainMenuList();
        setTextViews(mainMenuList);

        trackerApp = (AnalyticsTracker) getApplication();

    }

    private void setTextViews(List<String> mainMenuList) {
        TextView onlineRecipes = (TextView) findViewById(R.id.mainTextOnline);
        TextView savedRecipes = (TextView) findViewById(R.id.mainTextSaved);
        TextView favoriteRecipes = (TextView) findViewById(R.id.mainTextFavorites);
        TextView loafMaking = (TextView) findViewById(R.id.mainTextLoaf);
        TextView searchRecipes = (TextView) findViewById(R.id.mainTextSearch);

        onlineRecipes.setOnClickListener(this);
        onlineRecipes.setText(mainMenuList.get(0));
        savedRecipes.setOnClickListener(this);
        savedRecipes.setText(mainMenuList.get(1));
        favoriteRecipes.setOnClickListener(this);
        favoriteRecipes.setText(mainMenuList.get(2));
        loafMaking.setOnClickListener(this);
        loafMaking.setText(mainMenuList.get(3));
        searchRecipes.setOnClickListener(this);
        searchRecipes.setText(mainMenuList.get(4));

    }

    public void onClick(View v) {

        try {
            switch (v.getId()) {

                case R.id.mainTextOnline:
                    if (!isNetworkAvailable()) {
                        throw new Exception();
                    } else {
                        startNewActivity(
                                GlobalStaticVariables.RECIPE_CATEGORY_CLASS, 0);
                    }
                    break;
                case R.id.mainTextSaved:
                    startNewActivity(GlobalStaticVariables.SAVED_RECIPES_CLASS, 1);
                    break;
                case R.id.mainTextFavorites:
                    startNewActivity(GlobalStaticVariables.SAVED_RECIPES_CLASS, 2);
                    break;
                case R.id.mainTextLoaf:
                    startNewActivity(GlobalStaticVariables.LOAF_MAKING_CLASS, 3);
                    break;
                case R.id.mainTextSearch:
                    if (!isNetworkAvailable()) {
                        throw new Exception();
                    } else {
                        startNewActivity(GlobalStaticVariables.RECIPE_SEARCH_CLASS,
                                5);
                    }
                    break;

            }

        } catch (Exception e) {
            Toast.makeText(this, R.string.no_connection,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void startNewActivity(String className, int position) {
        Bundle sendData = new Bundle();
        Class<?> newClass = null;
        try {
            newClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Intent openRecipeCategory = new Intent(this, newClass);
        sendData.putInt("position", position);

        openRecipeCategory.putExtras(sendData);

        trackerApp.sendTrackerEvent(getString(R.string.analytics_category_main_menu_item), className);

        this.startActivity(openRecipeCategory);
    }

    private List<String> setMainMenuList() {
        ArrayList<? extends Object> mainMenuList = new ArrayList<String>();
        try {
            XmlParser parser = new XmlParser();
            XmlPullParser xpp = getResources().getXml(R.xml.mainmenulist);
            mainMenuList = parser.parseXml(xpp, "main_menu");
        } catch (Throwable t) {
            Toast.makeText(this, "Request failed: " + t.toString(),
                    Toast.LENGTH_LONG).show();
        }

        return (List<String>) mainMenuList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.activity_main_page, menu);

        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() { // ellen�rizz�k van-e internet el�r�s
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
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
