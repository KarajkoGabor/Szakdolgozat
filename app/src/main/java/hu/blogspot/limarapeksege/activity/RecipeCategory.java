package hu.blogspot.limarapeksege.activity;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.asyncs.AsyncRecipeCategoryClass;
import hu.blogspot.limarapeksege.model.Category;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.RecipeCategoryGridMaker;
import hu.blogspot.limarapeksege.util.SqliteHelper;

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

	private List<String> mainMenuList = new ArrayList<String>();

	private AsyncRecipeCategoryClass asyncCategory;
	private GridView grid;
	private SqliteHelper db;
	// public static int heightPixels;
	private Bundle b;
	private RecipeCategoryGridMaker categoryGridMaker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_category);
		b = new Bundle();
		db = SqliteHelper.getInstance(RecipeCategory.this);
		categoryGridMaker = new RecipeCategoryGridMaker(RecipeCategory.this);

		try {

			if (!db.getAllCategories().isEmpty()) { // ha már egyszer
													// elmentettük

				List<Category> tempCategoryList = new ArrayList<Category>();

				mainMenuList.clear();

				tempCategoryList = db.getAllCategories();
				Log.w("LimaraPéksége", tempCategoryList.size() + "");

				for (int i = 0; i < tempCategoryList.size(); i++) {
					Log.w("LimaraPéksége", tempCategoryList.get(i).getName());
					mainMenuList.add(tempCategoryList.get(i).getName());
				}

				Log.w("LimaraPéksége", "not async");
			} else {
				db.deleteCategoryTable();
				// db.deleteRecipeTable();
				asyncCategory = new AsyncRecipeCategoryClass(
						RecipeCategory.this);
				asyncCategory.execute(RecipeCategory.this,
						GlobalStaticVariables.URL_TARTALOM, categoryGridMaker,
						grid);
				Log.w("LimaraPéksége", "async");
			}

			grid = categoryGridMaker.setGridItems(mainMenuList);
			db.closeDatabase();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		grid.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				try {
					if (isNetworkAvailable() == false) {
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

		Intent openReceptList = new Intent(RecipeCategory.this, recipeList);
		b.putInt("position", position);
		b.putString("category", mainMenuList.get(position).toString());// elküldjük
																		// a
																		// kategóriát

		openReceptList.putExtras(b);
		startActivity(openReceptList);
	}

	public void postResult(ArrayList<String> resultList) {
		// TODO Auto-generated method stub

	}

}
