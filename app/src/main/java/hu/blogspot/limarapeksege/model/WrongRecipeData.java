package hu.blogspot.limarapeksege.model;

public class WrongRecipeData {

    String apiName;

    String htmlName;

    String id;

    public WrongRecipeData(){

    }


    public WrongRecipeData(String apiName, String htmlName, String id) {
        this.apiName = apiName;
        this.htmlName = htmlName;
        this.id = id;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getHtmlName() {
        return htmlName;
    }

    public void setHtmlName(String htmlName) {
        this.htmlName = htmlName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {

        boolean returnValue = false;

        if( o instanceof WrongRecipeData){
            WrongRecipeData wrongRecipeData = (WrongRecipeData) o;
            returnValue = wrongRecipeData.getHtmlName().equals(this.getHtmlName());
        }

        return returnValue;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
