package com.softeng306team15.plantoid.Activities;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.softeng306team15.plantoid.Adaptors.ItemAdaptor;
import com.softeng306team15.plantoid.ItemModels.IItem;
import com.softeng306team15.plantoid.UserModels.IUser;
import com.softeng306team15.plantoid.ItemModels.MainItem;
import com.softeng306team15.plantoid.UserModels.User;
import com.softeng306team15.plantoid.MyCallback;
import com.softeng306team15.plantoid.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private class ViewHolder {
        CardView seedsCardView, plantsCardView, plantersCardView, careCardView;
        LinearLayout discoverButton, wishlistButton, logoutButton;
        SearchView searchBar;
        TextView usernameText;

        RecyclerView forYouRecyclerView, bestSellerRecyclerView,newItemsRecyclerView;

        public ViewHolder() {

            seedsCardView = findViewById(R.id.seeds_category_card);
            plantsCardView = findViewById(R.id.plants_category_card);
            plantersCardView = findViewById(R.id.planters_category_card);
            careCardView = findViewById(R.id.care_category_card);

            discoverButton = findViewById(R.id.discover_navbar_button);
            wishlistButton = findViewById(R.id.wishlist_navbar_button);
            logoutButton = findViewById(R.id.profile_navbar_button);

            searchBar = findViewById(R.id.searchView);

            usernameText = findViewById(R.id.banner_welcome_text);

            forYouRecyclerView = findViewById(R.id.recyclerView_main_1);
            bestSellerRecyclerView = findViewById(R.id.recyclerView_main_2);
            newItemsRecyclerView = findViewById(R.id.recyclerView_main_3);

        }
    }

    ViewHolder vh;
    private FirebaseAuth mAuth;
    String userTopCategory, userTopPrice,userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        //get intent to get userID
        userId = getIntent().getStringExtra("User");
        setContentView(R.layout.activity_main);

        vh = new ViewHolder();
        setUserDisplay(userId);
        fetchItemData();

        vh.seedsCardView.setOnClickListener(this::goSeeds);

        vh.plantsCardView.setOnClickListener(this::goPlants);

        vh.plantersCardView.setOnClickListener(this::goPlanters);

        vh.careCardView.setOnClickListener(this::goCare);

        vh.wishlistButton.setOnClickListener(this::goWishlist);

        vh.logoutButton.setOnClickListener(this::goLogout);

        vh.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Start search activity and pass it the query
                Intent intent = new Intent(getBaseContext(), SearchActivity.class);
                intent.putExtra("User", userId);
                intent.putExtra("Query", query);
                intent.putExtra("Category", "All");
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO auto-fill functionality goes here
                return false;
            }
        });
    }

    public void setUserDisplay(String id) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        Log.d(TAG, "user " + firebaseUser);
        Log.d(TAG, "user " + id);
        Log.d(TAG, "user ");
        // set to actual user's name
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference userDoc = db.collection("users").document(id);
        userDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot userDoc1 = task.getResult();
                if (userDoc1.exists()) {
                    IUser user = userDoc1.toObject(User.class);

                    String message = "Welcome,\n" + user.getUserName();
                    user.setId(userDoc1.getId());
                    vh.usernameText.setText(message);
                    userTopCategory = user.getTopCategory();
                    userTopPrice = user.getTopPriceRange();
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

    }

    /**
     * load all items from firestore
     */
    private void fetchItemData(){

        List<IItem> itemList = new LinkedList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("items").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot results = task.getResult();
                for(QueryDocumentSnapshot itemDoc: results){
                    IItem item = itemDoc.toObject(MainItem.class);
                    item.setId(itemDoc.getId());
                    itemList.add(item);
                }
                if (itemList.size() > 0) {
                    // Once the task is successful and data is fetched, get the tag and image data
                    getItemSubCollections(itemList);

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

    /**
     * Load the image and tag subcollections for all of the items from firestore
     *
     * @param data items with type IItem
     */
    private void getItemSubCollections(List<IItem> data){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<IItem> bestSellerItems = new LinkedList<>();
        List<IItem> newItems = new LinkedList<>();
        List<IItem> forYouItems = new LinkedList<>();

        //Due to async loading it is not known what finishes loading first. Callback counter ensures
        //adaptor is only called when all of the items have fully loaded.
        MyCallback callback = new MyCallback() {
            int responses = 0;
            @Override
            public void onCallback() {
                responses ++;
                Log.d(TAG, "callback called responses number " + responses);
                if (responses == 2*data.size()){
                    Log.d(TAG, "callback called adaptor");
                    propagateAdaptor(forYouItems, vh.forYouRecyclerView);
                    propagateAdaptor(bestSellerItems, vh.bestSellerRecyclerView);
                    propagateAdaptor(newItems, vh.newItemsRecyclerView);
                }
            }
        };

        for(IItem item: data){
            //load all images for item
            db.collection("/items/"+item.getId()+"/images").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot results = task.getResult();
                    List<String> images = new ArrayList<>();
                    for(QueryDocumentSnapshot imageDoc: results){
                        images.add((String) imageDoc.get("image"));
                    }
                    item.setImages(images);
                    callback.onCallback();
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    Toast.makeText(getBaseContext(), "Loading items images failed from Firestore!", Toast.LENGTH_LONG).show();
                }
            });
            //load all tags for item
            db.collection("items/"+item.getId()+"/tags").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot results = task.getResult();
                    List<String> tags = new ArrayList<>();
                    for(QueryDocumentSnapshot imageDoc: results){
                        tags.add((String) imageDoc.get("tagName"));
                    }
                    item.setTags(tags);
                    callback.onCallback();

                    //separate items for the recycler views
                    if(item.isBestSeller()){
                        bestSellerItems.add(item);
                    }
                    if(item.isNewItem()){
                        newItems.add(item);
                    }
                    if(item.getTags().contains(userTopPrice)){
                        Log.d(TAG, "item " + item.getItemName());
                        if (item.getCategory().equals(userTopCategory)){
                            forYouItems.add(item);
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    Toast.makeText(getBaseContext(), "Loading items tags failed from Firestore!", Toast.LENGTH_LONG).show();
                }
            });
        }

    }
    private void propagateAdaptor(List<IItem> data, RecyclerView recyclerView) {
        ItemAdaptor itemAdapter = new ItemAdaptor(data, R.layout.item_rv_main, userId);
        recyclerView.setAdapter(itemAdapter);
        LinearLayoutManager lm = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(lm);
    }

    // Categories
    public void goSeeds(View v) {
        Intent categoryIntent = new Intent(getBaseContext(), CategoryActivity.class);
        categoryIntent.putExtra("Category", "Seeds");
        categoryIntent.putExtra("User", userId);
        startActivity(categoryIntent);
    }

    public void goPlants(View v) {
        Intent categoryIntent = new Intent(getBaseContext(), CategoryActivity.class);
        categoryIntent.putExtra("Category", "Plants");
        categoryIntent.putExtra("User", userId);
        startActivity(categoryIntent);
    }

    public void goPlanters(View v) {
        Intent categoryIntent = new Intent(getBaseContext(), CategoryActivity.class);
        categoryIntent.putExtra("Category", "Planters");
        categoryIntent.putExtra("User", userId);
        startActivity(categoryIntent);
    }

    public void goCare(View v) {
        Intent categoryIntent = new Intent(getBaseContext(), CategoryActivity.class);
        categoryIntent.putExtra("Category", "Care");
        categoryIntent.putExtra("User", userId);
        startActivity(categoryIntent);
    }

    // Navbar
    public void goWishlist(View v) {
        Intent wishlistIntent = new Intent(getBaseContext(), WishlistActivity.class);
        wishlistIntent.putExtra("User", userId);
        startActivity(wishlistIntent);
    }

    public void goLogout(View v) {
        FirebaseAuth.getInstance().signOut();
        Intent logoutIntent = new Intent(getBaseContext(), LogInActivity.class);
        startActivity(logoutIntent);
    }

}