package hu.blogspot.limarapeksege.model;

public class Category extends Object {

	private int id;
	private String name;
	private String label;
	private boolean isRecipesDownloaded;

	public Category(int id, String name, boolean isRecipesDownloaded) {
		this.id = id;
		this.name = name;
		this.isRecipesDownloaded = isRecipesDownloaded;
	}

	public Category() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isRecipesDownloaded() {
		return isRecipesDownloaded;
	}

	public void setRecipesDownloaded(boolean isRecipesDownloaded) {
		this.isRecipesDownloaded = isRecipesDownloaded;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
