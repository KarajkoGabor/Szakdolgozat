package hu.blogspot.limarapeksege.util.handlers.file;

import hu.blogspot.limarapeksege.model.Recipe;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.util.Log;

import com.opencsv.CSVWriter;

public class FileHandler {

	public void writeToFile(String textToWrite, String recipTypeToSave,
			String folderPathToSave, String outputFileName) {
		try {
			File webpageDirectory = new File(
					Environment.getExternalStorageDirectory() + GlobalStaticVariables.MAIN_DIRECTORY,
					recipTypeToSave);
			if (!webpageDirectory.exists())
				webpageDirectory.mkdirs();
			File webpage = new File(Environment.getExternalStorageDirectory()
					+ folderPathToSave, outputFileName);
			FileOutputStream fOS;
			fOS = new FileOutputStream(webpage);
			fOS.write(textToWrite.getBytes());
			fOS.flush();
			fOS.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeToCSVFile(ArrayList<Recipe> recipes,String category){
		String csv = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+GlobalStaticVariables.MAIN_DIRECTORY+category+".csv";
		Log.w("LimaraPékségeCSV", "Csv file " + csv);
		List<String[]> data = new ArrayList<String[]>();
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(csv));
			for(Recipe recipe: recipes){
				data.add(new String[]{recipe.getRecipeName(), category});
			}
			writer.writeAll(data);

			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.w("LimaraPékségeCSV", "Csv file done");
	}
	
}
