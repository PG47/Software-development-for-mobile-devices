package com.example.gallery.Images_screen;

import java.util.Date;

public class ImageModel {
    private String imagePath;
    private Date captureDate;

    public ImageModel(String imagePath, Date captureDate) {
        this.imagePath = imagePath;
        this.captureDate = captureDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Date getCaptureDate() {
        return captureDate;
    }
}
