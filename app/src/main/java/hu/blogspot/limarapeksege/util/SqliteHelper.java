package hu.blogspot.limarapeksege.util;

import hu.blogspot.limarapeksege.model.Category;
import hu.blogspot.limarapeksege.model.Recipe;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.Settings;
import android.util.Log;

public class SqliteHelper extends SQLiteOpenHelper {

	private static SqliteHelper instance = null;
	private static final String LOG = "LimaraP�ks�geSQL";

	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_NAME = "limaraDatabase";

	private static final String TABLE_CATEGORY = "categories";
	private static final String TABLE_RECIPE = "recipes";
	private static final String TABLE_NOTE = "notes";

	/* most common column names */
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";

	private static final String KEY_IS_DOWNLOADED = "isRecipesDownloaded";

	/* Recipe table column names */

	private static final String KEY_IS_SAVED = "isSaved";
	private static final String KEY_IS_FAVORITE = "isFavorite";
	private static final String KEY_LINK = "link";
	private static final String KEY_CATEGORY_ID = "categoryID";
	private static final String KEY_IS_NOTE = "isNote";
	private static final String KEY_NOTE_ID = "noteID";
	private static final String KEY_IMG_THUMBNAIL = "thumbnail";

	private static final String CREATE_TABLE_CATEGORY = "CREATE TABLE "
			+ TABLE_CATEGORY + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT,"
			+ KEY_IS_DOWNLOADED + " INTEGER)";

	private static final String CREATE_TABLE_RECIPE = "CREATE TABLE "
			+ TABLE_RECIPE + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT,"
			+ KEY_IS_SAVED + " INTEGER," + KEY_IS_FAVORITE + " INTEGER,"
			+ KEY_LINK + " TEXT," + KEY_CATEGORY_ID + " INTEGER," + KEY_IS_NOTE
			+ " INTEGER," + KEY_NOTE_ID + " INTEGER," + KEY_IMG_THUMBNAIL + " TEXT)";

	private static final String CREATE_TABLE_NOTE = "CREATE TABLE "
			+ TABLE_NOTE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ KEY_NAME + " TEXT)";

	private final String DROP_TABLE_SYNTAX = " DROP TABLE IF EXISTS ";

	public SqliteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	public static SqliteHelper getInstance(Context context) {
		if (instance == null) {
			instance = new SqliteHelper(context);
		}
		return instance;

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_CATEGORY);
		db.execSQL(CREATE_TABLE_RECIPE);
		db.execSQL(CREATE_TABLE_NOTE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL(DROP_TABLE_SYNTAX + TABLE_CATEGORY);
		db.execSQL(DROP_TABLE_SYNTAX + TABLE_RECIPE);
		db.execSQL(DROP_TABLE_SYNTAX + TABLE_NOTE);

		onCreate(db);

	}

