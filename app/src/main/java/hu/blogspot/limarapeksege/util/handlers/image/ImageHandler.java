package hu.blogspot.limarapeksege.util.handlers.image;

import hu.blogspot.limarapeksege.util.GlobalStaticVariables;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.Environment;
import android.util.Log;

public class ImageHandler {

    public String setImageDivs(String html) {
        String tempHtml = html;
        Document document = null;
        document = Jsoup.parse(html, "utf-8");
        Elements imageDivs = document.select("div.separator");
        Elements hrefs = imageDivs.select("a");

        for (Element e : hrefs) {
            String style = e.attr("style");
            // e.attr("style","clear: both; text-align: center;  margin-right: 1em;display: block;");
            if (style.contains("float: left")) {
                String newStyle = style.replace("float: left;", " ");
                html = tempHtml.replace(style, newStyle);
                Log.w(GlobalStaticVariables.LOG_TAG, style);
                Log.w(GlobalStaticVariables.LOG_TAG, "�J STYLE***" + newStyle);
            }
        }
        return html;

    }

    //TODO REFACTOR K?P MENT?SE!!!

    public String saveImage(String text, String imageName)
            throws IOException {
        Log.w("LimaraP�kg�se", "image save");
        Document document = null;
        int i = 0;
        File storeImage;

        document = Jsoup.parse(text, "utf-8");

        Elements images = document.select("img");
        for (Element e : images) {
            String temp = e.absUrl("src");
            String storageImagePath = "file://";
            URL url = new URL(temp);

            InputStream io = url.openStream();

            File storagePath = Environment.getExternalStorageDirectory();
            storeImage = new File(storagePath
                    + GlobalStaticVariables.SAVED_RECIPE_PATH + "/Images/",
                    imageName + i + ".jpg");
            storageImagePath = storageImagePath + storagePath
                    + GlobalStaticVariables.SAVED_RECIPE_PATH + "/Images/"
                    + imageName + i + ".jpg";

            text = text.replace("\"" + temp + "\"", "\"" + storageImagePath
                    + "\"");


            if (!storeImage.exists()) {
                storeImage.mkdirs();
            }

            if (storeImage.exists()) {
                storeImage.delete();
            }

            OutputStream ou = new FileOutputStream(storeImage);

            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = io.read(buffer, 0, buffer.length)) >= 0) {
                ou.write(buffer, 0, bytesRead);
            }
            ou.close();
            io.close();
            i++;
        }
        return text;

    }

    public void saveImageForRecipeListToLocalPath(String src, String recipeName) {
        try {
            URL url = new URL(src);

            InputStream io = url.openStream();

            File storagePath = Environment.getExternalStorageDirectory();
            File storeImage = new File(storagePath
                    + GlobalStaticVariables.SAVED_RECIPE_PATH + "/Images/",
                    recipeName + "0.jpg");

            if (!storeImage.exists()) {
                storeImage.mkdirs();
            }

            if (storeImage.exists()) {
                storeImage.delete();
            }

            OutputStream ou = new FileOutputStream(storeImage);

            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = io.read(buffer, 0, buffer.length)) >= 0) {
                ou.write(buffer, 0, bytesRead);
            }
            ou.close();
            io.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
