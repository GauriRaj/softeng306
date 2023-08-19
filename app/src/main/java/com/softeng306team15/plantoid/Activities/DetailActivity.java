package com.softeng306team15.plantoid.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.softeng306team15.plantoid.Fragments.ImageSlidePageFragment;
import com.softeng306team15.plantoid.Models.DetailedItem;
import com.softeng306team15.plantoid.Models.MainItem;
import com.softeng306team15.plantoid.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class DetailActivity extends FragmentActivity {

    private DetailedItem item;
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Get the detailed item model class
        /*
        Bundle extras = getIntent().getExtras();
        String item;
        if (extras != null){
            item = extras.getString("item");
        }
        */

        // ViewPager acts as parent to the fragment collection,
        // ImageSlidePagerAdapter handles each fragment
        viewPager = findViewById(R.id.image_pager);
        pagerAdapter = new ImageSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
    }

    private class ImageSlidePagerAdapter extends FragmentStateAdapter {
        public ImageSlidePagerAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @Override
        public Fragment createFragment(int position) {
            ImageSlidePageFragment imageSlide = new ImageSlidePageFragment();
            // Load image from the item model class list of images,
            // corresponding to the position of this fragment in the collection
            ImageView iv = imageSlide.requireView().findViewById(R.id.detail_imageView);
            Picasso.get().load(item.getImages().get(position)).into(iv);
            return imageSlide;
        }

        @Override
        public int getItemCount() {
            return item.getImages().size();
        }
    }

}
