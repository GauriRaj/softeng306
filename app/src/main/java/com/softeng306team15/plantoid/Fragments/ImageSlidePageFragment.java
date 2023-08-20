package com.softeng306team15.plantoid.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.softeng306team15.plantoid.R;
import com.squareup.picasso.Picasso;

public class ImageSlidePageFragment extends Fragment {

    String image;
    public ImageSlidePageFragment(String image){
        this.image = image;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // This is the base ViewGroup of the layout for the fragment
        ViewGroup vg = (ViewGroup) inflater.inflate(
                R.layout.image_slide_page, container, false);

        // Load the image from Firestore by its URL
        ImageView iv = vg.findViewById(R.id.detail_imageView);
        Picasso.get().load(image).into(iv);

        return vg;
    }



}
