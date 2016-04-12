package hu.blogspot.limarapeksege.activity;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.adapters.SavedListAdapter;
import hu.blogspot.limarapeksege.model.Recipe;
import hu.blogspot.limarapeksege.util.AnalyticsTracker;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.SqliteHelper;
import hu.blogspot.limarapeksege.util.handlers.recipe.RecipeActionsHandler;

import java.io.File;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class SavedRecipes extends ListActivity {

	private ArrayList<String> savedRecipeTitles;
	private String temp;
	private int mainPosition;
	private static final File savedRecipesDirectory = new File(
			Environment.getExternalStorageDirectory() + GlobalStaticVariables.SAVED_RECIPE_PATH);
	private static final File favoriteRecipesDirectory = new File(
			Environment.getExternalStorageDirectory() + GlobalStaticVariables.FAVORITE_RECIPE_PATH);
	private AnalyticsTracker tracker;
	private SqliteHelper db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_saved_recipes);

		tracker = (AnalyticsTracker) getApplication();

		ListView lv = (ListView) findViewById(android.R.id.list);
		Bundle mainPositionBundle = getIntent().getExtras();
		mainPosition = mainPositionBundle.getInt("position");
		RecipeActionsHandler util = new RecipeActionsHandler(getApplicationContext());
		savedRecipeTitles = new ArrayList<>();
		db = SqliteHelper.getInstance(this);

		if (mainPosition == 1) {
			try {
				savedRecipeTitles = getSavedRecipeTitlesFromDirectory(savedRecipesDirectory);
			} catch (Exception e) {
				Toast.makeText(this, R.string.noSaved, Toast.LENGTH_LONG)
						.show();

			}
			tracker.sendScreen((getString(R.string.title_saved_recipes)));
			setTitle(getString(R.string.title_saved_recipes));

		} else if (mainPosition == 2) {
			try {
				savedRecipeTitles = getSavedRecipeTitlesFromDirectory(favoriteRecipesDirectory);
			} catch (Exception e) {
				Toast.makeText(this, R.string.noSaved, Toast.LENGTH_LONG)
						.show();

			}
			tracker.sendScreen((getString(R.string.title_favorite_recipes)));
			setTitle(getString(R.string.title_favorite_recipes));
		}

		util.stringListSorter(savedRecipeTitles);
		setAdapter(savedRecipeTitles);

		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bundle pushData = new Bundle();
				File recipeDirectory = null;
				// TODO Auto-generated method stub
				if (mainPosition == 1) {
					pushData.putBoolean("saved", true);
					recipeDirectory = savedRecipesDirectory;
				} else if (mainPosition == 2) {
					pushData.putBoolean("favorite", true);
					recipeDirectory = favoriteRecipesDirectory;
				}

                Recipe savedRecipe = db.getRecipeByName(savedRecipeTitles.get(arg2));
				temp = savedRecipe.getId();
				Log.w(GlobalStaticVariables.LOG_TAG, temp);

				assert recipeDirectory != null;
				for (File fs : recipeDirectory.listFiles()) {
					if (fs.isFile()) {
						if (fs.getName().equals(temp)) {
							Log.w(GlobalStaticVariables.LOG_TAG, "File megtal�lva");
							startRecipePageActivity(mainPosition, temp,
									pushData);
						}

					}

				}
			}
		});

	}

	private ArrayList<String> getSavedRecipeTitlesFromDirectory(File directory) {
		ArrayList<String> savedRecipeTitles = new ArrayList<String>();
		for (File fs : directory.listFiles()) {
			if (fs.isFile()) {

				temp = fs.getName();
				temp = temp.replace(".xml", "");
				savedRecipeTitles.add(db.getRecipeById(temp).getRecipeName());
			}
		}
		return savedRecipeTitles;
	}

	private void setAdapter(ArrayList<String> recipeTitles) {

		SavedListAdapter adapter = new SavedListAdapter(this, recipeTitles,
				 R.layout.list_row_saved);
		setListAdapter(adapter);

	}

	private void startRecipePageActivity(int mainposition, String recipeName,
			Bundle pushData) {
		Class<?> recipePage = null;
		try {
			recipePage = Class.forName(GlobalStaticVariables.RECIPE_PAGE_CLASS);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Intent openRecipe = new Intent(SavedRecipes.this, recipePage);

		pushData.putString("name", temp);
		pushData.putInt("position", mainPosition);

		openRecipe.putExtras(pushData);

		if(mainPosition == 1){
			tracker.sendTrackerEvent(getString(R.string.analytics_category_recipe), getString(R.string.analytics_open_saved_recipe));
		}else{
			tracker.sendTrackerEvent(getString(R.string.analytics_category_recipe), getString(R.string.analytics_open_favorite_recipe));
		}
		startActivity(openRecipe);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
