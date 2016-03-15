package hu.blogspot.limarapeksege.activity;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.adapters.RecipeCategoryListAdapter;
import hu.blogspot.limarapeksege.asyncs.AsyncRecipeListClass;
import hu.blogspot.limarapeksege.model.Category;
import hu.blogspot.limarapeksege.model.Recipe;
import hu.blogspot.limarapeksege.util.AnalyticsTracker;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.SqliteHelper;
import hu.blogspot.limarapeksege.util.handlers.recipe.RecipeActionsHandler;

public class RecipeList extends ListActivity {

    private int categoryPos;

    private RecipeActionsHandler util;
    private SqliteHelper db;
    private Category currentCategory;
    private AsyncRecipeListClass asyncRecipeList;
    private AnalyticsTracker trackerApp;
    private ArrayAdapter<String> adapter;
    private ListView lv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        trimCache(this);
        trackerApp = (AnalyticsTracker) getApplication();

        util = new RecipeActionsHandler(RecipeList.this);

        db = SqliteHelper.getInstance(RecipeList.this);

        lv = (ListView) findViewById(android.R.id.list);
//        lv.setTextFilterEnabled(true);
        Bundle extras = getIntent().getExtras();

        String category = extras.getString("category");// beolvassuk milyen
        categoryPos = extras.getInt("position");
        currentCategory = db.getCategoryByName(category);

        setTitle(category);

        if (currentCategory.isRecipesDownloaded()) {
            setListAdapter(currentCategory);
        } else {
            setListAdapterUsingAsync(currentCategory);
        }
        db.closeDatabase();

        lv.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                try {
                    if (isNetworkAvailable() == false) {
                        throw new Exception();
                    } else {

                        String currentRecipeName = lv.getItemAtPosition(position).toString();

                        startNewActivity(
                                GlobalStaticVariables.RECIPE_PAGE_CLASS,
                                currentRecipeName);

                    }

                } catch (Exception e) {
                    Toast.makeText(RecipeList.this, R.string.no_connection,
                            Toast.LENGTH_LONG).show();
                }
            }

        });

        trackerApp.sendScreen(getString(R.string.analytics_screen_recipe_list));

    }

    private void setAdapter(ArrayList<String> recipes) {

        TypedArray icons = getResources().obtainTypedArray(
                R.array.category_icons);
        Bitmap tempIcon = BitmapFactory.decodeResource(getResources(),
                icons.getResourceId(categoryPos, -1));
        adapter = new RecipeCategoryListAdapter(this,
                R.layout.list_row_category, recipes, tempIcon);
        setListAdapter(adapter);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    private void startNewActivity(String className, String recipeName) {
        Bundle b2 = new Bundle();
        Recipe selectedRecipe;
        selectedRecipe = db.getRecipeByName(recipeName);
        Log.w(GlobalStaticVariables.LOG_TAG, selectedRecipe.getRecipeName());

        String recipePage = selectedRecipe.getRecipeURL();
        b2.putString("href", recipePage);
        Log.w(GlobalStaticVariables.LOG_TAG, recipePage);

        Class<?> selectedRecipePage = null;

        try {
            selectedRecipePage = Class.forName(className);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Intent openRecipe = new Intent(RecipeList.this, selectedRecipePage);
        b2.putString("name", selectedRecipe.getRecipeName());

        openRecipe.putExtras(b2);
        Log.w(GlobalStaticVariables.LOG_TAG, "new activity starting");
        db.closeDatabase();
        trackerApp.sendTrackerEvent(getString(R.string.analytics_category_recipe), getString(R.string.analytics_open_recipe));
        trackerApp.sendTrackerEvent(getString(R.string.analytics_category_recipe), selectedRecipe.getRecipeName());
        startActivity(openRecipe);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recipe_list, menu);

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

        searchView.setSubmitButtonEnabled(false);

        SearchView.OnQueryTextListener textListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                adapter.getFilter().filter(query);
                Log.w(GlobalStaticVariables.LOG_TAG, "onQueryTextSubmit " + query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

//                lv.setFilterText(newText);

                adapter.getFilter().filter(newText);
                Log.w(GlobalStaticVariables.LOG_TAG, "onQueryTextChange " + newText);

                return true;
            }


        };

        searchView.setOnQueryTextListener(textListener);

        return super.onCreateOptionsMenu(menu);
    }

    private void setListAdapter(Category currentCategory) {
        try {
            Log.w(GlobalStaticVariables.LOG_TAG, "not async");
            ArrayList<String> recipeTitles = new ArrayList<String>();
            List<Recipe> recipeList = db.getRecipesByCategoryID(currentCategory.getId());

            Log.w(GlobalStaticVariables.LOG_TAG, "size" + recipeList.size() + "");
            for (Recipe recipe : recipeList) {
                recipeTitles.add(recipe.getRecipeName());
            }
            Log.w(GlobalStaticVariables.LOG_TAG, "size" + recipeTitles.size() + "");
            setAdapter(util.stringListSorter(recipeTitles));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void setListAdapterUsingAsync(Category currentCategory) {
        File storageReceipeListDirectory = new File(
                Environment.getExternalStorageDirectory()
                        + GlobalStaticVariables.MAIN_DIRECTORY,
                GlobalStaticVariables.RECIPE_LIST_DIRECTORY);
        Log.w(GlobalStaticVariables.LOG_TAG, "async");
        storageReceipeListDirectory.mkdirs();
        asyncRecipeList = new AsyncRecipeListClass(RecipeList.this,
                RecipeList.this, getString(R.string.recipes_download),
                categoryPos);
        asyncRecipeList.execute(currentCategory.getName(),
                GlobalStaticVariables.URL_TARTALOM,
                currentCategory.getId(), RecipeList.this);
    }

}
