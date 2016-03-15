package hu.blogspot.limarapeksege.util.handlers.note;

import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.SqliteHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class NoteHandler {
	
	SqliteHelper db;
	
	public NoteHandler(Context context){
		db = SqliteHelper.getInstance(context);
	}

	public boolean deleteNote(String name) {

		File noteDirectory = new File(Environment.getExternalStorageDirectory()
				+ GlobalStaticVariables.FAVORITE_RECIPE_PATH + "/Notes/");

		File note = new File(noteDirectory, name + ".txt");

		if (note.exists()) {

			note.delete();
			db.updateRecipeisNoteAdded(db.getRecipeByName(name).getId(), 0);
			db.closeDatabase();
			return true;
		} else {
			return false;
		}
	}

	public boolean saveNote(String noteText, String recipeName) {

		File noteFileDir = new File(Environment.getExternalStorageDirectory()
				+ GlobalStaticVariables.FAVORITE_RECIPE_PATH + "/Notes/");
		File noteFile = new File(Environment.getExternalStorageDirectory()
				+ GlobalStaticVariables.FAVORITE_RECIPE_PATH + "/Notes/",
				recipeName + ".txt");
		if (!noteFileDir.exists()) {
			noteFileDir.mkdirs();
		}

		if (noteFile.exists()) {
			noteFile.delete();
		}

		try {
			noteFile.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(noteFile));
			out.write(noteText);
			out.flush();
			out.close();
			db.updateRecipeisNoteAdded(db.getRecipeByName(recipeName).getId(),
					1);
			db.closeDatabase();
			Log.w(GlobalStaticVariables.LOG_TAG, "Done");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public String readNote(File note) {

		StringBuilder tempText = new StringBuilder();

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(note));
			String line;
			line = br.readLine();
			while (line != null) {
				tempText.append(line);
				tempText.append('\n');
				line = br.readLine();
			}
			br.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tempText.toString();
	}
	
}
