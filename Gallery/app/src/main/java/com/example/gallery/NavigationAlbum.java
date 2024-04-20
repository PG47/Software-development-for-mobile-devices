package com.example.gallery;

import java.util.ArrayList;

public interface NavigationAlbum {
    public void openAlbum(ArrayList<String> images, boolean secure, String album_name);
    public void closeAlbum();
}
