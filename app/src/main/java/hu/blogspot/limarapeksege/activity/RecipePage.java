package hu.blogspot.limarapeksege.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.adapters.items.DrawerListItem;
import hu.blogspot.limarapeksege.asyncs.AsyncFileCopy;
import hu.blogspot.limarapeksege.asyncs.AsyncRecipeSaveClass;
import hu.blogspot.limarapeksege.model.Recipe;
import hu.blogspot.limarapeksege.util.AnalyticsTracker;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.SqliteHelper;
import hu.blogspot.limarapeksege.util.handlers.recipe.RecipeActionsHandler;
import hu.blogspot.limarapeksege.util.ourWebViewClient;

@SuppressLint("NewApi")
public class RecipePage extends BaseActivity {
    private RecipeActionsHandler util;
    private static String URLsave;
    private static String NAMEsave;
    private Recipe currentRecipe;
    private SqliteHelper db;
    private Bundle bundleData;
    private static boolean isFavoriteRecipe;
    private AnalyticsTracker trackerApp;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_recipe_page);

        super.onCreateDrawer(preparedDrawerListItems(), getLocalClassName());

        bundleData = getIntent().getExtras();

        setClassVariables(bundleData);
        setWebViewClient();

        trackerApp = (AnalyticsTracker) getApplication();
        trackerApp.sendScreen(getString(R.string.analytics_screen_recipe));

        setTitle(currentRecipe.getRecipeName());

        trackerApp.sendTrackerEvent(getString(R.string.analytics_category_recipe), currentRecipe.getRecipeName());

        final TextView readingModeTextView = (TextView) findViewById(R.id.reading_mode_textView);

        readingModeTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                readingModeTextView.setVisibility(View.GONE);
                return false;
            }
        });


    }

    private List<DrawerListItem> preparedDrawerListItems() {
        DrawerListItem drawerListItemHome = new DrawerListItem(getString(R.string.nav_drawer_item_kezdolap), R.drawable.ic_menu_home, 0);
        DrawerListItem drawerListItemAbout = new DrawerListItem(getString(R.string.nav_drawer_item_about), R.drawable.ic_info_black_24dp, 1);
        List<DrawerListItem> items = new ArrayList<>();
        items.add(drawerListItemHome);
        items.add(drawerListItemAbout);

        return items;
    }

    private void setWebViewClient() {
        String urlToLoad;
        ourWebViewClient webViewClient;

        WebView wv = (WebView) findViewById(R.id.recipeWv);
        wv.setFocusableInTouchMode(false);
        wv.setFocusable(false);
        wv.setClickable(false);
        WebSettings settings = wv.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);

        if (bundleData.getBoolean("saved")) {

            urlToLoad = "file://" + Environment.getExternalStorageDirectory()
                    + GlobalStaticVariables.SAVED_RECIPE_PATH + currentRecipe.getId();
            wv.loadUrl(urlToLoad);

        } else if (bundleData.getBoolean("favorite")) {
            urlToLoad = "file://" + Environment.getExternalStorageDirectory()
                    + GlobalStaticVariables.FAVORITE_RECIPE_PATH + currentRecipe.getId();
            wv.loadUrl(urlToLoad);
        } else {
            urlToLoad = currentRecipe.getRecipeURL();

            urlToLoad = urlToLoad.replace("limarapeksege.blogspot.com",
                    "www.limarapeksege.hu");
            wv.loadUrl(urlToLoad);
        }

        webViewClient = new ourWebViewClient(urlToLoad, RecipePage.this);
        wv.setWebViewClient(webViewClient);
    }

    private void setClassVariables(Bundle bundleData) {

        util = new RecipeActionsHandler(getApplicationContext());

        URLsave = bundleData.getString("href");
        NAMEsave = bundleData.getString("name");

        try {
            isFavoriteRecipe = bundleData.getBoolean("favorite");

            db = SqliteHelper.getInstance(RecipePage.this);


            if (bundleData.getBoolean("saved") || bundleData.getBoolean("favorite")) {
                currentRecipe = db.getRecipeById(NAMEsave);
            } else {
                currentRecipe = db.getRecipeByName(NAMEsave);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        Bundle menubundle = getIntent().getExtras();
        MenuInflater inflater = getMenuInflater();
        boolean loaded = false;

        if (!menubundle.getBoolean("saved")
                && !menubundle.getBoolean("favorite")) {
            inflater.inflate(R.menu.recipe_page_online, menu);
            if (currentRecipe.isSaved()) {
                menu.findItem(R.id.menu_save).setIcon(R.drawable.save_colored);
            } else {
                menu.findItem(R.id.menu_save).setIcon(R.drawable.save);
            }

            if (currentRecipe.isFavorite()) {
                menu.findItem(R.id.menu_favorite).setIcon(
                        R.drawable.favorite_colored);
            } else {
                menu.findItem(R.id.menu_favorite).setIcon(R.drawable.favorite);
            }

            loaded = true;
        } else if (menubundle.getBoolean("saved")) {
            inflater.inflate(R.menu.recipe_page_saved, menu);

            if (currentRecipe.isFavorite()) {
                menu.findItem(R.id.menu_favorite).setIcon(
                        R.drawable.favorite_colored);
            } else {
                menu.findItem(R.id.menu_favorite).setIcon(R.drawable.favorite);
            }

            if (currentRecipe.isNoteAdded()) {
                menu.findItem(R.id.menu_note).setIcon(
                        R.drawable.note_pencil_colored);
            } else {
                menu.findItem(R.id.menu_note).setIcon(R.drawable.note_purple);
            }

            loaded = true;
        } else if (menubundle.getBoolean("favorite")) {
            inflater.inflate(R.menu.recipe_page_favorite, menu);
            if (currentRecipe.isNoteAdded()) {
                menu.findItem(R.id.menu_note).setIcon(
                        R.drawable.note_pencil_colored);
            } else {
                menu.findItem(R.id.menu_note).setIcon(R.drawable.note_purple);
            }
            loaded = true;
        }

        return loaded;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        Bundle menubundle = getIntent().getExtras();
        if (menubundle.getBoolean("favorite")) {
            if (currentRecipe.isNoteAdded()) {
                menu.findItem(R.id.menu_note).setIcon(
                        R.drawable.note_pencil_colored);
            } else {
                menu.findItem(R.id.menu_note).setIcon(R.drawable.note_purple);
            }
        }
        return super.onPrepareOptionsMenu(menu);

    }

    @SuppressLint("NewApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (!item.hasSubMenu()) {
            if (getString(R.string.menu_save) == item.getTitle()) {
                saveRecipe(item);
                trackerApp.sendTrackerEvent(getString(R.string.analytics_category_recipe), getString(R.string.analytics_save_recipe));
            } else if (getString(R.string.menu_favorite) == item.getTitle()) {
                saveRecipeToFavorite(item);
                trackerApp.sendTrackerEvent(getString(R.string.analytics_category_recipe), getString(R.string.analytics_move_recipte_to_favorite));
            } else if (getString(R.string.menu_delete) == item.getTitle()) {
                deleteDialogBox(RecipePage.this, currentRecipe.getRecipeName(), isFavoriteRecipe);
                trackerApp.sendTrackerEvent(getString(R.string.analytics_category_recipe), getString(R.string.analytics_delete_recipe));
            } else if (getString(R.string.menu_note) == item.getTitle()) {
                openNotePadActivity();
                trackerApp.sendTrackerEvent(getString(R.string.analytics_category_recipe), getString(R.string.analytics_note_open));
            } else if (getString(R.string.menu_reading_option) == item.getTitle()) {
                setScreenToReadingMode();
            } else if (getString(R.string.menu_share) == item.getTitle()) {
                shareRecipe();
            }
        }

        return true;
    }

    private void shareRecipe() {

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(currentRecipe.getRecipeURL()))
                .setImageUrl(Uri.parse(currentRecipe.getRecipeThumbnailUrl()))
                .setContentTitle(getString(R.string.app_name) + " - " + currentRecipe.getRecipeName())
                .build();

        shareDialog = new ShareDialog(this);
        shareDialog.show(content);

    }

    private void saveRecipe(MenuItem item) {
        Log.w("LimaraPeksege", URLsave + NAMEsave);
        try {
            new AsyncRecipeSaveClass(RecipePage.this).execute(currentRecipe, false);
            item.setIcon(R.drawable.save_colored);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void setScreenToReadingMode() {

        TextView readingModeTextView = (TextView) findViewById(R.id.reading_mode_textView);
        Animation animation = null;


        if ((getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) != 0) { // flag has been already set
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            readingModeTextView.setVisibility(View.GONE);
            animation = AnimationUtils.loadAnimation(this,
                    R.anim.slide_down);
            trackerApp.sendTrackerEvent(getString(R.string.analytics_category_recipe), getString(R.string.analytics_reading_mode_disabled));

        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            readingModeTextView.setVisibility(View.VISIBLE);
            animation = AnimationUtils.loadAnimation(this,
                    R.anim.slide_up);
            trackerApp.sendTrackerEvent(getString(R.string.analytics_category_recipe), getString(R.string.analytics_reading_mode_enabled));

        }
        readingModeTextView.startAnimation(animation);
    }

    private void saveRecipeToFavorite(MenuItem item) {
        File fileSource = new File(Environment.getExternalStorageDirectory()
                + GlobalStaticVariables.SAVED_RECIPE_PATH + currentRecipe.getId());

        try {
            if (fileSource.isFile()) {
                Log.w("LimaraPeksege", "favorite save");
                File fileDestination = new File(
                        Environment.getExternalStorageDirectory()
                                + GlobalStaticVariables.FAVORITE_RECIPE_PATH
                                + currentRecipe.getId());

                File webpageDirectory = new File(
                        Environment.getExternalStorageDirectory()
                                + GlobalStaticVariables.FAVORITE_RECIPE_PATH);
                if (!webpageDirectory.exists())
                    webpageDirectory.mkdirs();
                AsyncFileCopy asyncFileCopy = new AsyncFileCopy(RecipePage.this);
                asyncFileCopy.execute(fileSource, fileDestination, currentRecipe.getId());
                if (asyncFileCopy.get()) {
                    Toast.makeText(this, "Kedvencekhez áthelyezve",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Log.w("LimaraPeksege", "favorite save from url");
                new AsyncRecipeSaveClass(RecipePage.this).execute(currentRecipe, true);
            }
            item.setIcon(R.drawable.favorite_colored);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void openNotePadActivity() {
        Class<?> notePad = null;
        try {
            notePad = Class.forName(GlobalStaticVariables.NOTEPAD_CLASS);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Intent openNotePad = new Intent(RecipePage.this, notePad);
        Bundle sendName = new Bundle();
        sendName.putString("name", currentRecipe.getRecipeName());

        openNotePad.putExtras(sendName);

        startActivity(openNotePad);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (currentRecipe != null) {
            refreshCurrentRecipe(this.currentRecipe);
            if (Build.VERSION.SDK_INT >= 11) {
                invalidateOptionsMenu();
            }
        }

    }

    private void refreshCurrentRecipe(Recipe currentRecipe) {
        this.currentRecipe = db.getRecipeById(currentRecipe.getId());
        Log.w("LimaraPeksege", this.currentRecipe.isNoteAdded()
                + " is note added on resume");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private boolean isNetworkAvailable() { // ellen�rizz�k van-e internet el�r�s
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private void deleteDialogBox(Context context, String recipeName,
                                 final boolean isFavorite) {
        AlertDialog.Builder dialogBox = new AlertDialog.Builder(context);

        dialogBox.setTitle(recipeName + " " + getString(R.string.delete_title));

        dialogBox.setMessage(getString(R.string.confirm_delete));
        dialogBox.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        util.deleteRecipe(currentRecipe, isFavorite);

                        Intent intent = new Intent(RecipePage.this,
                                SavedRecipes.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtras(bundleData);
                        startActivity(intent);
                    }
                });

        dialogBox.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = dialogBox.create();
        dialog.show();
    }

}
