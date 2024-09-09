package model;

public class PageSearchResult {
    private String formattedContent;


    public PageSearchResult(String content){
        assert content != null;
        assert !content.isEmpty();
        formattedContent = content;
    }

    public String getFormattedContent() {
        return formattedContent;
    }
}
