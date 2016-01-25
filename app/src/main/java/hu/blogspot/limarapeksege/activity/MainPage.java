package hu.blogspot.limarapeksege.activity;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.adapters.items.DrawerListItem;
import hu.blogspot.limarapeksege.asyncs.AsyncPrepareRecipeDatas;
import hu.blogspot.limarapeksege.util.AnalyticsTracker;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.XmlParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

@SuppressLint("NewApi")
public class MainPage extends BaseActivity implements OnClickListener {

	protected static final String LIMARA_URL = "http://limarapeksegetartalom.blogspot.hu/";
	private Bitmap onlineIcon;
	private Bitmap savedIcon;
	private Bitmap favoritesIcon;
	private Bitmap loafIcon;
	private Bitmap searchIcon;
	private Bitmap newsIcon;
	private final String USER_AGENT = "Mozilla/5.0";
	private Tracker tracker;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

		DrawerListItem drawerListItem = new DrawerListItem(getString(R.string.nav_drawer_item_kezdolap), R.drawable.ic_menu_home);
		List<DrawerListItem> items = new ArrayList<>();
		items.add(drawerListItem);

        super.onCreateDrawer(items, getLocalClassName());
		List<String> mainMenuList; // men� lista

		if (isWifiConnected() && !isAllRecipesPreDownloaded()) {
			wifiDialogBox(MainPage.this);
		}

		setIcons();

		mainMenuList = setMainMenuList();
		setTextViews(mainMenuList);

