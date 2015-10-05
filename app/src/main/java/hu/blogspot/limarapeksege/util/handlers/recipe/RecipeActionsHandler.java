package hu.blogspot.limarapeksege.util.handlers.recipe;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.blogger.Blogger;
import com.google.api.services.blogger.model.Post;
import com.google.api.services.blogger.model.PostList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.model.Category;
import hu.blogspot.limarapeksege.model.Recipe;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;
import hu.blogspot.limarapeksege.util.SqliteHelper;
import hu.blogspot.limarapeksege.util.XmlParser;
import hu.blogspot.limarapeksege.util.handlers.file.FileHandler;
import hu.blogspot.limarapeksege.util.handlers.image.ImageHandler;

public class RecipeActionsHandler {

	private final String MAIN_DIRECTORY = "/LimaraPeksege";
	private final String SAVED_RECIPES = "SavedRecipes";
	private final String FAVORITE_RECIPES = "FavoriteRecipes";
	private SqliteHelper db;
	private Context context;
	private HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private JacksonFactory JSON_FACTORY = new JacksonFactory();
	private GoogleCredential credential = new GoogleCredential();
	private Blogger blogger = new Blogger.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName("LimaraApp").build();
	private Blogger.Posts.List postsListAction;

	public RecipeActionsHandler(Context context) {
		this.context = context;
		db = SqliteHelper.getInstance(context);
	}

	/**
	 * Recept kateg�ri�k list�z�sra j�
	 * 
	 * @param urlName
	 *            az URL amit megvizsg�lunk
	 * 
	 * @return a lista amit a vizsg�lat sor�n kaptunk
	 */
	public ArrayList<Category> categoryParser(String urlName) {

		// TODO ez az eg�sz cucc mehet resource fileba is

		ArrayList<Category> categoryList = new ArrayList<Category>(); // v�gs� lista
															// oldal
		XmlParser parser = new XmlParser();
		String parseMode = "category";
		XmlPullParser xpp = context.getResources().getXml(R.xml.categories);
		
		try {
			categoryList = (ArrayList<Category>) parser.parseXml(xpp, parseMode);
			// abc sorrendbe rakjuk
//			categoryList = listSorter(categoryList);

			Collections.sort(categoryList, new Comparator<Category>() {
				@Override
				public int compare(Category category1, Category category2) {
					return category1.getName().compareTo(category2.getName());
				}
			});

			Log.w("LimaraPeksege", "Web Parsing succeed");
			Log.w("LimaraPeksege", "Recipe Category save begin");

			for (int i = 0; i < categoryList.size(); i++) {
//				Category category = new Category();
				Log.w("LimaraPeksege", "Actual category" + categoryList.get(i).getName());
//				category.setName(categoryList.get(i));
				db.addCategory(categoryList.get(i));
			}

			Log.w("LimaraPeksege", "Recipe Category save succeed");

			db.closeDatabase();
		} catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return categoryList;
	}

	/**
	 * Mivel az oldal vizsg�l�sa(a weboldal form�zotts�ga miatt) sor�n sok
	 * felesleges " " �s egy�b elemek is beker�lnek, ez�rt ez a met�dus ezeket a
	 * felesleges elemeket kit�rli
	 * 
	 * @param list
	 *            egy lista amib�l a felesleges elemeket ki akarjuk t�r�lni
	 * @return a letisztult lista
	 */
	public ArrayList<String> ListClear(ArrayList<String> list) {

		for (int i = 0; i < list.size(); i++) {
			// Log.w("myApp", recipeTitles.get(i).toString());
			if (list.get(i).toString().equals("")
					|| list.get(i).toString().equals("|")
					|| list.get(i).toString().equals("skip to main")
					|| list.get(i).toString().equals("skip to sidebar")
					|| list.get(i).toString().equals("Tartalomjegyz�k")
					|| list.get(i).toString().equals("<<")
					|| list.get(i).toString()
							.equals("Vissza a Limara P�ks�g f�oldalra")) {
				list.remove(i);
				i = i - 1; // l�pj�nk vissza �jra az elemre
			}

		}

		return list;
	}

