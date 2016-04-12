package hu.blogspot.limarapeksege.adapters.items;

import android.graphics.Bitmap;

public class DrawerListItem {

    private String title;
    private int imageID;
    private int itemId;

    public DrawerListItem(String title, int imageID, int itemId) {
        this.title = title;
        this.imageID = imageID;
        this.itemId = itemId;
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

    public int getItemId() {
        return itemId;
    }
}