		AnalyticsTracker trackerApp = (AnalyticsTracker) getApplication();
		tracker = trackerApp.getDefaultTracker();

	}

	private void setTextViews(List<String> mainMenuList) {
		TextView onlineRecipes = (TextView) findViewById(R.id.mainTextOnline);
		TextView savedRecipes = (TextView) findViewById(R.id.mainTextSaved);
		TextView favoriteRecipes = (TextView) findViewById(R.id.mainTextFavorites);
		TextView loafMaking = (TextView) findViewById(R.id.mainTextLoaf);
		TextView searchRecipes = (TextView) findViewById(R.id.mainTextSearch);
		TextView newPosts = (TextView) findViewById(R.id.mainTextNews);

		onlineRecipes.setOnClickListener(this);
		onlineRecipes.setText(mainMenuList.get(0));
		savedRecipes.setOnClickListener(this);
		savedRecipes.setText(mainMenuList.get(1));
		favoriteRecipes.setOnClickListener(this);
		favoriteRecipes.setText(mainMenuList.get(2));
		loafMaking.setOnClickListener(this);
		loafMaking.setText(mainMenuList.get(3));
		searchRecipes.setOnClickListener(this);
		searchRecipes.setText(mainMenuList.get(4));
		newPosts.setOnClickListener(this);
		newPosts.setText(mainMenuList.get(5));

	}

	private boolean isAllRecipesPreDownloaded() {

		SharedPreferences savedSettings = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean isAllRecipeDownloaded;

		if (savedSettings.getBoolean("All_recipe_downloaded", false)) {
			isAllRecipeDownloaded = true;
		} else {
			isAllRecipeDownloaded = false;
		}

		return isAllRecipeDownloaded;
	}

	public void onClick(View v) {

		try {
			switch (v.getId()) {

			case R.id.mainTextOnline:
				if (isNetworkAvailable() == false) {
					throw new Exception();
				} else {
					startNewActivity(
							GlobalStaticVariables.RECIPE_CATEGORY_CLASS, 0);
				}
				break;
			case R.id.mainTextSaved:
				startNewActivity(GlobalStaticVariables.SAVED_RECIPES_CLASS, 1);
				break;
			case R.id.mainTextFavorites:
				startNewActivity(GlobalStaticVariables.SAVED_RECIPES_CLASS, 2);
				break;
			case R.id.mainTextLoaf:
				startNewActivity(GlobalStaticVariables.LOAF_MAKING_CLASS, 3);
				break;
			case R.id.mainTextSearch:
				if (isNetworkAvailable() == false) {
					throw new Exception();
				} else {
					startNewActivity(GlobalStaticVariables.RECIPE_SEARCH_CLASS,
							5);
				}
				break;
			case R.id.mainTextNews:
				if (isNetworkAvailable() == false) {
					throw new Exception();
				} else {
					startNewActivity(GlobalStaticVariables.NEWS_CLASS, 6);
				}
				break;

			}

		} catch (Exception e) {
			Toast.makeText(this, R.string.no_connection,
					Toast.LENGTH_LONG).show();
		}
	}

	private void sendTrackerEvent(String eventCategoryName, String eventActionName){
		tracker.send(new HitBuilders.EventBuilder()
				.setCategory(eventCategoryName)
				.setAction(eventActionName)
				.build());
	}

	private void startNewActivity(String className, int position) {
		Bundle sendData = new Bundle();
		Class<?> newClass = null;
		try {
			newClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent openRecipeCategory = new Intent(this, newClass);
		sendData.putInt("position", position);

		openRecipeCategory.putExtras(sendData);

		sendTrackerEvent(className, getString(R.string.analytics_choose_main_menu));

		this.startActivity(openRecipeCategory);
	}

	private void setIcons() {

		onlineIcon = BitmapFactory.decodeResource(getResources(),
				R.drawable.online_icon);
		savedIcon = BitmapFactory.decodeResource(getResources(),
				R.drawable.saved_icon);
		favoritesIcon = BitmapFactory.decodeResource(getResources(),
				R.drawable.favorite_icon);
		loafIcon = BitmapFactory.decodeResource(getResources(),
				R.drawable.loaf_icon);
		searchIcon = BitmapFactory.decodeResource(getResources(),
				R.drawable.search_icon);
		newsIcon = BitmapFactory.decodeResource(getResources(),
				R.drawable.news_icon);

	}

	private List<String> setMainMenuList() {
		ArrayList<? extends Object> mainMenuList = new ArrayList<String>();
		try {
			XmlParser parser = new XmlParser();
			XmlPullParser xpp = getResources().getXml(R.xml.mainmenulist);
			mainMenuList = parser.parseXml(xpp,"main_menu");
		} catch (Throwable t) {
			Toast.makeText(this, "Request failed: " + t.toString(),
					Toast.LENGTH_LONG).show();
		}

		return (List<String>) mainMenuList;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main_page, menu);

        super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (getString(R.string.menu_about) == item.getTitle()) {
			openAboutDialog();
		}
		return super.onOptionsItemSelected(item);
	}

	private void openAboutDialog() {
		AlertDialog.Builder newAboutDialog = new AlertDialog.Builder(this);
		View about_dialog_layout = getLayoutInflater().inflate(
				R.layout.about_dialog_layout, null, false);

		newAboutDialog.setTitle(R.string.about_title);
		newAboutDialog.setIcon(R.drawable.ic_launcher);
		newAboutDialog.setView(about_dialog_layout);
		newAboutDialog.setNegativeButton(R.string.close,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		AlertDialog dialog = newAboutDialog.create();
		dialog.show();
	}

	private boolean isNetworkAvailable() { // ellen�rizz�k van-e internet el�r�s
		ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	private boolean isWifiConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = null;
		wifi = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifi.isConnected()) {
			return true;
		} else {
			return true;
		}

	}

	private void wifiDialogBox(final Context context) {
		AlertDialog.Builder wifiConnectedBuilder = new AlertDialog.Builder(
				context);

		wifiConnectedBuilder.setTitle(R.string.prepare_recipes);

		wifiConnectedBuilder.setMessage(R.string.wifi_connected_dialog_content);
		wifiConnectedBuilder.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						new AsyncPrepareRecipeDatas(context,MainPage.this
								).execute(MainPage.LIMARA_URL);
						setAllRecipesDownloadedSettings();
					}
				});

		wifiConnectedBuilder.setNegativeButton(R.string.not_now,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		AlertDialog dialog = wifiConnectedBuilder.create();
		dialog.show();

	}

	private void setAllRecipesDownloadedSettings() {
		SharedPreferences savedSettings = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = savedSettings.edit();
		editor.putBoolean("All_recipe_downloaded", true);
		editor.commit();

	}

	@Override
	public void onResume() {
		super.onResume();
		tracker.setScreenName(getString(R.string.analytics_main_page_screen));
		tracker.send(new HitBuilders.ScreenViewBuilder().build());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.w(GlobalStaticVariables.LOG_TAG, "onActivityResult session");
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

}
