package hu.blogspot.limarapeksege.util;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.adapters.CategoryPageGridAdapter;
import hu.blogspot.limarapeksege.model.GridItem;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.widget.GridView;

public class RecipeCategoryGridMaker {

	private GridView grid;
	private Activity activity;
	private ArrayList<GridItem> itemArray;
	private CategoryPageGridAdapter gridAdapter;

	public RecipeCategoryGridMaker(Activity activity) {
		this.activity = activity;
	}

	public GridView setGridItems(List<String> mainMenuList) {
		itemArray = new ArrayList<GridItem>();
		grid = (GridView) activity.findViewById(R.id.categoryGrid);

		if ((activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
			grid.setNumColumns(3);
		} else if ((activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
			grid.setNumColumns(4);
		} else if ((activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			grid.setNumColumns(2);
		} else if ((activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
			grid.setNumColumns(2);
		}

		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int heightPixels = metrics.heightPixels;

		TypedArray icons = activity.getResources().obtainTypedArray(
				R.array.category_icons);

		for (int i = 0; i < mainMenuList.size(); i++) {
			Bitmap tempIcon = BitmapFactory.decodeResource(
					activity.getResources(), icons.getResourceId(i, -1));
			itemArray.add(new GridItem(mainMenuList.get(i), tempIcon));
		}

		gridAdapter = new CategoryPageGridAdapter(activity,
				R.layout.category_grid_row, itemArray, heightPixels);
		grid.setAdapter(gridAdapter);

		return grid;
	}

}
