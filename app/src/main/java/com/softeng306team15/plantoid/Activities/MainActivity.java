package com.softeng306team15.plantoid.Activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.softeng306team15.plantoid.Adaptors.ItemAdaptor;
import com.softeng306team15.plantoid.Models.IItem;
import com.softeng306team15.plantoid.Models.IUser;
import com.softeng306team15.plantoid.Models.MainItem;
import com.softeng306team15.plantoid.Models.User;
import com.softeng306team15.plantoid.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private class ViewHolder {
        CardView seedsCardView, plantsCardView, plantersCardView, careCardView;
        LinearLayout discoverButton, wishlistButton, profileButton;
        SearchView searchBar;
        TextView usernameText;

        RecyclerView recyclerView_main_1, recyclerView_main_2,recyclerView_main_3;

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
            recyclerView_main_1 = findViewById(R.id.recyclerView_main_1);
            recyclerView_main_2 = findViewById(R.id.recyclerView_main_2);
            recyclerView_main_3 = findViewById(R.id.recyclerView_main_3);

        }
    }

    ViewHolder vh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get intent to get userID
        String userId = "1";
        setContentView(R.layout.activity_main);

        vh = new ViewHolder();
        setUserDisplay(userId);
        fetchItemData();

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
                    DocumentSnapshot userDoc = task.getResult();
                    if (userDoc.exists()) {
                        IUser user = userDoc.toObject(User.class);
                        String message = "Welcome,\n" + user.getUserName();
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

    private void fetchItemData(){

        List<IItem> itemList = new LinkedList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot results = task.getResult();
                    for(QueryDocumentSnapshot itemDoc: results){
                        IItem item = itemDoc.toObject(MainItem.class);
                        item.setId(itemDoc.getId());
                        itemList.add(item);
                    }
                    if (itemList.size() > 0) {
                        // Once the task is successful and data is fetched, propagate the adaptor
                        getItemSubCollections(itemList);

                    } else {
                        Toast.makeText(getBaseContext(), "Collection was empty!", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    Toast.makeText(getBaseContext(), "Loading items collection failed from Firestore!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void getItemSubCollections(List<IItem> data){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<IItem> bestSellerItems = new LinkedList<>();
        List<IItem> newItems = new LinkedList<>();

        for(IItem item: data){
            db.collection("/items/"+item.getId()+"/images").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot results = task.getResult();
                        List<String> images = new ArrayList<>();
                        for(QueryDocumentSnapshot imageDoc: results){
                            images.add((String) imageDoc.get("image"));
                        }
                        item.setImages(images);
                        if(item == data.get(data.size()-1)){
                            propagateAdaptor(bestSellerItems, vh.recyclerView_main_1);
                            propagateAdaptor(newItems, vh.recyclerView_main_2);
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        Toast.makeText(getBaseContext(), "Loading items images failed from Firestore!", Toast.LENGTH_LONG).show();
                    }
                }
            });
            db.collection("items/"+item.getId()+"/tags").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot results = task.getResult();
                        List<String> tags = new ArrayList<>();
                        for(QueryDocumentSnapshot imageDoc: results){
                            tags.add((String) imageDoc.get("tagName"));
                        }
                        item.setTags(tags);
                        if(item.isBestSeller()){
                            bestSellerItems.add(item);
                        }
                        if(item.isNewItem()){
                            newItems.add(item);
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        Toast.makeText(getBaseContext(), "Loading items tags failed from Firestore!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

//        propagateAdaptor(data);
    }
    private void propagateAdaptor(List<IItem> data, RecyclerView recyclerView) {
        ItemAdaptor itemAdapter = new ItemAdaptor(data, R.layout.item_rv_main);
        recyclerView.setAdapter(itemAdapter);
        LinearLayoutManager lm = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(lm);
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