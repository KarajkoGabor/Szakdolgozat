package hu.blogspot.limarapeksege.adapters;

import hu.blogspot.limarapeksege.R;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class LoafMakingPageAdapter extends PagerAdapter {

	Context context;
	int[] pictures;
	ArrayList<String> texts;
	LayoutInflater inflater;

	public LoafMakingPageAdapter(Context context, int[] pictures,
			ArrayList<String> texts) {
		this.context = context;
		this.pictures = pictures;
		this.texts = texts;
	}

	@Override
	public int getCount() {
		return pictures.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		ImageView imgLoaf;
		TextView textLoaf;
		TextView textStep;
		String loafMakingStep = this.context.getString(R.string.loaf_making_step);

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.loafmaking_items, container,
				false);

		textLoaf = (TextView) itemView.findViewById(R.id.loaf_text);
		textLoaf.setText(texts.get(position));

		imgLoaf = (ImageView) itemView.findViewById(R.id.loaf_image);
		imgLoaf.setImageResource(pictures[position]);

		textStep = (TextView) itemView.findViewById(R.id.step);
		textStep.setText((position + 1) + loafMakingStep);

		container.addView(itemView);

		return itemView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		container.removeView((View) object);
	}

}
