package hu.blogspot.limarapeksege.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.google.api.client.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.adapters.MainPageGridAdapter;
import hu.blogspot.limarapeksege.adapters.NavigationDrawerListAdapter;
import hu.blogspot.limarapeksege.adapters.items.DrawerListItem;
import hu.blogspot.limarapeksege.model.Category;
import hu.blogspot.limarapeksege.model.Recipe;
import hu.blogspot.limarapeksege.util.AnalyticsTracker;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.SqliteHelper;
import hu.blogspot.limarapeksege.util.handlers.file.FileHandler;

public class BaseActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ActionBarDrawerToggle left_actionBarDrawerToggle;
    private ActionBarDrawerToggle right_actionBarDrawerToggle;
    private AnalyticsTracker trackerApp;
    private static String currentClassName;
    private DrawerLayout drawerLayout;
    private SqliteHelper db;
    private Toolbar toolbar;
    private MainPageGridAdapter adapter;
    private StaggeredGridView mainGridView;
    private boolean doubleBackToExitPressedOnce = false;

    protected void onCreateDrawer(String currentClassName) {

        initToolBar();

        setDrawerContainerWidth(R.id.left_drawer_container);

        BaseActivity.currentClassName = currentClassName;
        db = SqliteHelper.getInstance(this);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        prepareLeftDrawer(drawerLayout);
        if(!GlobalStaticVariables.RECIPE_PAGE_CLASS.contains(currentClassName)){
            setDrawerContainerWidth(R.id.right_drawer_container);
            prepareRightDrawer(drawerLayout);
        }

        trackerApp = (AnalyticsTracker) getApplication();

    }

    private void prepareLeftDrawer(DrawerLayout drawerLayout){
        left_actionBarDrawerToggle = setActionBarDrawerToggle(drawerLayout);
        drawerLayout.addDrawerListener(left_actionBarDrawerToggle);
        ListView leftDrawerList = (ListView) findViewById(R.id.left_drawer);

        NavigationDrawerListAdapter leftDrawerAdapter = new NavigationDrawerListAdapter(this, getLeftDrawerListItems(), R.layout.custom_drawer_item);

        assert leftDrawerList != null;
        leftDrawerList.setAdapter(leftDrawerAdapter);
        leftDrawerList.setOnItemClickListener(this);
    }
    private void prepareRightDrawer(DrawerLayout drawerLayout){
        right_actionBarDrawerToggle = setActionBarDrawerToggle(drawerLayout);
        drawerLayout.addDrawerListener(right_actionBarDrawerToggle);
        ListView rightDrawerList = (ListView) findViewById(R.id.right_drawer);

        NavigationDrawerListAdapter rightDrawerAdapter = new NavigationDrawerListAdapter(this, getRightDrawerListItems(), R.layout.custom_drawer_item);

        assert rightDrawerList != null;
        rightDrawerList.setAdapter(rightDrawerAdapter);
        rightDrawerList.setOnItemClickListener(this);
    }

    public void initToolBar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbarTitle("Összes recept");

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawerLayout.openDrawer(findViewById(R.id.left_drawer_container));
                    }
                }

        );
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        left_actionBarDrawerToggle.syncState();
        if(!GlobalStaticVariables.RECIPE_PAGE_CLASS.contains(currentClassName)){
            right_actionBarDrawerToggle.syncState();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);

        prepareSearchMenu(menu);
        prepareMoreCategoriesMenu(menu);

        return true;
    }

    private void prepareMoreCategoriesMenu(Menu menu){

        menu.findItem(R.id.moreCategories).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                drawerLayout.openDrawer(findViewById(R.id.right_drawer_container));
                return false;
            }
        });

    }


    private void prepareSearchMenu(Menu menu) {
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchManager.setOnCancelListener(new SearchManager.OnCancelListener() {
            @Override
            public void onCancel() {
                Log.w(GlobalStaticVariables.LOG_TAG, "cancel");
            }
        });

        searchView.setSubmitButtonEnabled(true);

        searchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                trackerApp.sendTrackerEvent(getString(R.string.analytics_category_recipe_list), getString(R.string.analytics_use_of_search_bar));
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int colorId = ContextCompat.getColor(BaseActivity.this, R.color.dark_primary_color);
                int red = Color.red(colorId);
                int green = Color.green(colorId);
                int blue = Color.blue(colorId);

                toolbar.setBackgroundColor(Color.rgb(red, green, blue));
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    int colorId = ContextCompat.getColor(BaseActivity.this, R.color.dark_primary_color);
                    int red = Color.red(colorId);
                    int green = Color.green(colorId);
                    int blue = Color.blue(colorId);

                    toolbar.setBackgroundColor(Color.rgb(red, green, blue));
                }
            }
        });

        SearchView.OnQueryTextListener textListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                adapter.getFilter().filter(query);
                Log.w(GlobalStaticVariables.LOG_TAG, "onQueryTextSubmit " + query);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(BaseActivity.this.getCurrentFocus().getWindowToken(), 0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                mainGridView.setFilterText(newText);

                adapter.getFilter().filter(newText);
                Log.w(GlobalStaticVariables.LOG_TAG, "onQueryTextChange " + newText);

                return true;
            }


        };

        searchView.setOnQueryTextListener(textListener);
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
        if(!GlobalStaticVariables.RECIPE_PAGE_CLASS.contains(currentClassName)){
            right_actionBarDrawerToggle.onConfigurationChanged(newConfig);
        }
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
                trackerApp.sendTrackerEvent(getString(R.string.analytics_close_nav_drawer), BaseActivity.currentClassName);
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            public void onDrawerOpened(View drawerView) {
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

        if (parent.getId() == R.id.left_drawer) {
            Intent intent = null;
            Bundle sendData = new Bundle();
            Log.w(GlobalStaticVariables.LOG_TAG, "Left drawer");
            switch (drawerItemPosition) {
                case 0:
                    intent = new Intent(BaseActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    break;
                case 1:
                    setContentView(R.layout.main_page);
                    onCreateDrawer(GlobalStaticVariables.RECIPE_PAGE_CLASS);
                    setGridAdapter(collectRecipesBasedOnDirectory(GlobalStaticVariables.SAVED_RECIPE_PATH));
                    setToolbarTitle(getString(R.string.title_saved_recipes));
                    break;
                case 2:
                    setContentView(R.layout.main_page);
                    onCreateDrawer(GlobalStaticVariables.RECIPE_PAGE_CLASS);
                    setGridAdapter(collectRecipesBasedOnDirectory(GlobalStaticVariables.FAVORITE_RECIPE_PATH));
                    setToolbarTitle(getString(R.string.title_favorite_recipes));
                    break;
                case 3:
                    intent = new Intent(BaseActivity.this, LoafMakingActivity.class);
                    break;
                case 4:
                    intent = new Intent(BaseActivity.this, AboutActivity.class);
                    break;

                default:
                    intent = new Intent(BaseActivity.this, MainActivity.class);
                    break;
            }

            if (intent != null) {
                sendData.putInt("position", drawerItemPosition);
                intent.putExtras(sendData);

                trackerApp.sendTrackerEvent(getString(R.string.analytics_category_choose_nav_drawer), BaseActivity.currentClassName);
                startActivity(intent);
            }


        } else if (parent.getId() == R.id.right_drawer) {
            Log.w(GlobalStaticVariables.LOG_TAG, "Right drawer");

            DrawerListItem drawerListItem = (DrawerListItem) parent.getItemAtPosition(drawerItemPosition);
            Category selectedCategory = db.getCategoryById(drawerListItem.getItemId());

            setGridAdapter((ArrayList<Recipe>) db.getRecipesByCategoryID(selectedCategory.getId()));
            setToolbarTitle(selectedCategory.getName());

        }
        view.setSelected(true);
    }

    private List<DrawerListItem> getRightDrawerListItems() {

        List<DrawerListItem> drawerListItems = new ArrayList<>();
        SqliteHelper db = SqliteHelper.getInstance(BaseActivity.this);

        List<Category> categoryList = db.getAllCategories();

        TypedArray icons = this.getResources().obtainTypedArray(
                R.array.category_icons);

        for (int i = 0; i < categoryList.size(); i++) {
            DrawerListItem drawerListItem = new DrawerListItem(categoryList.get(i).getName(), icons.getResourceId(i, -1), categoryList.get(i).getId());
            drawerListItems.add(drawerListItem);
        }

        return drawerListItems;

    }

    private List<DrawerListItem> getLeftDrawerListItems() {

        DrawerListItem drawerListItemHome = new DrawerListItem(getString(R.string.nav_drawer_item_kezdolap), R.drawable.ic_menu_home, 0);
        DrawerListItem drawerListItemAbout = new DrawerListItem(getString(R.string.nav_drawer_item_about), R.drawable.ic_info_black_24dp, 1);
        DrawerListItem drawerListItemSavedRecipes = new DrawerListItem("Lementett receptek", R.drawable.ic_sd_card_black_24dp, 2);
        DrawerListItem drawerListItemFavoriteRecipes = new DrawerListItem("Kedvenc receptek", R.drawable.ic_favorite_black_24dp, 3);
        DrawerListItem drawerListItemLoafMaking = new DrawerListItem("Vekni formázása", R.drawable.loaf_icon, 4);

        List<DrawerListItem> items = new ArrayList<>();
        items.add(drawerListItemHome);
        items.add(drawerListItemSavedRecipes);
        items.add(drawerListItemFavoriteRecipes);
        items.add(drawerListItemLoafMaking);
        items.add(drawerListItemAbout);

        return items;

    }


    protected void setGridAdapter(ArrayList<Recipe> recipeList) {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View headerView = layoutInflater.inflate(R.layout.grid_header_layout, null, false);
        mainGridView = (StaggeredGridView) findViewById(R.id.mainPageGridView);

        assert mainGridView != null;

        adapter = new MainPageGridAdapter(this, R.layout.main_page_grid_item, recipeList);
        adapter.notifyDataSetChanged();

        mainGridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {


                int colorId = ContextCompat.getColor(BaseActivity.this, R.color.dark_primary_color);
                int red = Color.red(colorId);
                int green = Color.green(colorId);
                int blue = Color.blue(colorId);

                int scrollPos = Math.abs(((StaggeredGridView) view).getDistanceToTop());
                float bound = 1;
                if (view.getChildAt(0) != null) {
                    bound = view.getChildAt(0).getHeight();
                }
                float ratio = (float) (scrollPos / bound);

                if (toolbar.getMenu().hasVisibleItems() && toolbar.getMenu().getItem(0).getActionView().hasFocus()) {
                    ratio = 1;
                }

                if (scrollPos < bound) {
                    toolbar.setBackgroundColor(Color.argb((int) (ratio * 255), red, green, blue));
                }

            }

        });


        if (mainGridView.getHeaderViewsCount() == 0) {
            mainGridView.addHeaderView(headerView);
        }


        mainGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    Recipe selectedRecipe = (Recipe) parent.getItemAtPosition(position);
                    startNewActivity(selectedRecipe);
                }
            }

        });

        mainGridView.setAdapter(adapter);
        drawerLayout.closeDrawers();

    }

    private ArrayList<Recipe> collectRecipesBasedOnDirectory(String directoryPath) {

        FileHandler fileHandler = new FileHandler();
        ArrayList<Recipe> recipeList = new ArrayList<>();

        for (String fileName : fileHandler.getFileNamesFromDirectory(directoryPath)) {
            recipeList.add(db.getRecipeById(fileName));
        }

        return recipeList;

    }

    private void startNewActivity(Recipe recipe) {
        Bundle bundleData = new Bundle();
        Log.w(GlobalStaticVariables.LOG_TAG, "Selected recipe " + recipe.getRecipeName());

        Class<?> selectedRecipePage = null;

        try {
            selectedRecipePage = Class.forName(GlobalStaticVariables.RECIPE_PAGE_CLASS);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Intent openRecipe = new Intent(BaseActivity.this, selectedRecipePage);

        if (toolbar.getTitle().equals(getString(R.string.title_saved_recipes))) {
            bundleData.putBoolean("saved", true);
        } else if (toolbar.getTitle().equals(getString(R.string.title_favorite_recipes))) {
            bundleData.putBoolean("favorite", true);
        } else {
            bundleData.putBoolean("online", true);
        }

        bundleData.putString("id", recipe.getId());

        openRecipe.putExtras(bundleData);
        if ((bundleData.getBoolean("online") && isNetworkAvailable())
                || bundleData.getBoolean("saved")
                || bundleData.getBoolean("favorite")) {
            Log.w(GlobalStaticVariables.LOG_TAG, "new activity starting");
            trackerApp.sendTrackerEvent(getString(R.string.analytics_category_recipe), getString(R.string.analytics_open_recipe));
            startActivity(openRecipe);
        } else {
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
        }

    }

    protected void setToolbarTitle(String title) {
        toolbar.setTitle(title);
    }

    protected boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Kilépéshez nyomja meg még egyszer a VISSZA gombot", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}
