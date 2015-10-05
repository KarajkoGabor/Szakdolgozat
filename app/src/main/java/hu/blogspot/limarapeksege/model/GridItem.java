package hu.blogspot.limarapeksege.model;

import android.graphics.Bitmap;

public class GridItem {

	Bitmap icon;
	String title;

	public GridItem(String title, Bitmap icon) {
		this.icon = icon;
		this.title = title;
	}

	public Bitmap getIcon() {
		return icon;
	}

	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
