package hu.blogspot.limarapeksege.adapters.items;

import android.graphics.Bitmap;

/**
 * Created by benti on 2015.10.23..
 */
public class DrawerListItem {

    private String title;
    private int imageID;

    public DrawerListItem(String title, int imageID) {
        this.title = title;
        this.imageID = imageID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }
}
