package hu.blogspot.limarapeksege.activity;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.adapters.SavedListAdapter;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
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

public class SavedRecipes extends ListActivity {

	private ArrayList<String> savedRecipeTitles;
	private String temp;
	private RecipeActionsHandler util;
	private int mainPosition;
	private static final File savedRecipesDirectory = new File(
			Environment.getExternalStorageDirectory() + "/LimaraPeksege",
			"SavedRecipes");
	private static final File favoriteRecipesDirectory = new File(
			Environment.getExternalStorageDirectory() + "/LimaraPeksege",
			"FavoriteRecipes");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_saved_recipes);

		ListView lv = (ListView) findViewById(android.R.id.list);
		Bundle mainPositionBundle = getIntent().getExtras();
		mainPosition = mainPositionBundle.getInt("position");
		util = new RecipeActionsHandler(getApplicationContext());
		savedRecipeTitles = new ArrayList<String>();
		if (mainPosition == 1) {
			try {
				savedRecipeTitles = getSavedRecipeTitlesFromDirectory(savedRecipesDirectory);
			} catch (Exception e) {
				Toast.makeText(this, R.string.noSaved, Toast.LENGTH_LONG)
						.show();

			}
			setTitle("Lementett receptek");
		} else if (mainPosition == 2) {
			try {
				savedRecipeTitles = getSavedRecipeTitlesFromDirectory(favoriteRecipesDirectory);
			} catch (Exception e) {
				Toast.makeText(this, R.string.noSaved, Toast.LENGTH_LONG)
						.show();

			}
			setTitle("Kedvenc receptek");
		}

		util.listSorter(savedRecipeTitles);
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

				temp = savedRecipeTitles.get(arg2).toString();
				Log.w("LimaraPeksege", temp);

				for (File fs : recipeDirectory.listFiles()) {
					if (fs.isFile()) {
						if (fs.getName().equals(temp)) {
							Log.w("LimaraPeksege", "File megtal�lva");
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
				savedRecipeTitles.add(temp);
			}
		}
		return savedRecipeTitles;
	}

	private void setAdapter(ArrayList<String> recipeTitles) {

		ArrayList<Bitmap> icons = new ArrayList<Bitmap>();
		File storagePath = Environment.getExternalStorageDirectory();
		String storeImagePath = null;
		storeImagePath = storagePath + GlobalStaticVariables.SAVED_RECIPE_PATH
				+ "Images/";
		for (int i = 0; i < recipeTitles.size(); i++) {
			String storeImage = recipeTitles.get(i) + "0" + ".jpg";
			storeImage = storeImagePath + storeImage;
			Log.w("LimaraPeksege", storeImage);
			try {
				Bitmap tempBitmap = BitmapFactory.decodeFile(storeImage);
				icons.add(tempBitmap);
				tempBitmap.recycle();
			} catch (Exception e) {
				Toast.makeText(this,
						"Elfogyott a mem�ria a k�p bet�lt�se k�zben!",
						Toast.LENGTH_LONG).show();
			}
		}
		SavedListAdapter adapter = new SavedListAdapter(this, recipeTitles,
				icons, R.layout.list_row_saved);
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
		startActivity(openRecipe);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
