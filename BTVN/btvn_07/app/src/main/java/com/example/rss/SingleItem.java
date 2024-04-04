package com.example.rss;

public class SingleItem {
    private String title;
    private String description;
    private String link;
    public String getTitle() { return title;}
    public String getDescription() { return description; }
    public String getLink() { return link; }
    public SingleItem(String _title, String _description, String _link) {
        description = _description;
        title = _title;
        link = _link;
    }
    @Override
    public String toString() { return title; }
}
