package hu.blogspot.limarapeksege.activity;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.model.Recipe;
import hu.blogspot.limarapeksege.util.AnalyticsTracker;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.SqliteHelper;
import hu.blogspot.limarapeksege.util.handlers.recipe.RecipeActionsHandler;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class RecipeSearch extends ListActivity {

    private SqliteHelper db;
    private ArrayAdapter<String> adapter;
    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_search);

        final ListView list = (ListView) findViewById(android.R.id.list);
        final EditText input = (EditText) findViewById(R.id.search_field);

        AnalyticsTracker trackerApp = (AnalyticsTracker) getApplication();
        tracker = trackerApp.getDefaultTracker();

        Button delete_filter = (Button) findViewById(R.id.search_delete);
        ArrayList<String> recipeNames = new ArrayList<String>();

        db = SqliteHelper.getInstance(RecipeSearch.this);

        List<Recipe> recipesList = db.getAllRecipes();
        for (int i = 0; i < recipesList.size(); i++) {
            recipeNames.add(recipesList.get(i).getRecipeName());

        }
        RecipeActionsHandler util = new RecipeActionsHandler(this);
        recipeNames = util.stringListSorter(recipeNames);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, recipeNames);
        list.setAdapter(adapter);

        input.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                RecipeSearch.this.adapter.getFilter().filter(s);

            }

            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }

            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

        });

        list.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                String recipeName = (String) list.getItemAtPosition(arg2);

                Class<?> selectedRecipePage = null;

                try {
                    selectedRecipePage = Class
                            .forName(GlobalStaticVariables.RECIPE_PAGE_CLASS);
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Intent openRecipe = new Intent(RecipeSearch.this,
                        selectedRecipePage);
                Bundle recipeBundle = new Bundle();
                recipeBundle.putString("name", db.getRecipeByName(recipeName)
                        .getRecipeName());

                String recipePage = db.getRecipeByName(recipeName)
                        .getRecipeURL();
                recipeBundle.putString("href", recipePage);
                Log.w(GlobalStaticVariables.LOG_TAG, recipePage);
                openRecipe.putExtras(recipeBundle);
                Log.w(GlobalStaticVariables.LOG_TAG, "new activity starting");
                db.closeDatabase();

                sendTrackerEvent(getString(R.string.analytics_screen_search_page), getString(R.string.analytics_navigate_from_search));

                startActivity(openRecipe);

            }
        });

        delete_filter.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View arg0, MotionEvent arg1) {
                input.setText("");
                return false;
            }
        });

    }

    private void sendTrackerEvent(String eventCategoryName, String eventActionName) {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(eventCategoryName)
                .setAction(eventActionName)
                .build());
    }


}
