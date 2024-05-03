package com.example.gallery;

import java.util.ArrayList;

public interface NavigationSearch {
    public void openSearch(String keyword);
    public void openTags(ArrayList<String> img_path);
    public void closeSearch();
}
