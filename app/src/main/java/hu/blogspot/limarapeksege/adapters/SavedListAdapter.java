package hu.blogspot.limarapeksege.adapters;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.SqliteHelper;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SavedListAdapter extends BaseAdapter {

    private ArrayList<String> recipeTitles;
    private LayoutInflater inflater = null;
    private int resourceID;
    private Activity activity;
    private SqliteHelper db;

    public SavedListAdapter(Activity activity, ArrayList<String> recipeTitles,
                            int resourceID) {
        this.recipeTitles = recipeTitles;
        this.activity = activity;
        inflater = (LayoutInflater) this.activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resourceID = resourceID;
        this.db = SqliteHelper.getInstance(this.activity);
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return recipeTitles.size();
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    public View getView(int arg0, View arg1, ViewGroup arg2) {
        View view = arg1;
        if (arg1 == null) {
            view = inflater.inflate(resourceID, null);
        }

        File storagePath = Environment.getExternalStorageDirectory();
        String storeImagePath = null;
        storeImagePath = storagePath + GlobalStaticVariables.IMAGES_PATH + "/";

        String storeImage = db.getRecipeByName(recipeTitles.get(arg0)).getId() + "_0.jpg";
        storeImage = storeImagePath + storeImage;
        Bitmap tempBitmap = BitmapFactory.decodeFile(storeImage);

        ImageView icon = (ImageView) view.findViewById(R.id.categoryPic);
        TextView title = (TextView) view.findViewById(R.id.title);
        icon.setImageBitmap(tempBitmap);

        title.setText(recipeTitles.get(arg0));

        return view;
    }

}
