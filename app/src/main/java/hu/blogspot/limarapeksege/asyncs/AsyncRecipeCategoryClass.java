package hu.blogspot.limarapeksege.asyncs;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.model.Category;
import hu.blogspot.limarapeksege.util.RecipeCategoryGridMaker;
import hu.blogspot.limarapeksege.util.handlers.recipe.RecipeActionsHandler;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.GridView;

public class AsyncRecipeCategoryClass extends
		AsyncTask<Object, Integer, ArrayList<Category>> {

	private ProgressDialog progressDialog;
	private Context context;
	private RecipeCategoryGridMaker recipeCategoryGridMaker;
	private GridView grid;

	public AsyncRecipeCategoryClass(Context context) {
		this.context = context;
	}

	protected void onPreExecute() {
		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle(R.string.categories_download);
		progressDialog.show();
	}

	@Override
	protected ArrayList<Category> doInBackground(Object... params) {
		RecipeActionsHandler util = new RecipeActionsHandler(context);
		recipeCategoryGridMaker = (RecipeCategoryGridMaker) params[2];
		grid = (GridView) params[3];

		return util.categoryParser();
	}

	protected void onPostExecute(ArrayList<String> list) {

		grid = recipeCategoryGridMaker.setGridItems(list);
		progressDialog.dismiss();
	}

}
