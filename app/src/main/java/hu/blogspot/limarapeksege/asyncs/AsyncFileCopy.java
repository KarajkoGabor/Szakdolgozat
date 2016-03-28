package hu.blogspot.limarapeksege.asyncs;

import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.SqliteHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncFileCopy extends AsyncTask<Object, Integer, Boolean> {

	private Context context;

	public AsyncFileCopy(Context context) {
		this.context = context;
	}

	@Override
	protected Boolean doInBackground(Object... params) {
		File source = (File) params[0];
		File destination = (File) params[1];
		String recipeID = (String) params[2];

		Log.w(GlobalStaticVariables.LOG_TAG, "copy file start");

		if (!destination.exists()) {
			try {
				destination.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		copyFile(source, destination, recipeID);
		Log.w(GlobalStaticVariables.LOG_TAG, "copy file end");
		return true;

	}

	protected void onPostExecute(Void finish) {

	}

	private void copyFile(File source, File destination, String recipeID) {
		SqliteHelper db = new SqliteHelper(context);

		try {
			InputStream fileIn = new FileInputStream(source);
			OutputStream fileOut = new FileOutputStream(destination);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = fileIn.read(buffer)) > 0) {
				fileOut.write(buffer, 0, length);
			}
			fileIn.close();
			fileOut.close();

			db.updateRecipeIsFavorite(db.getRecipeById(recipeID).getId(), 1);
			db.closeDatabase();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