	public void addCategory(Category category) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_NAME, category.getName());
		if (category.isRecipesDownloaded()) {
			values.put(KEY_IS_DOWNLOADED, 1);
		} else {
			values.put(KEY_IS_DOWNLOADED, 0);
		}

		db.insert(TABLE_CATEGORY, null, values);

		Log.w(LOG, "category added");
	}

	public Category getCategoryById(long category_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT * FROM " + TABLE_CATEGORY + " WHERE"
				+ KEY_ID + "=" + category_id;

		Log.w(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null) {
			c.moveToFirst();
		}

		Category category = new Category();
		category.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		category.setName(c.getString(c.getColumnIndex(KEY_NAME)));
		if (c.getInt(c.getColumnIndex(KEY_IS_DOWNLOADED)) == 1) {
			category.setRecipesDownloaded(true);
		} else {
			category.setRecipesDownloaded(false);
		}

		return category;
	}

	public Category getCategoryByName(String categoryName) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT * FROM " + TABLE_CATEGORY + " WHERE "
				+ KEY_NAME + " LIKE '" + categoryName + "'";

		Log.w(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null) {
			c.moveToFirst();
		}

		Category category = new Category();
		category.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		category.setName(c.getString(c.getColumnIndex(KEY_NAME)));
		if (c.getInt(c.getColumnIndex(KEY_IS_DOWNLOADED)) == 1) {
			category.setRecipesDownloaded(true);
		} else {
			category.setRecipesDownloaded(false);
		}

		return category;
	}

	public List<Category> getAllCategories() {
		List<Category> categories = new ArrayList<Category>();

		String selectAllQuery = "SELECT * FROM " + TABLE_CATEGORY;

		Log.w(LOG, selectAllQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectAllQuery, null);

		if (c.moveToFirst()) {
			do {
				Category category = new Category();
				category.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				category.setName(c.getString(c.getColumnIndex(KEY_NAME)));

				if (c.getInt(c.getColumnIndex(KEY_IS_DOWNLOADED)) == 1) {
					category.setRecipesDownloaded(true);
				} else {
					category.setRecipesDownloaded(false);
				}

				categories.add(category);
			} while (c.moveToNext());
		}

		return categories;
	}

	public void deleteCategoryTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(DROP_TABLE_SYNTAX + TABLE_CATEGORY);

		Log.w(LOG, "category table deleted");

		db.execSQL(CREATE_TABLE_CATEGORY);

	}

	public void deleteRecipeTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(DROP_TABLE_SYNTAX + TABLE_RECIPE);

		Log.w(LOG, "recipe table deleted");

		db.execSQL(CREATE_TABLE_RECIPE);

	}

	public void addRecipe(Recipe recipe) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_NAME, recipe.getRecipeName());
		values.put(KEY_IS_SAVED, recipe.isSaved());
		values.put(KEY_IS_FAVORITE, recipe.isFavorite());
		values.put(KEY_LINK, recipe.getRecipeURL());
		values.put(KEY_CATEGORY_ID, recipe.getCategory_id());
		values.put(KEY_IS_NOTE, recipe.isNoteAdded());
