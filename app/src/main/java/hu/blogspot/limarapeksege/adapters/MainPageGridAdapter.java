package hu.blogspot.limarapeksege.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.asyncs.AsyncRecipeSaveClass;
import hu.blogspot.limarapeksege.model.Recipe;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.SqliteHelper;
import hu.blogspot.limarapeksege.util.handlers.recipe.RecipeActionsHandler;
import in.srain.cube.views.GridViewWithHeaderAndFooter;

public class MainPageGridAdapter extends ArrayAdapter<Recipe> implements View.OnClickListener {

    private Context context;
    private int resource;
    private ArrayList<Recipe> recipeList;
    private int currentSize;
    private SqliteHelper db;
//    Category currentCategory;

    public MainPageGridAdapter(Context context, int resource,
                               ArrayList<Recipe> recipeList) {
        super(context, resource, recipeList);
        this.context = context;
        this.resource = resource;
        this.recipeList = recipeList;
        db = SqliteHelper.getInstance(this.context);
//        this.currentCategory = currentCategory;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View gridView = convertView;
        TempHolder tempHolder = new TempHolder();

        if (gridView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            gridView = inflater.inflate(resource, parent, false);

            setGridViewItemSizeBasedOnDisplaySize((LinearLayout) gridView);

            setNumberOfColumns((GridViewWithHeaderAndFooter) parent);

            tempHolder.gridItemIcon = (ImageView) gridView
                    .findViewById(R.id.recipeThumbnail);
            tempHolder.gridItemTitle = (TextView) gridView
                    .findViewById(R.id.recipeTitle);
            tempHolder.gridItemCategoryTitle = (TextView) gridView.findViewById(R.id.recipeCategoryTitle);
            tempHolder.gridSaveButton = (ImageButton) gridView.findViewById(R.id.saveGridButton);
            tempHolder.gridFavoriteButton = (ImageButton) gridView.findViewById(R.id.favoriteGridButton);

            gridView.setTag(tempHolder);

        } else {
            tempHolder = (TempHolder) gridView.getTag();
        }

        Recipe currentRecipe = recipeList.get(position);
        tempHolder.gridItemTitle.setText(currentRecipe.getRecipeName());
        tempHolder.gridItemCategoryTitle.setText(db.getCategoryById(currentRecipe.getCategory_id()).getName());
        tempHolder.gridFavoriteButton.setOnClickListener(this);
        tempHolder.gridSaveButton.setOnClickListener(this);
        tempHolder.gridSaveButton.setOnClickListener(this);
        tempHolder.gridSaveButton.setTag(currentRecipe);
        tempHolder.gridFavoriteButton.setTag(currentRecipe);
        Glide.with(this.context).load(currentRecipe.getRecipeThumbnailUrl()).fitCenter().into(tempHolder.gridItemIcon);
//        tempHolder.gridItemIcon.setImageBitmap(actualRecipe.getRecipeThumbnailUrl());

        return gridView;
    }

    private void setNumberOfColumns(GridViewWithHeaderAndFooter gridView) {
        if ((this.context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            gridView.setNumColumns(3);
        } else if ((this.context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            gridView.setNumColumns(4);
        } else if ((this.context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            gridView.setNumColumns(2);
        } else if ((this.context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            gridView.setNumColumns(2);
        }
    }

    private void setGridViewItemSizeBasedOnDisplaySize(LinearLayout gridView) {

        WindowManager windowManager = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int heightPixels = metrics.heightPixels;
        this.currentSize = heightPixels / 4;

        if ((this.context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            Log.w(GlobalStaticVariables.LOG_TAG, "large");
            this.currentSize = heightPixels / 4;
        } else if ((this.context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            Log.w(GlobalStaticVariables.LOG_TAG, "xlarge");
            this.currentSize = heightPixels / 6;
        } else if ((this.context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            Log.w(GlobalStaticVariables.LOG_TAG, "normall");
            this.currentSize = heightPixels / 4;
        } else if ((this.context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            Log.w(GlobalStaticVariables.LOG_TAG, "small");
            this.currentSize = heightPixels / 3;
        }

        gridView.setMinimumHeight(this.currentSize);
        gridView.setMinimumWidth(this.currentSize);

    }

    @Override
    public void onClick(View v) {
        boolean isFavorite = true;
        try {
            if (v.getId() == R.id.saveGridButton) {
                new AsyncRecipeSaveClass(this.context).execute(v.getTag(), !isFavorite);
            }else if(v.getId() == R.id.favoriteGridButton){
                new AsyncRecipeSaveClass(this.context).execute(v.getTag(), isFavorite);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class TempHolder {
        TextView gridItemTitle;
        TextView gridItemCategoryTitle;
        ImageView gridItemIcon;
        ImageButton gridSaveButton;
        ImageButton gridFavoriteButton;
    }

}
