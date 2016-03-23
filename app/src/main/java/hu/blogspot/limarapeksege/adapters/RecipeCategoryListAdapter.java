package hu.blogspot.limarapeksege.adapters;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.model.Recipe;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.SqliteHelper;
import hu.blogspot.limarapeksege.util.handlers.image.ImageHandler;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Picasso;

public class RecipeCategoryListAdapter extends ArrayAdapter<String> implements Filterable {

    private Context context;
    private int resourceId;
    private List<String> recipeTitles;
    private List<String> originalRecipeTitles = new ArrayList<String>();
    private int icon;
    private SqliteHelper db;

    public RecipeCategoryListAdapter(Context context, int resourceId,
                                     List<String> recipeTitles, int icon) {
        super(context, resourceId, recipeTitles);
        this.context = context;
        this.resourceId = resourceId;
        this.originalRecipeTitles.addAll(recipeTitles);
        this.recipeTitles = recipeTitles;
        this.icon = icon;
        this.db = SqliteHelper.getInstance(this.context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(resourceId, parent, false);

        if (recipeTitles.size() > position) {

            Recipe currentRecipe = db.getRecipeByName(recipeTitles.get(position));

            ImageView categoryIcon = (ImageView) view.findViewById(R.id.categoryPic);
            TextView recipeName = (TextView) view.findViewById(R.id.title);

            Log.w(GlobalStaticVariables.LOG_TAG,"I WANT TO LOAD " + currentRecipe.getRecipeThumbnailUrl());

            Glide.with(this.context).load(currentRecipe.getRecipeThumbnailUrl()).placeholder(icon).listener(new LoggingListener<String, GlideDrawable>()).override(180, 180).into(categoryIcon);
            recipeName.setText(recipeTitles.get(position));

        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint = constraint.toString().toLowerCase();

                FilterResults filterResults = new FilterResults();

                if (!TextUtils.isEmpty(constraint)) {
                    List<String> matchingTitles = new ArrayList<String>();

                    for (String title : RecipeCategoryListAdapter.this.originalRecipeTitles) {
                        if (title.toLowerCase().contains(constraint)) {
                            matchingTitles.add(title);
                        }
                    }

                    filterResults.values = matchingTitles;
                    filterResults.count = matchingTitles.size();

                } else {
                    filterResults.values = RecipeCategoryListAdapter.this.originalRecipeTitles;
                    filterResults.count = RecipeCategoryListAdapter.this.originalRecipeTitles.size();
                }


                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                List<String> resultValues = (List<String>) results.values;

                if (results.count > 0 && !resultValues.containsAll(originalRecipeTitles)) {
                    clear();
                    addAll(resultValues);
                    notifyDataSetChanged();
                    Log.w(GlobalStaticVariables.LOG_TAG, "Filtered");
                } else if (results.count == 0 && !resultValues.containsAll(originalRecipeTitles)) {
                    clear();
                    notifyDataSetChanged();
                    Log.w(GlobalStaticVariables.LOG_TAG, "Filtered -- no result");
                } else {
                    clear();
                    addAll(resultValues);
                    notifyDataSetChanged();
                    Log.w(GlobalStaticVariables.LOG_TAG, "Not filtered");
                }


            }
        };
    }

    class LoggingListener<T, R> implements RequestListener<T, R> {
        @Override public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
            android.util.Log.w(GlobalStaticVariables.LOG_TAG, String.format(Locale.ROOT,
                    "onException(%s, %s, %s, %s)", e, model, target, isFirstResource), e);
            return false;
        }
        @Override public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
            android.util.Log.w(GlobalStaticVariables.LOG_TAG, String.format(Locale.ROOT,
                    "onResourceReady(%s, %s, %s, %s, %s)", resource, model, target, isFromMemoryCache, isFirstResource));
            return false;
        }
    }

}
