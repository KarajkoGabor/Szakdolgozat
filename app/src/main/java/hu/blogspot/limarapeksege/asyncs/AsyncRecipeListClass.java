package hu.blogspot.limarapeksege.asyncs;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.adapters.RecipeCategoryListAdapter;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.handlers.recipe.RecipeActionsHandler;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

public class AsyncRecipeListClass extends
		AsyncTask<Object, Integer, List<String>> {

	private ProgressDialog progressDialog;
	private Context context;
	private String recipesDownloadMessage;
	private ListActivity activity;
	private int categoryPos;

	public AsyncRecipeListClass(ListActivity activity, Context context,
			String recipesDownloadMessage, int categoryPos) {
		super();
		this.context = context;
		this.recipesDownloadMessage = recipesDownloadMessage;
		this.activity = activity;
		this.categoryPos = categoryPos;
	}

	protected void onPreExecute() {
		progressDialog = ProgressDialog.show(context, "",
				recipesDownloadMessage);
	}

	@Override
	protected List<String> doInBackground(Object... params) {
		String categoryName = (String) params[0];
		String url = (String) params[1];
		int categoryID = (Integer) params[2];
		RecipeActionsHandler util = new RecipeActionsHandler(context);
		ArrayList<String> recipeTitles = util.recipeTitleParser(categoryName, url, categoryID);
		recipeTitles = util.stringListSorter(recipeTitles);

		Log.w(GlobalStaticVariables.LOG_TAG, "async continue");
		return recipeTitles;
	}

	protected void onPostExecute(List<String> list) {
		setAdapter(list);
		progressDialog.dismiss();
	}

	private void setAdapter(List<String> list) {

		TypedArray icons = activity.getResources().obtainTypedArray(
				R.array.category_icons);
		ArrayAdapter<String> adapter = new RecipeCategoryListAdapter(activity,
				R.layout.list_row_category, list, icons.getResourceId(categoryPos, -1));
		activity.setListAdapter(adapter);
	}

}
