package hu.blogspot.limarapeksege.asyncs;

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
	private SqliteHelper db;

	public AsyncFileCopy(Context context) {
		this.context = context;
	}

	@Override
	protected Boolean doInBackground(Object... params) {
		File source = (File) params[0];
		File destination = (File) params[1];
		String recipeName = (String) params[2];

		Log.w("LimaraPéksége", "copy file start");

		if (!destination.exists()) {
			try {
				destination.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		copyFile(source, destination, recipeName);
		Log.w("LimaraPéksége", "copy file end");
		return true;

	}

	protected void onPostExecute(Void finish) {

	}

	private void copyFile(File source, File destination, String recipeName) {
		db = new SqliteHelper(context);

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

			db.updateRecipeIsFavorite(db.getRecipeByName(recipeName).getId(), 1);
			Log.w("LimaraPéksége", db.getRecipeByName(recipeName).isFavorite()
					+ " is favorite from copy");
			db.closeDatabase();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
