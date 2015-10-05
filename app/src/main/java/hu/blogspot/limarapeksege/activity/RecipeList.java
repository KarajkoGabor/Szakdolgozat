package hu.blogspot.limarapeksege.activity;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.adapters.RecipeCategoryListAdapter;
import hu.blogspot.limarapeksege.asyncs.AsyncRecipeListClass;
import hu.blogspot.limarapeksege.model.Category;
import hu.blogspot.limarapeksege.model.Recipe;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.SqliteHelper;
import hu.blogspot.limarapeksege.util.handlers.recipe.RecipeActionsHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class RecipeList extends ListActivity {

	private int categoryPos;

	private RecipeActionsHandler util;
	private SqliteHelper db;
	private Category currentCategory;
	private AsyncRecipeListClass asyncRecipeList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_list);
		trimCache(this);
		ArrayList<String> recipeTitles = new ArrayList<String>();
		List<Recipe> recipeList;

		util = new RecipeActionsHandler(RecipeList.this);

		db = SqliteHelper.getInstance(RecipeList.this);

		ListView lv = (ListView) findViewById(android.R.id.list);
		Bundle extras = getIntent().getExtras();

		String category = extras.getString("category");// beolvassuk milyen
														// kategóriát
		// választott a felhasználó
		categoryPos = extras.getInt("position");
		currentCategory = db.getCategoryByName(category);

//		category = category.replace(":", "");
		setTitle(category);

		File storageReceipeListDirectory = new File(
				Environment.getExternalStorageDirectory()
						+ GlobalStaticVariables.MAIN_DIRECTORY,
				GlobalStaticVariables.RECIPE_LIST_DIRECTORY);

//		if (!category.equals(GlobalStaticVariables.KELTTESZTA))
//			category = category.concat(":");

		Log.w("LimaraPéksége", db.getCategoryByName(category)
				.isRecipesDownloaded() + "");

		if (currentCategory.isRecipesDownloaded()) {
			try {
				Log.w("LimaraPéksége", "not async");
				recipeList = db.getRecipesByCategoryID(currentCategory.getId());
				Log.w("LimaraPéksége", "size" + recipeList.size() + "");
				for (Recipe recipe : recipeList) {
					recipeTitles.add(recipe.getRecipeName());
				}
				Log.w("LimaraPéksége", "size" + recipeTitles.size() + "");
				setAdapter(util.listSorter(recipeTitles));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			Log.w("LimaraPéksége", "async");
			storageReceipeListDirectory.mkdirs();
			asyncRecipeList = new AsyncRecipeListClass(RecipeList.this,
					RecipeList.this, getString(R.string.recipes_download),
					categoryPos);
			asyncRecipeList.execute(category,
					GlobalStaticVariables.URL_TARTALOM,
					currentCategory.getId(), RecipeList.this);

		}

		db.closeDatabase();

		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				try {
					if (isNetworkAvailable() == false) {
						throw new Exception();
					} else {
						String recipeName = getRecipeNameFromDBByCategoryId(
								currentCategory.getId(), position);

						startNewActivity(
								GlobalStaticVariables.RECIPE_PAGE_CLASS,
								recipeName);

					}

				} catch (Exception e) {
					Toast.makeText(RecipeList.this, R.string.no_connection,
							Toast.LENGTH_LONG).show();
				}
			}

		});

	}

	private String getRecipeNameFromDBByCategoryId(int categoryId,
			int positionInView) {
		List<Recipe> recipes = db.getRecipesByCategoryID(categoryId);
		ArrayList<String> recipeNames = new ArrayList<String>();
		for (Recipe recipe : recipes) {
			recipeNames.add(recipe.getRecipeName());
		}
		recipeNames = util.listSorter(recipeNames);
		return recipeNames.get(positionInView);
	}

	private void setAdapter(List<String> recipeTitles) {

		TypedArray icons = getResources().obtainTypedArray(
				R.array.category_icons);
		Bitmap tempIcon = BitmapFactory.decodeResource(getResources(),
				icons.getResourceId(categoryPos, -1));
		ArrayAdapter<String> adapter = new RecipeCategoryListAdapter(this,
				R.layout.list_row_category, recipeTitles, tempIcon);
		setListAdapter(adapter);

	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	public static void trimCache(Context context) {
		try {
			File dir = context.getCacheDir();
			if (dir != null && dir.isDirectory()) {
				deleteDir(dir);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static boolean deleteDir(File dir) {
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
		Log.w("LimaraPéksége", selectedRecipe.getRecipeName());

		String recipePage = selectedRecipe.getRecipeURL();
		b2.putString("href", recipePage);
		Log.w("LimaraPéksége", recipePage);

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
		Log.w("LimaraPéksége", "new activity starting");
		db.closeDatabase();
		startActivity(openRecipe);
	}

}
