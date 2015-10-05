package hu.blogspot.limarapeksege.adapters;

import hu.blogspot.limarapeksege.R;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RecipeCategoryListAdapter extends ArrayAdapter<String> {

	private Context context;
	private int resourceId;
	private List<String> objects;
	private ImageView categoryIcon;
	private TextView recipeName;
	private Bitmap icon;

	public RecipeCategoryListAdapter(Context context, int resourceId,
			List<String> objects, Bitmap icon) {
		super(context, resourceId, objects);
		this.context = context;
		this.resourceId = resourceId;
		this.objects = objects;
		this.icon = icon;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;

		LayoutInflater inflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(resourceId, parent, false);

		categoryIcon = (ImageView) view.findViewById(R.id.categoryPic);
		recipeName = (TextView) view.findViewById(R.id.title);

		categoryIcon.setImageBitmap(icon);
		recipeName.setText(this.objects.get(position));

		return view;
	}

}
