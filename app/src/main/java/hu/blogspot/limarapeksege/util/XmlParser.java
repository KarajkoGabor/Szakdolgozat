package hu.blogspot.limarapeksege.util;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.model.Category;
import hu.blogspot.limarapeksege.model.WrongRecipeData;

public class XmlParser {

    public ArrayList<?> parseXml(XmlPullParser xpp, String mode)
            throws XmlPullParserException, IOException {
        ArrayList<?> resultList = null;
        ArrayList<String> tempResultListString = new ArrayList<String>();
        ArrayList<Category> tempResultListCategory = new ArrayList<Category>();

        // megvizsg�ljuk �s node-onk�nt kiolvassuk
        while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
            if (xpp.getEventType() == XmlPullParser.START_TAG) {
                if (xpp.getName().equals("Item")) {
                    if (mode.equals("category")) {

                        Category tempCategory = new Category();
                        tempCategory.setLabel(xpp.getAttributeValue(0));
                        tempCategory.setName(xpp.getAttributeValue(1));
                        tempResultListCategory.add(tempCategory);
                        resultList = tempResultListCategory;
                        Log.w(GlobalStaticVariables.LOG_TAG, "Category parsed with label " + tempCategory.getLabel() + " and name " + tempCategory.getName());
                        Log.w(GlobalStaticVariables.LOG_TAG, "Xpp attributeName " + xpp.getAttributeName(0) + " and name " + xpp.getText()
                        );
                    } else {

                        tempResultListString.add(xpp.getAttributeValue(0));
                        resultList = tempResultListString;
                        Log.w(GlobalStaticVariables.LOG_TAG,
                                xpp.getAttributeValue(0));

                    }
                }
            }

            xpp.next();
        }

        return resultList;
    }


    public ArrayList<WrongRecipeData> parseWrongRecipesXML(XmlPullParser xpp)
            throws XmlPullParserException, IOException {
        ArrayList<WrongRecipeData> resultList = new ArrayList<>();

        while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
            if (xpp.getEventType() == XmlPullParser.START_TAG) {
                if (xpp.getName().equals("Recipe")) {
                        WrongRecipeData wrongRecipe = new WrongRecipeData();
                        wrongRecipe.setApiName(xpp.getAttributeValue(0));
                        wrongRecipe.setHtmlName(xpp.getAttributeValue(1));
                        resultList.add(wrongRecipe);
                }
            }

            xpp.next();
        }

        return resultList;
    }

}
