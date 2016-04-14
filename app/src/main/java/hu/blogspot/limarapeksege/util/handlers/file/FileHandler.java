package hu.blogspot.limarapeksege.util.handlers.file;

import hu.blogspot.limarapeksege.model.Recipe;
import hu.blogspot.limarapeksege.model.WrongRecipeData;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.SqliteHelper;
import hu.blogspot.limarapeksege.util.XmlParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.opencsv.CSVWriter;

import org.jsoup.helper.StringUtil;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class FileHandler {

    private static final String SEGITSUTI = "Csokis rugelach - Segítsüti 2013";
    private static final String ADVENTI_2013 = "Adventi koszorú 2013";
    private static final String MEZESKALACS_2010 = "Mézeskalács adventi koszorú 2010";


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

    public void writeToCSVFile(ArrayList<Recipe> recipes, String category) {
        String csv = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + GlobalStaticVariables.MAIN_DIRECTORY + category + ".csv";
        Log.w(GlobalStaticVariables.LOG_TAG, "Csv file " + csv);
        List<String[]> data = new ArrayList<String[]>();
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(csv));
            for (Recipe recipe : recipes) {
                data.add(new String[]{recipe.getRecipeName(), category});
            }
            writer.writeAll(data);

            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.w(GlobalStaticVariables.LOG_TAG, "Csv file done");
    }

    public void renameFiles(XmlPullParser xpp, File[] oldFiles, Context context, String path, ArrayList<WrongRecipeData> wrongRecipeDatasList) throws NullPointerException {

        Log.w(GlobalStaticVariables.LOG_TAG, "Prepare saved stuff start on path: " + path);

        SqliteHelper db = SqliteHelper.getInstance(context);
        String regexWrongFile = "\\D*\\d?\\(\\d?\\).jpg";
        String regexForImages = "\\D+\\d+.jpg";

        for (File currentFile : oldFiles) {

            if (currentFile.getName().matches(regexWrongFile)) {
                currentFile.delete();
            } else if (!currentFile.isDirectory() && !StringUtil.isNumeric(currentFile.getName()) && !StringUtil.isNumeric(currentFile.getName().split("_")[0])) {

                WrongRecipeData tempWrongRecipeData = new WrongRecipeData();
                tempWrongRecipeData.setHtmlName(currentFile.getName());
                String newFileName;

                if (wrongRecipeDatasList.contains(tempWrongRecipeData)) {
                    newFileName = db.getRecipeByName(wrongRecipeDatasList.get(wrongRecipeDatasList.indexOf(tempWrongRecipeData)).getApiName()).getId();
                } else if (currentFile.getName().matches(regexForImages)) {
                    String[] splittedFileName = currentFile.getName().split("(?<=\\d)|(?=\\d)");
                    int splitSize = splittedFileName.length;
                    String beforeLastSplittedElement = splittedFileName[splitSize - 1];
                    String twoBeforeLastSplittedElement = splittedFileName[splitSize - 2];
                    String threeBeforeLastSplittedElement = splittedFileName[splitSize - 3];
                    String numbersAtEndOfImageFile = "";
                    String recipeName = "";
                    if (!StringUtil.isNumeric(beforeLastSplittedElement) && StringUtil.isNumeric(twoBeforeLastSplittedElement) && StringUtil.isNumeric(threeBeforeLastSplittedElement)
                            && !SEGITSUTI.contains(splittedFileName[0])
                            && !ADVENTI_2013.contains(splittedFileName[0])
                            && !MEZESKALACS_2010.contains(splittedFileName[0])) { //kétjegyű szám
                        numbersAtEndOfImageFile = "_" + splittedFileName[splitSize - 3] + splittedFileName[splitSize - 2] + splittedFileName[splitSize - 1]; //kétjegyű szám
                        for (int i = 0; i < splitSize - 3; i++) {
                            recipeName += splittedFileName[i];
                        }
                    } else {
                        numbersAtEndOfImageFile = "_" + splittedFileName[splitSize - 2] + splittedFileName[splitSize - 1];
                        for (int i = 0; i < splitSize - 2; i++) {
                            recipeName += splittedFileName[i];
                        }
                    }

                    tempWrongRecipeData.setHtmlName(recipeName);
                    if (wrongRecipeDatasList.contains(tempWrongRecipeData)) {
                        String tempRecipeId = db.getRecipeByName(wrongRecipeDatasList.get(wrongRecipeDatasList.indexOf(tempWrongRecipeData)).getApiName()).getId();
                        newFileName = "/" + tempRecipeId + numbersAtEndOfImageFile; //kétjegyű szám
                    } else {
                        newFileName = "/" + db.getRecipeByName(recipeName).getId() + numbersAtEndOfImageFile; //TODO kétszámjegyű
                    }
                } else {
                    newFileName = db.getRecipeByName(currentFile.getName()).getId();
                }
                File newFile = new File(path + newFileName);
                currentFile.renameTo(newFile);
            }

            Log.w(GlobalStaticVariables.LOG_TAG, "Prepare saved stuff ended on path: " + path);
        }

    }

    public File[] getSavedRecipeFiles() {
        File savedFilesLocation = new File(
                Environment.getExternalStorageDirectory() + GlobalStaticVariables.SAVED_RECIPE_PATH);

        return savedFilesLocation.listFiles();
    }

    public File[] getFavoriteRecipeFiles() {
        File favoriteFilesLocation = new File(
                Environment.getExternalStorageDirectory() + GlobalStaticVariables.FAVORITE_RECIPE_PATH);

        return favoriteFilesLocation.listFiles();
    }

    public File[] getImageFiles() {
        File imageFiles = new File(
                Environment.getExternalStorageDirectory() + GlobalStaticVariables.IMAGES_PATH);

        return imageFiles.listFiles();
    }

    public ArrayList<String> getFileNamesFromDirectory(String path){

        File filesLocation = new File(Environment.getExternalStorageDirectory() + path);

        List<String> fileNames = new LinkedList<>(Arrays.asList(filesLocation.list()));

        if(fileNames.contains("Images")){
            fileNames.remove("Images");
        }

        return new ArrayList<>(fileNames);
    }


}
