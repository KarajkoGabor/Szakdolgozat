package hu.blogspot.limarapeksege.model;

public class Recipe {

	private String id;
	private String recipeURL;
	private String recipeName;
	private boolean isSaved;
	private boolean isFavorite;
	private boolean isNoteAdded;
	private int category_id;
	private int note_id;
	private String recipeThumbnailUrl;

	public Recipe() {

	}

	public Recipe(String recipeName, boolean isSaved) {
		this.recipeName = recipeName;
		this.isSaved = isSaved;
	}

	public String getRecipeURL() {
		return recipeURL;
	}

	public void setRecipeURL(String recipeURL) {
		this.recipeURL = recipeURL;
	}

	public String getRecipeName() {
		return recipeName;
	}

	public void setRecipeName(String recipeName) {
		this.recipeName = recipeName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isSaved() {
		return isSaved;
	}

	public void setSaved(boolean isSaved) {
		this.isSaved = isSaved;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

	public boolean isNoteAdded() {
		return isNoteAdded;
	}

	public void setNoteAdded(boolean isNoteAdded) {
		this.isNoteAdded = isNoteAdded;
	}

	public int getCategory_id() {
		return category_id;
	}

	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}

	public int getNote_id() {
		return note_id;
	}

	public void setNote_id(int note_id) {
		this.note_id = note_id;
	}

	public String getRecipeThumbnailUrl() {
		return recipeThumbnailUrl;
	}

	public void setRecipeThumbnailUrl(String recipeThumbnailUrl) {
		this.recipeThumbnailUrl = recipeThumbnailUrl;
	}

}
