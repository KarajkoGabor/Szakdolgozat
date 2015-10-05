package hu.blogspot.limarapeksege.asyncs;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.model.Category;
import hu.blogspot.limarapeksege.util.SqliteHelper;
import hu.blogspot.limarapeksege.util.handlers.recipe.RecipeActionsHandler;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.WindowManager;

public class AsyncPrepareRecipeDatas extends AsyncTask<Object, String, Boolean> {

	private Context context;
	private SqliteHelper db;
	private RecipeActionsHandler util;
	private ProgressDialog progDialog;
	private Activity activity;

	public AsyncPrepareRecipeDatas(Context context, Activity activity) {
		this.context = context;
		this.activity = activity;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		progDialog = new ProgressDialog(context);
		progDialog.setCancelable(false);
		progDialog.setMax(100);
		progDialog.setProgress(0);
		progDialog.setTitle(R.string.prepare_settings);
		progDialog.show();
		activity.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	protected Boolean doInBackground(Object... params) {
		// TODO Auto-generated method stub
		ArrayList<Category> categories = new ArrayList<Category>();
		List<Category> categoriesTemp = new ArrayList<Category>();
		int i = 1;
		util = new RecipeActionsHandler(this.context);
		db = SqliteHelper.getInstance(context);

		if (db.getAllCategories().size() == 0 || db.getRecipesByCategoryID(
				db.getCategoryByName(categoriesTemp.get(i).getName())
						.getId()).size() == 0) { // ha még nincsenek a receptek
													// letöltve
			categories = util.categoryParser((String) params[0]);

			for(Category category : categories){
				publishProgress(category.getName()
						+ " kateg?ria bet?lt?se " + i + "/"
						+ categories.size());
				i++;
				int categoryID = db.getCategoryByName(category.getName()).getId();
				util.gatherRecipeData(category.getLabel(), categoryID);
			}
		}

		return null;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		progDialog.dismiss();
		activity.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	}

	@Override
	protected void onProgressUpdate(String... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		progDialog.setMessage(values[0]);
	}

}
