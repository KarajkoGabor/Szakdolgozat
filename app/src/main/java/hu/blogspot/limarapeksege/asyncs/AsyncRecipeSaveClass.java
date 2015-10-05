package hu.blogspot.limarapeksege.asyncs;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.util.handlers.recipe.RecipeActionsHandler;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class AsyncRecipeSaveClass extends AsyncTask<Object, Integer, Void> {

	private ProgressDialog progressDialog;
	private Context context;

	public AsyncRecipeSaveClass(Context context) {
		super();
		this.context = context;
	}

	protected void onPreExecute() {
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(context.getResources().getString(
				R.string.recipe_save));
		progressDialog.show();
	}

	@Override
	protected Void doInBackground(Object... params) {
		try {
			String url = params[0].toString();
			String name = params[1].toString();
			Boolean isFavorite = (Boolean) params[2];
			RecipeActionsHandler util = new RecipeActionsHandler(context);

			util.saveRecipePage(url, name, isFavorite);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	protected void onPostExecute(Void valami) {
		progressDialog.dismiss();

	}

}
