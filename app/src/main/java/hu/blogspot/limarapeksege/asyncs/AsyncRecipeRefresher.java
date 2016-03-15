package hu.blogspot.limarapeksege.asyncs;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.model.Category;
import hu.blogspot.limarapeksege.util.SqliteHelper;
import hu.blogspot.limarapeksege.util.handlers.recipe.RecipeActionsHandler;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class AsyncRecipeRefresher extends AsyncTask<Object, String, Boolean> {

	private Context context;
	private ProgressDialog progressDialog;

	public AsyncRecipeRefresher(Context context) {
		this.context = context;
	}

	@Override
	protected Boolean doInBackground(Object... params) {

		List<Category> recipeCategories = new ArrayList<Category>();
		SqliteHelper db = new SqliteHelper(context);
		RecipeActionsHandler util = new RecipeActionsHandler(context);
		recipeCategories = db.getAllCategories();

		for (int i = 0; i < recipeCategories.size(); i++) {
			util.recipeTitleParser(recipeCategories.get(i).getName(),
					(String) params[0], recipeCategories.get(i).getId());
			publishProgress(recipeCategories.get(i).getName()
					+ " kategória frissítése...");
		}

		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		progressDialog.dismiss();
		Toast.makeText(context, R.string.update_finished, Toast.LENGTH_LONG)
				.show();
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(false);
		progressDialog.setMax(100);
		progressDialog.setProgress(0);
		progressDialog.setTitle(R.string.refresh_recipes);
		progressDialog.show();
	}

	@Override
	protected void onProgressUpdate(String... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		progressDialog.setMessage(values[0]);
	}

}