//		values.put(KEY_IMG_THUMBNAIL, recipe.getRecipeThumbnailUrl());

		db.insert(TABLE_RECIPE, null, values);

		Log.w(LOG, "recipe added: " + recipe.getRecipeName());

	}

	public List<Recipe> getAllRecipes() {
		List<Recipe> recipes = new ArrayList<Recipe>();

		String selectAllQuery = "SELECT * FROM " + TABLE_RECIPE;

		Log.w(LOG, selectAllQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectAllQuery, null);

		if (c.moveToFirst()) {
			do {
				Recipe recipe = new Recipe();
				recipe.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				recipe.setRecipeName(c.getString(c.getColumnIndex(KEY_NAME)));
				recipe.setCategory_id(c.getInt(c
						.getColumnIndex(KEY_CATEGORY_ID)));
				if (c.getInt(c.getColumnIndex(KEY_IS_SAVED)) == 1) {
					recipe.setSaved(true);
				} else {
					recipe.setSaved(false);
				}
				if (c.getInt(c.getColumnIndex(KEY_IS_FAVORITE)) == 1) {
					recipe.setFavorite(true);
				} else {
					recipe.setFavorite(false);
				}
				recipe.setRecipeURL(c.getString(c.getColumnIndex(KEY_LINK)));
				if (c.getInt(c.getColumnIndex(KEY_IS_NOTE)) == 1) {
					recipe.setNoteAdded(true);
				} else {
					recipe.setNoteAdded(false);
				}

				recipes.add(recipe);
			} while (c.moveToNext());
		}

		return recipes;
	}

	public List<Recipe> getRecipesByCategoryID(int categoryID) {
		Log.w(GlobalStaticVariables.LOG_TAG, "Get recipe list from db by category id");
		List<Recipe> recipes = new ArrayList<Recipe>();

		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT * FROM " + TABLE_RECIPE + " WHERE "
				+ KEY_CATEGORY_ID + " = " + categoryID + "";

		Log.w(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null) {
			c.moveToFirst();
		}

		if (c.moveToFirst()) {
			do {
				Recipe recipe = new Recipe();
				recipe.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				recipe.setRecipeName(c.getString(c.getColumnIndex(KEY_NAME)));
				if (c.getInt(c.getColumnIndex(KEY_IS_SAVED)) == 1) {
					recipe.setSaved(true);
				} else {
					recipe.setSaved(false);
				}
				if (c.getInt(c.getColumnIndex(KEY_IS_FAVORITE)) == 1) {
					recipe.setFavorite(true);
				} else {
					recipe.setFavorite(false);
				}
				recipe.setRecipeURL(c.getString(c.getColumnIndex(KEY_LINK)));
				if (c.getInt(c.getColumnIndex(KEY_IS_NOTE)) == 1) {
					recipe.setNoteAdded(true);
				} else {
					recipe.setNoteAdded(false);
				}

//				recipe.setRecipeThumbnailUrl(c.getString(c.getColumnIndex(KEY_IMG_THUMBNAIL)));
				recipes.add(recipe);
			} while (c.moveToNext());
		}

		return recipes;

	}

	public Recipe getRecipeByName(String recipeName){

		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT * FROM " + TABLE_RECIPE + " WHERE "
				+ KEY_NAME + " LIKE '" + recipeName + "'";

		Cursor c = db.rawQuery(selectQuery, null);
        Recipe recipe = null;

		Log.w(GlobalStaticVariables.LOG_TAG, c.toString());
		if (c != null && c.moveToFirst()) {

			recipe = new Recipe();
			recipe.setId(c.getInt(c.getColumnIndex(KEY_ID)));
			recipe.setRecipeName(c.getString(c.getColumnIndex(KEY_NAME)));
//			recipe.setRecipeThumbnailUrl(c.getString(c.getColumnIndex(KEY_IMG_THUMBNAIL)));
			if (c.getInt(c.getColumnIndex(KEY_IS_SAVED)) == 1) {
				recipe.setSaved(true);
			} else {
				recipe.setSaved(false);
			}
			if (c.getInt(c.getColumnIndex(KEY_IS_FAVORITE)) == 1) {
				recipe.setFavorite(true);
			} else {
				recipe.setFavorite(false);
			}
			recipe.setRecipeURL(c.getString(c.getColumnIndex(KEY_LINK)));
			if (c.getInt(c.getColumnIndex(KEY_IS_NOTE)) == 1) {
				recipe.setNoteAdded(true);
			} else {
				recipe.setNoteAdded(false);
			}
		}
        if(recipe == null){
			Log.w(GlobalStaticVariables.LOG_TAG, "No entry " + recipeName);
            try {
                throw new Exception("There is no recipe in the DB for that name " +  recipeName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return recipe;

	}

	public void updateRecipeDownloaded(long categoryID) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_IS_DOWNLOADED, 1);
		db.update(TABLE_CATEGORY, values, KEY_ID + "= ?",
				new String[] { String.valueOf(categoryID) });
		Log.w(GlobalStaticVariables.LOG_TAG, "category downloaded updated");
	}

	public void updateRecipeIsSaved(long recipeID, int isSavedValue) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_IS_SAVED, isSavedValue);
		db.update(TABLE_RECIPE, values, KEY_ID + "= ?",
				new String[] { String.valueOf(recipeID) });

		Log.w(GlobalStaticVariables.LOG_TAG, "recipe updated saved");

	}

	public void updateRecipeIsFavorite(long recipeID, int isFavoriteValue) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_IS_FAVORITE, isFavoriteValue);
		db.update(TABLE_RECIPE, values, KEY_ID + "= ?",
				new String[] { String.valueOf(recipeID) });

		Log.w(GlobalStaticVariables.LOG_TAG, "recipe updated favorite");

	}

	public void updateRecipeisNoteAdded(long recipeID, int isNoteAddedValue) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_IS_NOTE, isNoteAddedValue);
		db.update(TABLE_RECIPE, values, KEY_ID + "= ?",
				new String[] { String.valueOf(recipeID) });

		Log.w(GlobalStaticVariables.LOG_TAG, "recipe updated note");
	}

	public void deleteRecipe(String recipeName) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_RECIPE, KEY_NAME + " LIKE '" + recipeName + "'", null);
		Log.w(LOG, "recipe deleted");
	}

	public void closeDatabase() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen()) {
			db.close();
		}

		Log.w(GlobalStaticVariables.LOG_TAG, "database closed");

	}

}
