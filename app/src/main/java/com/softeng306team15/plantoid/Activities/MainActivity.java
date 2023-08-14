package com.softeng306team15.plantoid.Activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softeng306team15.plantoid.R;

public class MainActivity extends AppCompatActivity {

    private class ViewHolder {
        CardView seedsCardView, plantsCardView, plantersCardView, careCardView;
        LinearLayout discoverButton, wishlistButton, profileButton;
        SearchView searchBar;
        TextView usernameText;

        public ViewHolder() {

            seedsCardView = findViewById(R.id.seeds_category_card);
            plantsCardView = findViewById(R.id.plants_category_card);
            plantersCardView = findViewById(R.id.planters_category_card);
            careCardView = findViewById(R.id.care_category_card);

            discoverButton = findViewById(R.id.discover_navbar_button);
            wishlistButton = findViewById(R.id.wishlist_navbar_button);
            profileButton = findViewById(R.id.profile_navbar_button);

            searchBar = findViewById(R.id.searchView);

            usernameText = findViewById(R.id.banner_welcome_text);

        }
    }

    ViewHolder vh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vh = new ViewHolder();
        setUserDisplay("1");

        vh.seedsCardView.setOnClickListener(this::goSeeds);

        vh.plantsCardView.setOnClickListener(this::goPlants);

        vh.plantersCardView.setOnClickListener(this::goPlanters);

        vh.careCardView.setOnClickListener(this::goCare);

        vh.wishlistButton.setOnClickListener(this::goWishlist);

        vh.profileButton.setOnClickListener(this::goProfile);
        
    }

    public void setUserDisplay(String id) {
        // set to actual user's name
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Change the 1 to whichever user is being displayed
        DocumentReference userDoc = db.collection("users").document(id);
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot userData = task.getResult();
                    if (userData.exists()) {
                        String message = "Welcome,\n" + userData.get("userName");
                        vh.usernameText.setText(message);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    // Categories
    public void goSeeds(View v) {
        Intent categoryIntent = new Intent(getBaseContext(), CategoryActivity.class);
        categoryIntent.putExtra("Category", "Seeds");
        startActivity(categoryIntent);
    }

    public void goPlants(View v) {
        Intent categoryIntent = new Intent(getBaseContext(), CategoryActivity.class);
        categoryIntent.putExtra("Category", "Plants");
        startActivity(categoryIntent);
    }

    public void goPlanters(View v) {
        Intent categoryIntent = new Intent(getBaseContext(), CategoryActivity.class);
        categoryIntent.putExtra("Category", "Planters");
        startActivity(categoryIntent);
    }

    public void goCare(View v) {
        Intent categoryIntent = new Intent(getBaseContext(), CategoryActivity.class);
        categoryIntent.putExtra("Category", "Care");
        startActivity(categoryIntent);
    }

    // Navbar
    public void goWishlist(View v) {
        Intent wishlistIntent = new Intent(getBaseContext(), WishlistActivity.class);
        startActivity(wishlistIntent);
    }

    public void goProfile(View v) {
        Intent profileIntent = new Intent(getBaseContext(), ProfileActivity.class);
        startActivity(profileIntent);
    }

    public void goSearch(View v) {

    }


}