	public List<Recipe> gatherRecipeData(String categoryLabelName , int categoryID){

		Log.w(GlobalStaticVariables.LOG_TAG , "Gathering recipe data started");

		List<Recipe> recipeList = new ArrayList<Recipe>();

		int numberOfRecipesInDB = db.getRecipesByCategoryID(categoryID).size();

		if (numberOfRecipesInDB == 0) { //ha nincs a kategóriához mentve recept
			recipeList = gatherRecipeDataFromAPI(categoryLabelName, categoryID);
			numberOfRecipesInDB = db.getRecipesByCategoryID(categoryID).size();
			if(numberOfRecipesInDB == recipeList.size()){
				db.updateRecipeDownloaded(categoryID);
			}

		}else if(!db.getCategoryById(categoryID).isRecipesDownloaded()){
			recipeList = gatherRecipeDataFromAPI(categoryLabelName,categoryID);
			for(Recipe recipe: recipeList){
				Recipe recipeFromDb = db.getRecipeByName(recipe
						.getRecipeName());
				if (recipeFromDb == null) {
					db.addRecipe(recipe);
				} else if (recipeFromDb != null
						&& recipeFromDb.getCategory_id() == 0
						&& recipeFromDb.getNote_id() == 0) {
					Log.w("LimaraPeksege", "START: Recipe is saved again: " + recipe.getRecipeName());
					recipe.setSaved(recipeFromDb.isSaved());
					recipe.setFavorite(recipeFromDb.isFavorite());
					recipe.setNoteAdded(recipeFromDb.isNoteAdded());
					db.deleteRecipe(recipe.getRecipeName());
					db.addRecipe(recipe);
					Log.w("LimaraPeksege", "END: Recipe is saved again");
				}
			}
		}

		else{
			recipeList = db.getRecipesByCategoryID(categoryID);
		}

		if(recipeList.size() == db.getRecipesByCategoryID(categoryID).size()){
			db.updateRecipeDownloaded(categoryID);
		}

		db.closeDatabase();
		Log.w(GlobalStaticVariables.LOG_TAG, "Gathering recipe data ended");
		return recipeList;
	}

