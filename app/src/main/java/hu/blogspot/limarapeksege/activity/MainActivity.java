package hu.blogspot.limarapeksege.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.adapters.MainPageGridAdapter;
import hu.blogspot.limarapeksege.adapters.items.DrawerListItem;
import hu.blogspot.limarapeksege.model.Recipe;
import hu.blogspot.limarapeksege.util.AnalyticsTracker;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.SqliteHelper;
import in.srain.cube.views.GridViewWithHeaderAndFooter;

@SuppressLint("NewApi")
public class MainActivity extends BaseActivity{

    private AnalyticsTracker trackerApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

//        setFullScreen();

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

        trackerApp = (AnalyticsTracker) getApplication();

        prepareMainView();

    }

    private void prepareMainView() {

        SqliteHelper db = SqliteHelper.getInstance(this);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View headerView = layoutInflater.inflate(R.layout.grid_header_layout, null, false);
        GridViewWithHeaderAndFooter mainGridView = (GridViewWithHeaderAndFooter) findViewById(R.id.mainPageGridView);

        MainPageGridAdapter adapter = new MainPageGridAdapter(this, R.layout.main_page_grid_item, (ArrayList<Recipe>) db.getAllRecipes());
        mainGridView.addHeaderView(headerView);
        mainGridView.setAdapter(adapter);
        mainGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Recipe selectedRecipe = (Recipe) parent.getItemAtPosition(position+2);
                startNewActivity(selectedRecipe);
            }

        });

        db.closeDatabase();
    }

    private void startNewActivity(Recipe recipe) {
        Bundle b2 = new Bundle();
        Log.w(GlobalStaticVariables.LOG_TAG, "Selected recipe " + recipe.getRecipeName());

        b2.putString("href", recipe.getRecipeURL());
        Class<?> selectedRecipePage = null;

        try {
            selectedRecipePage = Class.forName(GlobalStaticVariables.RECIPE_PAGE_CLASS);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Intent openRecipe = new Intent(MainActivity.this, selectedRecipePage);
        b2.putString("name", recipe.getRecipeName());

        openRecipe.putExtras(b2);
        Log.w(GlobalStaticVariables.LOG_TAG, "new activity starting");
        trackerApp.sendTrackerEvent(getString(R.string.analytics_category_recipe), getString(R.string.analytics_open_recipe));
        startActivity(openRecipe);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    private void setFullScreen() {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            ActionBar actionBar = getActionBar();
            assert actionBar != null;
            actionBar.hide();
        }
    }

}
