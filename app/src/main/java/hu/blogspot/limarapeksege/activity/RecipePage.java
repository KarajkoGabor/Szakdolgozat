package hu.blogspot.limarapeksege.activity;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.asyncs.AsyncFileCopy;
import hu.blogspot.limarapeksege.asyncs.AsyncRecipeSaveClass;
import hu.blogspot.limarapeksege.model.Recipe;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.SqliteHelper;
import hu.blogspot.limarapeksege.util.ourWebViewClient;
import hu.blogspot.limarapeksege.util.handlers.recipe.RecipeActionsHandler;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class RecipePage extends Activity {
	private RecipeActionsHandler util;
	private static String URLsave;
	private static String NAMEsave;
	private Recipe currentRecipe;
	private SqliteHelper db;
	private Bundle bundleData;
	private static boolean isFavoriteRecipe;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_page);

		bundleData = getIntent().getExtras();

		setClassVariables(bundleData);
		setWebViewClient();

		setTitle(NAMEsave);
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
					+ GlobalStaticVariables.SAVED_RECIPE_PATH + NAMEsave;
			wv.loadUrl(urlToLoad);

		} else if (bundleData.getBoolean("favorite")) {
			urlToLoad = "file://" + Environment.getExternalStorageDirectory()
					+ GlobalStaticVariables.FAVORITE_RECIPE_PATH + NAMEsave;
			wv.loadUrl(urlToLoad);
			Log.w("LimaraPeksege", "favorite");
		} else {
			urlToLoad = URLsave;

			Log.w("LimaraPeksege", urlToLoad + "urlToLoad");
			urlToLoad = urlToLoad.replace("limarapeksege.blogspot.com",
					"www.limarapeksege.hu");
			Log.w("LimaraPeksege", urlToLoad + "urlToLoad");
			wv.loadUrl(urlToLoad);
			Log.w("LimaraPeksege", "url_web");
		}

		webViewClient = new ourWebViewClient(urlToLoad, RecipePage.this);
		wv.setWebViewClient(webViewClient);
	}

	private void setClassVariables(Bundle bundleData) {

		util = new RecipeActionsHandler(getApplicationContext());

		URLsave = bundleData.getString("href");
		NAMEsave = bundleData.getString("name");
		isFavoriteRecipe = bundleData.getBoolean("favorite");

		db = SqliteHelper.getInstance(RecipePage.this);
		currentRecipe = db.getRecipeByName(NAMEsave);
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
			Log.w("LimaraPeksege", db.getRecipeByName(NAMEsave).isNoteAdded()
					+ " is note added");
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
		Log.w("LimaraPeksege", currentRecipe.isNoteAdded()
				+ "is note added onprepare");
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
		if (item.hasSubMenu() == false) {
			if (getString(R.string.menu_save) == item.getTitle()) {
				saveRecipe(item);
			} else if (getString(R.string.menu_favorite) == item.getTitle()) {
				saveRecipeToFavorite(item);
			} else if (getString(R.string.menu_delete) == item.getTitle()) {
				dialogBox(RecipePage.this, NAMEsave, isFavoriteRecipe);
			} else if (getString(R.string.menu_note) == item.getTitle()) {
				openNotePadActivity();
			}
		}

		return true;
	}

	private void saveRecipe(MenuItem item) {
		Log.w("LimaraPeksege", URLsave + NAMEsave);
		try {
			new AsyncRecipeSaveClass(RecipePage.this).execute(URLsave,
					NAMEsave, false);
			item.setIcon(R.drawable.save_colored);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void saveRecipeToFavorite(MenuItem item) {
		File fileSource = new File(Environment.getExternalStorageDirectory()
				+ GlobalStaticVariables.SAVED_RECIPE_PATH + NAMEsave);

		try {
			if (fileSource.isFile()) {
				Log.w("LimaraPeksege", "favorite save");
				File fileDestination = new File(
						Environment.getExternalStorageDirectory()
								+ GlobalStaticVariables.FAVORITE_RECIPE_PATH
								+ NAMEsave);

				File webpageDirectory = new File(
						Environment.getExternalStorageDirectory()
								+ GlobalStaticVariables.FAVORITE_RECIPE_PATH);
				if (!webpageDirectory.exists())
					webpageDirectory.mkdirs();
				AsyncFileCopy asyncFileCopy = new AsyncFileCopy(RecipePage.this);
				asyncFileCopy.execute(fileSource, fileDestination, NAMEsave);
				if (asyncFileCopy.get()) {
					Toast.makeText(this, "Kedvencekhez �thelyezve",
							Toast.LENGTH_LONG).show();
				}
			} else {
				Log.w("LimaraPeksege", "favorite save from url");
				new AsyncRecipeSaveClass(RecipePage.this).execute(URLsave,
						NAMEsave, true);
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
		sendName.putString("name", NAMEsave);

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
		this.currentRecipe = db.getRecipeByName(currentRecipe.getRecipeName());
		Log.w("LimaraPeksege", this.currentRecipe.isNoteAdded()
				+ " is note added on resume");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
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

	private void dialogBox(Context context, String recipeName,
			final boolean isFavorite) {
		AlertDialog.Builder dialogBox = new AlertDialog.Builder(context);

		dialogBox.setTitle(recipeName + " " + getString(R.string.delete_title));

		dialogBox.setMessage(getString(R.string.confirm_delete));
		dialogBox.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						util.deleteRecipe(NAMEsave, isFavorite);

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
