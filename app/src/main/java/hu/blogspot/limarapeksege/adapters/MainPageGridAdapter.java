package hu.blogspot.limarapeksege.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.etsy.android.grid.StaggeredGridView;
import com.etsy.android.grid.util.DynamicHeightImageView;
import com.etsy.android.grid.util.DynamicHeightTextView;

import java.io.File;
import java.net.URI;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.asyncs.AsyncRecipeSaveClass;
import hu.blogspot.limarapeksege.model.Recipe;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.SqliteHelper;
import hu.blogspot.limarapeksege.util.handlers.file.FileHandler;

public class MainPageGridAdapter extends ArrayAdapter<Recipe> implements View.OnClickListener, Filterable {

    private Context context;
    private int resource;
    private ArrayList<Recipe> recipeList;
    private ArrayList<Recipe> originalRecipeList;
    private int itemHeightPixels = 400;
    private int itemWidthPixels = 400;
    private SqliteHelper db;

    public MainPageGridAdapter(Context context, int resource,
                               ArrayList<Recipe> recipeList) {
        super(context, resource, recipeList);
        this.context = context;
        this.resource = resource;
        this.recipeList = recipeList;
        this.originalRecipeList = new ArrayList<>();
        this.originalRecipeList.addAll(recipeList);
        db = SqliteHelper.getInstance(this.context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View gridView = convertView;
        TempHolder tempHolder = new TempHolder();
        Recipe currentRecipe = recipeList.get(position);

        if (gridView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            gridView = inflater.inflate(resource, parent, false);

            setGridViewItemSizeBasedOnDisplaySize((LinearLayout) gridView);

            setNumberOfColumns((StaggeredGridView) parent);

            tempHolder.gridItemIcon = (DynamicHeightImageView) gridView
                    .findViewById(R.id.recipeThumbnail);
            tempHolder.gridItemTitle = (DynamicHeightTextView) gridView
                    .findViewById(R.id.recipeTitle);
            tempHolder.gridItemCategoryTitle = (DynamicHeightTextView) gridView.findViewById(R.id.recipeCategoryTitle);
            tempHolder.gridSaveButton = (ImageButton) gridView.findViewById(R.id.saveGridButton);
            tempHolder.gridFavoriteButton = (ImageButton) gridView.findViewById(R.id.favoriteGridButton);

            gridView.setTag(tempHolder);

        } else {
            tempHolder = (TempHolder) gridView.getTag();
        }

        prepareButtons(tempHolder, currentRecipe);

        tempHolder.gridItemTitle.setText(currentRecipe.getRecipeName());
        tempHolder.gridItemCategoryTitle.setText(db.getCategoryById(currentRecipe.getCategory_id()).getName());

        String imageToLoadPath = currentRecipe.getRecipeThumbnailUrl();

        if(currentRecipe.isFavorite() || currentRecipe.isSaved()){
            imageToLoadPath = Environment.getExternalStorageDirectory() + GlobalStaticVariables.IMAGES_PATH + "/" + currentRecipe.getId() + "_0.jpg";
            Glide.with(this.context).load(new File(imageToLoadPath)).placeholder(R.drawable.ic_restaurant_menu_black_24dp).override(400, itemHeightPixels).fitCenter().into(tempHolder.gridItemIcon);
        }else{
            Glide.with(this.context).load(imageToLoadPath).placeholder(R.drawable.ic_restaurant_menu_black_24dp).override(400, itemHeightPixels).fitCenter().into(tempHolder.gridItemIcon);
        }


        return gridView;
    }

    private void prepareButtons(TempHolder tempHolder, Recipe currentRecipe){
        tempHolder.gridFavoriteButton.setOnClickListener(this);
        tempHolder.gridSaveButton.setOnClickListener(this);

        tempHolder.gridSaveButton.setTag(currentRecipe);
        tempHolder.gridFavoriteButton.setTag(currentRecipe);

        if (currentRecipe.isSaved()) {
            Drawable saveIcon = ResourcesCompat.getDrawable(this.context.getResources(), R.drawable.ic_file_download_black_24dp, null);
            setImageButtonEnabled(false, tempHolder.gridSaveButton, saveIcon);
        }

        if (currentRecipe.isFavorite()) {
            Drawable favoriteIcon = ResourcesCompat.getDrawable(this.context.getResources(), R.drawable.ic_favorite_black_24dp, null);
            setImageButtonEnabled(false, tempHolder.gridFavoriteButton, favoriteIcon);
        }
    }

    private void setNumberOfColumns(StaggeredGridView gridView) {
        if ((this.context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            gridView.setColumnCount(3);
        } else if ((this.context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            gridView.setColumnCount(4);
        } else if ((this.context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            gridView.setColumnCount(2);
        } else if ((this.context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            gridView.setColumnCount(2);
        }
    }

    private void setGridViewItemSizeBasedOnDisplaySize(LinearLayout gridView) {

        WindowManager windowManager = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

        itemHeightPixels = metrics.heightPixels / 4;
        itemWidthPixels = metrics.widthPixels / 2;

        if ((this.context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            Log.w(GlobalStaticVariables.LOG_TAG, "large");
            itemHeightPixels = metrics.heightPixels / 4;
            itemWidthPixels = metrics.widthPixels / 2;
        } else if ((this.context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            Log.w(GlobalStaticVariables.LOG_TAG, "xlarge");
            itemHeightPixels = metrics.heightPixels / 6;
            itemWidthPixels = metrics.widthPixels / 3;
        } else if ((this.context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            Log.w(GlobalStaticVariables.LOG_TAG, "normall");
            itemHeightPixels = metrics.heightPixels / 3;
            itemWidthPixels = metrics.widthPixels / 2;
        } else if ((this.context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            Log.w(GlobalStaticVariables.LOG_TAG, "small");
            itemHeightPixels = metrics.heightPixels / 3;
            itemWidthPixels = metrics.widthPixels / 2;
        }

        ViewGroup.LayoutParams layoutParams = gridView.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.width = 300;

        gridView.setLayoutParams(layoutParams);

    }

    @Override
    public void onClick(View v) {
        boolean isFavorite = true;
        try {
            if (v.getId() == R.id.saveGridButton) {
                new AsyncRecipeSaveClass(this.context).execute(v.getTag(), !isFavorite);
                Drawable saveIcon = ResourcesCompat.getDrawable(this.context.getResources(), R.drawable.ic_file_download_black_24dp, null);
                setImageButtonEnabled(false, (ImageButton) v, saveIcon);
            } else if (v.getId() == R.id.favoriteGridButton) {
                new AsyncRecipeSaveClass(this.context).execute(v.getTag(), isFavorite);
                Drawable favoriteIcon = ResourcesCompat.getDrawable(this.context.getResources(), R.drawable.ic_favorite_black_24dp, null);
                setImageButtonEnabled(false, (ImageButton) v, favoriteIcon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setImageButtonEnabled(boolean enabled, ImageButton item,
                                      Drawable originalIcon) {
        item.setEnabled(enabled);
        Drawable icon = enabled ? originalIcon : convertDrawableToGrayScale(originalIcon);
        item.setImageDrawable(icon);
    }

    public Drawable convertDrawableToGrayScale(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Drawable res = drawable.mutate();
        res.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        return res;
    }


    static class TempHolder {
        DynamicHeightTextView gridItemTitle;
        DynamicHeightTextView gridItemCategoryTitle;
        DynamicHeightImageView gridItemIcon;
        ImageButton gridSaveButton;
        ImageButton gridFavoriteButton;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint = constraint.toString().toLowerCase();
                constraint = transformAccent(constraint.toString());

                FilterResults filterResults = new FilterResults();

                if (!TextUtils.isEmpty(constraint)) {
                    List<Recipe> matchingRecipes = new ArrayList<Recipe>();

                    for (Recipe recipe : MainPageGridAdapter.this.originalRecipeList) {
                        String recipeName = recipe.getRecipeName().toLowerCase();
                        recipeName = transformAccent(recipeName);
                        if (recipeName.contains(constraint)) {
                            matchingRecipes.add(recipe);
                        }
                    }

                    filterResults.values = matchingRecipes;
                    filterResults.count = matchingRecipes.size();

                } else {
                    filterResults.values = MainPageGridAdapter.this.originalRecipeList;
                    filterResults.count = MainPageGridAdapter.this.originalRecipeList.size();
                }


                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                List<Recipe> resultValues = (List<Recipe>) results.values;

                if (results.count > 0 && !resultValues.containsAll(originalRecipeList)) {
                    clear();
                    addAll(resultValues);
                    notifyDataSetChanged();
                    Log.w(GlobalStaticVariables.LOG_TAG, "Filtered");
                } else if (results.count == 0 && !resultValues.containsAll(originalRecipeList)) {
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

    private String transformAccent(String originalString){
        String nfdNormalizedString = Normalizer.normalize(originalString, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

}
