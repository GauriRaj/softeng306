package com.softeng306team15.plantoid.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.softeng306team15.plantoid.Fragments.ImageSlidePageFragment;
import com.softeng306team15.plantoid.Models.DetailedItem;
import com.softeng306team15.plantoid.Models.IItem;
import com.softeng306team15.plantoid.Models.IUser;
import com.softeng306team15.plantoid.Models.MainItem;
import com.softeng306team15.plantoid.Models.User;
import com.softeng306team15.plantoid.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class DetailActivity extends FragmentActivity {

    private IItem item;
    private IUser user;
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;

    private void fetchUserData(){

    }

    private void fetchUserData(String userId){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Change the 1 to whichever user is being displayed
        DocumentReference userDoc = db.collection("users").document(userId);
        userDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot userDoc1 = task.getResult();
                if (userDoc1.exists()) {
                    user = userDoc1.toObject(User.class);
                    user.setId(userDoc1.getId());
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private void fetchItemData(String itemId){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("/items/"+itemId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot results = task.getResult();
                for(QueryDocumentSnapshot itemDoc: results){
                    item = itemDoc.toObject(DetailedItem.class);
                    item.setId(itemDoc.getId());
                }
                if (item != null) {
                    // Once the task is successful and data is fetched, get the tag and image data
                    fetchItemSubCollections(item);

                } else {
                    Toast.makeText(getBaseContext(), "Collection was empty!", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                Toast.makeText(getBaseContext(), "Loading items collection failed from Firestore!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchItemSubCollections(IItem item) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("/items/" + item.getId() + "/images").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot results = task.getResult();
                List<String> images = new ArrayList<>();
                for (QueryDocumentSnapshot imageDoc : results) {
                    images.add((String) imageDoc.get("image"));
                }
                item.setImages(images);
            }
        });
        db.collection("items/" + item.getId() + "/tags").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot results = task.getResult();
                List<String> tags = new ArrayList<>();
                for (QueryDocumentSnapshot imageDoc : results) {
                    String tag = (String) imageDoc.get("tagName");
                    tags.add(tag);
                    //TODO increment tag relevance to user
                }
                item.setTags(tags);
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                Toast.makeText(getBaseContext(), "Loading items tags failed from Firestore!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // Get the user id and item id from previous activity
        Bundle extras = getIntent().getExtras();
        String itemId, userId;
        if (extras != null){
            itemId = extras.getString("itemId");
            userId = extras.getString("userId");
            fetchItemData(itemId);
            fetchUserData(userId);
        }
        // ViewPager acts as parent to the fragment collection,
        // ImageSlidePagerAdapter handles each fragment (for displaying images)
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
