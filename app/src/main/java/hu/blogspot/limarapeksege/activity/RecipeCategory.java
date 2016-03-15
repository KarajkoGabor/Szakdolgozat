package hu.blogspot.limarapeksege.activity;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.asyncs.AsyncRecipeCategoryClass;
import hu.blogspot.limarapeksege.model.Category;
import hu.blogspot.limarapeksege.util.AnalyticsTracker;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.RecipeCategoryGridMaker;
import hu.blogspot.limarapeksege.util.SqliteHelper;
import hu.blogspot.limarapeksege.util.handlers.recipe.RecipeActionsHandler;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class RecipeCategory extends Activity {

    private ArrayList<String> mainMenuList = new ArrayList<String>();
    private GridView grid;
    private AnalyticsTracker trackerApp;
    private RecipeActionsHandler util;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_category);
        SqliteHelper db = SqliteHelper.getInstance(RecipeCategory.this);
        RecipeCategoryGridMaker categoryGridMaker = new RecipeCategoryGridMaker(RecipeCategory.this);
        util = new RecipeActionsHandler(this);

        trackerApp = (AnalyticsTracker) getApplication();
        List<Category> categoryList = db.getAllCategories();

        try {

            if (!categoryList.isEmpty()) { // ha m�r egyszer
                // elmentett�k
                mainMenuList.clear();
                Log.w(GlobalStaticVariables.LOG_TAG, categoryList.size() + "");

                for (int i = 0; i < categoryList.size(); i++) {
                    Log.w(GlobalStaticVariables.LOG_TAG, categoryList.get(i).getName());
                    mainMenuList.add(categoryList.get(i).getName());
                }

                Log.w(GlobalStaticVariables.LOG_TAG, "not async");
            } else {
                db.deleteCategoryTable();
                // db.deleteRecipeTable();
                AsyncRecipeCategoryClass asyncCategory = new AsyncRecipeCategoryClass(
                        RecipeCategory.this);
                asyncCategory.execute(RecipeCategory.this,
                        GlobalStaticVariables.URL_TARTALOM, categoryGridMaker,
                        grid);
                Log.w(GlobalStaticVariables.LOG_TAG, "async");
            }

            grid = categoryGridMaker.setGridItems(util.stringListSorter(mainMenuList));
            db.closeDatabase();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        grid.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                try {
                    if (!isNetworkAvailable()) {
                        throw new Exception();
                    } else {
                        startNewActivity(
                                GlobalStaticVariables.RECIPE_LIST_CLASS,
                                position);
                    }

                } catch (Exception e) {
                    Toast.makeText(RecipeCategory.this, R.string.no_connection,
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        trackerApp.sendScreen(getString(R.string.analytics_screen_category_list));

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private void startNewActivity(String className, int position)
            throws ClassNotFoundException {
        Class<?> recipeList = null;
        recipeList = Class.forName(className);
        Bundle bundle = new Bundle();

        Intent openReceptList = new Intent(RecipeCategory.this, recipeList);
        bundle.putInt("position", position);
        bundle.putString("category", mainMenuList.get(position));// elk�ldj�k
        // a
        // kateg�ri�t

        openReceptList.putExtras(bundle);
        trackerApp.sendTrackerEvent(getString(R.string.analytics_category_recipe_category), mainMenuList.get(position));
        startActivity(openReceptList);
    }

}
