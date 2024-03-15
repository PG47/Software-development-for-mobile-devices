package com.example.gallery;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class DetailsActivity extends AppCompatActivity {
    FragmentTransaction ft;
    LargeImageFragment fragmentImage;
    OptionFragment fragmentOption;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        fragmentImage = LargeImageFragment.newInstance("image");
        fragmentOption = OptionFragment.newInstance("option");

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.detailsImage, fragmentImage);
        ft.addToBackStack(null); // Add transaction to the back stack
        ft.commit();

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.toDoWith, fragmentOption);
        ft.addToBackStack(null); // Add transaction to the back stack
        ft.commit();
    }
}
