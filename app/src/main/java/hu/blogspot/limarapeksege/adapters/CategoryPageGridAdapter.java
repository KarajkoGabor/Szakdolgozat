package hu.blogspot.limarapeksege.adapters;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.model.GridItem;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryPageGridAdapter extends ArrayAdapter<GridItem> {

	Context context;
	int resource;
	ArrayList<GridItem> objects;
	int heightPixels;

	public CategoryPageGridAdapter(Context context, int resource,
			ArrayList<GridItem> objects, int heightPixels) {
		super(context, resource, objects);
		this.context = context;
		this.resource = resource;
		this.objects = objects;
		this.heightPixels = heightPixels;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View gridView = convertView;
		TempHolder tempHolder = null;

		if (gridView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			gridView = inflater.inflate(resource, parent, false);

			if ((this.context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
				Log.w(GlobalStaticVariables.LOG_TAG, "large");
				gridView.setMinimumHeight(heightPixels / 4);
			} else if ((this.context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
				Log.w(GlobalStaticVariables.LOG_TAG, "xlarge");
				gridView.setMinimumHeight(heightPixels / 6);
			} else if ((this.context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
				Log.w(GlobalStaticVariables.LOG_TAG, "normall");
				gridView.setMinimumHeight(heightPixels / 4);
			} else if ((this.context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
				Log.w(GlobalStaticVariables.LOG_TAG, "small");
				gridView.setMinimumHeight(heightPixels / 3);
			}

			tempHolder = new TempHolder();
			tempHolder.gridItemIcon = (ImageView) gridView
					.findViewById(R.id.categoryIcon);
			tempHolder.gridItemTitle = (TextView) gridView
					.findViewById(R.id.categoryText);
			gridView.setTag(tempHolder);

		} else {
			tempHolder = (TempHolder) gridView.getTag();
		}

		GridItem gridItem = objects.get(position);
		tempHolder.gridItemTitle.setText(gridItem.getTitle());
		tempHolder.gridItemIcon.setImageBitmap(gridItem.getIcon());

		return gridView;
	}

	static class TempHolder {
		TextView gridItemTitle;
		ImageView gridItemIcon;
	}

}