	private List<Recipe> gatherRecipeDataFromAPI(String categoryLabelName, int categoryID) {

		Log.w(GlobalStaticVariables.LOG_TAG, "Recipe datas from API");

		List<Recipe> recipeList = new ArrayList<Recipe>();

		String BLOG_ID = "3172210115883978627";
		int pageCount = 0;

		try {
			postsListAction = blogger.posts().list(BLOG_ID);
			postsListAction.setKey("AIzaSyB0MQ_sdri3PY2xrYmFJvK_FNRPojNeZg8");
			postsListAction.setFields("items(title,url), nextPageToken");
			postsListAction.setLabels(categoryLabelName);
			postsListAction.setMaxResults((long) 499);
			PostList posts = postsListAction.execute();

			// Now we can navigate the response.
			while (posts.getItems() != null && !posts.getItems().isEmpty()) {
				for (Post post : posts.getItems()) {
					Recipe tempRecipe = new Recipe();
					tempRecipe.setRecipeName(post.getTitle());
					tempRecipe.setRecipeURL(post.getUrl());
					tempRecipe.setFavorite(false);
					tempRecipe.setNoteAdded(false);
					tempRecipe.setSaved(false);
					tempRecipe.setCategory_id(categoryID);
					Log.w(GlobalStaticVariables.LOG_TAG, "Recipe got from API: "+ tempRecipe.getRecipeName() );
					db.addRecipe(tempRecipe);
					recipeList.add(tempRecipe);
				}


				String pageToken = posts.getNextPageToken();
				if (pageToken == null || ++pageCount >= 5) {
					break;
				}
				postsListAction.setPageToken(pageToken);
				posts = postsListAction.execute();

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return recipeList;
	}


	/**
	 * A receptek nev�t list�zza ki. Web form�zotts�ga miatt csak �gy lehet
	 * megoldani.
	 * 
	 * @param category
	 *            A kiv�lasztott kateg�ria
	 * @param URL
	 *            a weboldal URL-je
	 * @return egy receptlista amiben elt�roljuk a nevet �s a recept linkj�t ez
	 *         a k�s�bbi xml f�jlba ment�shez fontos
	 */
	public ArrayList<String> recipeTitleParser(String category, String URL,
			int categoryID) {

		Document document = null;
		ArrayList<String> recipeTitles = new ArrayList<String>(); // lista a
																	// c�meknek
		ArrayList<Recipe> recipes;

		// JSOUP parsing
		try {
			document = Jsoup.connect(URL).get();

			Elements elements = document.select("span,a,strong");
			recipes = getRecipesFromUrl(category, elements);

			for (Recipe recipe : recipes) {

				recipe.setSaved(false);
				recipe.setFavorite(false);
				recipe.setNoteAdded(false);
				recipe.setCategory_id(categoryID);
				if (db.getAllRecipes().size() != 0) {
					Recipe recipeFromDb = db.getRecipeByName(recipe
							.getRecipeName());
					if (recipeFromDb == null) {
						db.addRecipe(recipe);
					} else if (recipeFromDb != null
							&& recipeFromDb.getCategory_id() == 0
							&& recipeFromDb.getNote_id() == 0) {
						Log.w("LimaraPeksege", recipe.getRecipeName());
						recipe.setSaved(recipeFromDb.isSaved());
						recipe.setFavorite(recipeFromDb.isFavorite());
						recipe.setNoteAdded(recipeFromDb.isNoteAdded());
						db.deleteRecipe(recipe.getRecipeName());
						db.addRecipe(recipe);
						Log.w("LimaraPeksege", "from saved added");
					}
				} else {
					db.addRecipe(recipe);
				}
				recipeTitles.add(recipe.getRecipeName());
			}

			db.updateRecipeDownloaded(categoryID);
			FileHandler handler = new FileHandler();
			handler.writeToCSVFile(recipes, category);

			db.closeDatabase();
			Log.w("LimaraPeksege", "recipe list succeed");

		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return recipeTitles;
	}

	private ArrayList<Recipe> getRecipesFromUrl(String category,
			Elements elements) throws XmlPullParserException, IOException {
		ArrayList<Recipe> recipes = new ArrayList<Recipe>();
		List<Category> categories = new ArrayList<Category>();
		boolean isAddeble = false;
		XmlParser parser = new XmlParser();
		XmlPullParser xpp = context.getResources().getXml(R.xml.categories);
		String mode = "category";
		categories = (List<Category>) parser.parseXml(xpp, mode);

		for (int i = 0; i < categories.size(); i++) {
			if (category.equals(categories.get(i).getName())) { // megn�zz�k a kateg�ri�t
				for (Element e : elements) {
					if (e.ownText().equals(categories.get(i + 1).getName())) {// fix
																	// t�mpontok,
																	// a
						// weboldal
						// form�zotts�ga miatt
						isAddeble = false;
						break;
					} else if (e.ownText().equals(categories.get(i).getName())) {
						isAddeble = true;
					} else if (isAddeble) {
						Recipe recipe = new Recipe();
						recipe.setRecipeName(e.ownText());
						recipe.setRecipeURL(modifyRecipeUrl(e.absUrl("href")));
						recipes.add(recipe);
					}
				}
			}

		}

		return recipes;
	}

	private String modifyRecipeUrl(String originalUrl) {
		String modifiedUrl = null;

		if (originalUrl.contains("limarapeksege.blogspot.com")) {
			modifiedUrl = originalUrl.replace("limarapeksege.blogspot.com",
					"www.limarapeksege.hu");
		} else if (originalUrl.contains("limarapeksege.blogspot.hu")) {

			modifiedUrl = originalUrl.replace("limarapeksege.blogspot.hu",
					"www.limarapeksege.hu");
		} else {
			modifiedUrl = originalUrl;
		}

		return modifiedUrl;
	}

	/**
	 * Sorba rendezi a list�t
	 * 
	 * @param list
	 *            input lista
	 * @return sorbarendezett lista
	 */
	public ArrayList<String> listSorter(ArrayList<String> list) {

		Locale hungarian = new Locale("hu_HU"); // magyar abc
		Collator hungarianCollator = Collator.getInstance(hungarian);

		Collections.sort(list, hungarianCollator);

		return list;
	}

	/**
	 * Elmenti egy recept oldal�t, egy file-ba
	 * 
	 * @param URL
	 *            recept linkje
	 * @param NAME
	 *            recept neve
	 * @param isFavorite
	 * @throws Exception
	 */
	public void saveRecipePage(String URL, String NAME, Boolean isFavorite)
			throws Exception {

		Log.w("LimaraPeksege", "Save the recipe started");
		String htmlText = null;
		URI website = new URI(URL);
		ImageHandler imageHandler = new ImageHandler();
		FileHandler fileHandler = new FileHandler();

		htmlText = parseRecipeContentFromWebsite(website).toString();
		htmlText = imageHandler.setImageDivs(htmlText);
		String finalText = imageHandler.saveImage(htmlText, NAME, isFavorite);

		if (!isFavorite) { // ha nem kedvenc
			fileHandler.writeToFile(finalText, SAVED_RECIPES,
					GlobalStaticVariables.SAVED_RECIPE_PATH, NAME);
			db.updateRecipeIsSaved(db.getRecipeByName(NAME).getId(), 1);
			Log.w("LimaraPeksege", db.getRecipeByName(NAME).isSaved()
					+ " is saved");
			db.closeDatabase();
		} else { // ha kedvenc
			fileHandler.writeToFile(finalText, FAVORITE_RECIPES,
					GlobalStaticVariables.FAVORITE_RECIPE_PATH, NAME);
			db.updateRecipeIsFavorite(db.getRecipeByName(NAME).getId(), 1);
			Log.w("LimaraPeksege", db.getRecipeByName(NAME).isSaved()
					+ " is favorite");
		}

		db.closeDatabase();
		Log.w("LimaraPeksege", "Save the recipe succeed");

	}

	private StringBuffer parseRecipeContentFromWebsite(URI website)
			throws IllegalStateException, IOException {
		boolean contain = false;
		int brCounter = 0;
		boolean tooMuchBr = false;
		final HttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet();

		request.setURI(website);
		HttpResponse response = httpClient.execute(request);
		BufferedReader in = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		StringBuffer sb = new StringBuffer("");
		String l = "";
		String nl = System.getProperty("line.separator");
		sb.append("<style> .post-body { margin: 1em; } </style>");
		sb.append("<body bgcolor=\"#f7dbe3\">");

		while ((l = in.readLine()) != null) {
			if (l.contains("entry-content")) {
				contain = true;
			} else if (l.contains("post-footer") && contain == true) {
				contain = false;
			}
			if (contain) {
				if (l.equals("<br />")) {
					brCounter++;
				} else {
					brCounter = 0;
					tooMuchBr = false;
				}
				if (brCounter > 2) {
					tooMuchBr = true;
				}
			}

			if (contain && !tooMuchBr) {

				sb.append(l + nl);
			}
		}

		sb.append("</body>");
		in.close();
		return sb;
	}

	public void deleteRecipe(String name, boolean isFavorite) {
		// TODO savedRecipes

		File webpageDirectory = new File(
				Environment.getExternalStorageDirectory() + MAIN_DIRECTORY,
				SAVED_RECIPES);
		File savedRecipe;
		if (!webpageDirectory.exists())
			webpageDirectory.mkdirs();
		if (!isFavorite) {
			savedRecipe = new File(Environment.getExternalStorageDirectory()
					+ GlobalStaticVariables.SAVED_RECIPE_PATH, name);
		} else {
			savedRecipe = new File(Environment.getExternalStorageDirectory()
					+ GlobalStaticVariables.FAVORITE_RECIPE_PATH, name);
		}

		if (savedRecipe.exists()) {
			if (!isFavorite) {
				db.updateRecipeIsSaved(db.getRecipeByName(name).getId(), 0);
				Log.w("LimaraPeksege", db.getRecipeByName(name).isFavorite()
						+ " after delete is favorite");
				Log.w("LimaraPeksege", db.getRecipeByName(name).isSaved()
						+ " after delete is saved");
			} else {
				db.updateRecipeIsFavorite(db.getRecipeByName(name).getId(), 0);
				Log.w("LimaraPeksege", db.getRecipeByName(name).isFavorite()
						+ " after delete is favorite");
				Log.w("LimaraPeksege", db.getRecipeByName(name).isSaved()
						+ " after delete is saved");
			}
			savedRecipe.delete();
		}
		db.closeDatabase();
	}

	public void updateSavedRecipes(String recipeName, boolean isFavorite) {

		boolean saved = false;
		boolean favorite = false;
		boolean isNoteAdded = false;
		File notePathFile;
		File recipePath;
		notePathFile = new File(Environment.getExternalStorageDirectory()
				+ GlobalStaticVariables.FAVORITE_RECIPE_PATH + "/Notes/"
				+ recipeName + ".txt");
		if (isFavorite) {
			favorite = true;
			Log.w("LimaraPeksege", "kedvencek");
			recipePath = new File(Environment.getExternalStorageDirectory()
					+ GlobalStaticVariables.SAVED_RECIPE_PATH + "/"
					+ recipeName);
			if (recipePath.exists()) {
				saved = true;
			}
		} else {
			Log.w("LimaraPeksege", "mentett");
			saved = true;
			recipePath = new File(Environment.getExternalStorageDirectory()
					+ GlobalStaticVariables.FAVORITE_RECIPE_PATH + "/"
					+ recipeName);
			if (recipePath.exists()) {
				Log.w("LimaraPeksege", "true");
				favorite = true;
			}
		}

		if (notePathFile.exists()) {
			isNoteAdded = true;
		}
		Recipe recipe = new Recipe();
		recipe.setRecipeName(recipeName);
		recipe.setSaved(saved);
		recipe.setFavorite(favorite);
		recipe.setNoteAdded(isNoteAdded);
		db.addRecipe(recipe);
		db.close();
	}

}
