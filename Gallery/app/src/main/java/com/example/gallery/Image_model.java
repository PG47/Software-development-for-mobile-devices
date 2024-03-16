package com.example.gallery;

import java.util.Date;

public class Image_model {
    private String imagePath;
    private Date captureDate;

    public Image_model(String imagePath, Date captureDate) {
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
