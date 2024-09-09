package model;

public class Page {
    private String title;

    private String content;

    private boolean isPrivate;

    public Page(String title, String content, boolean isPrivate){
        assert title != null;
        assert content != null;
        assert !title.isEmpty();
        assert !content.isEmpty();
        this.title = title;
        this.content = content;
        this.isPrivate = isPrivate;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
}
