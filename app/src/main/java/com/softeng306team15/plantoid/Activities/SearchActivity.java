package com.softeng306team15.plantoid.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.softeng306team15.plantoid.Adaptors.ItemAdaptor;
import com.softeng306team15.plantoid.ItemModels.IItem;
import com.softeng306team15.plantoid.ItemModels.MainItem;
import com.softeng306team15.plantoid.ItemModels.PlantCareDecorItem;
import com.softeng306team15.plantoid.ItemModels.PlantTreeItem;
import com.softeng306team15.plantoid.ItemModels.PotPlanterItem;
import com.softeng306team15.plantoid.ItemModels.SeedSeedlingItem;
import com.softeng306team15.plantoid.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private class ViewHolder {
        LinearLayout discoverButton, wishlistButton, logoutButton;
        ImageView backButton;
        SearchView searchBar;
        TextView categoryNameText;
        RecyclerView itemsRecyclerView;

        public ViewHolder() {
            discoverButton = findViewById(R.id.discover_navbar_button);
            wishlistButton = findViewById(R.id.wishlist_navbar_button);
            logoutButton = findViewById(R.id.profile_navbar_button);
            backButton = findViewById(R.id.back_button);

            searchBar = findViewById(R.id.searchView);
            categoryNameText = findViewById(R.id.category_title_textView);

            itemsRecyclerView = findViewById(R.id.categoryRecyclerView);
        }
    }
    ViewHolder vh;
    String userId, category, query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        userId = intent.getStringExtra("User");
        query = intent.getStringExtra("Query");
        category = intent.getStringExtra("Category");

        setContentView(R.layout.activity_category);

        vh = new SearchActivity.ViewHolder();

        fetchQueryItemData(category);

        vh.backButton.setOnClickListener(v -> {
            finish();
        });
        vh.discoverButton.setOnClickListener(this::goDiscover);
        vh.wishlistButton.setOnClickListener(this::goWishlist);
        vh.logoutButton.setOnClickListener(this::goLogout);
        String categoryName = "Search " + category;
        vh.categoryNameText.setText(categoryName);

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

    /**
     * load all item ids from search query
     */
    private void fetchQueryItemData(String category){

        List<IItem> itemList = new LinkedList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (category.equals("All")){
            db.collection("items")
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot results = task.getResult();
                            for(QueryDocumentSnapshot itemDoc: results){
                                IItem item = itemDoc.toObject(MainItem.class);
                                if (item.getItemName().toLowerCase().contains(query.toLowerCase())){
                                    item.setId(itemDoc.getId());
                                    itemList.add(item);
                                }
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
        } else{
            db.collection("items").whereEqualTo("category", category)
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot results = task.getResult();
                            for(QueryDocumentSnapshot itemDoc: results){
                                IItem item;
                                switch (category) {
                                    case "Plants and Trees":
                                        item = itemDoc.toObject(PlantTreeItem.class);
                                        break;
                                    case "Seeds and Seedlings":
                                        item = itemDoc.toObject(SeedSeedlingItem.class);
                                        break;
                                    case "Pots and Planters":
                                        item = itemDoc.toObject(PotPlanterItem.class);
                                        break;
                                    default:  //must be Plant Care and Decor
                                        item = itemDoc.toObject(PlantCareDecorItem.class);
                                        break;
                                }
                                if (item.getItemName().toLowerCase().contains(query.toLowerCase())){
                                    item.setId(itemDoc.getId());
                                    itemList.add(item);
                                }
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
    }

    /**
     * Load the image and tag subcollections for all of the category items from firestore
     *
     * @param data items with type IItem
     */
    private void getItemSubCollections(List<IItem> data){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<IItem> categoryItems = new LinkedList<>();

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
                    if(item == data.get(data.size()-1)){
                        //propagate to adaptors to fill the recycler view once all images are loaded
                        propagateAdaptor(categoryItems);
                    }
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
                    categoryItems.add(item);
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    Toast.makeText(getBaseContext(), "Loading items tags failed from Firestore!", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    private void propagateAdaptor(List<IItem> data) {
        ItemAdaptor itemAdapter;
        if(category.equals("Seeds and Seedlings")){
            itemAdapter = new ItemAdaptor(data, R.layout.item_rv_category_seeds, userId);
        }else if (category.equals("All")) {
            itemAdapter = new ItemAdaptor(data, R.layout.item_rv_main, userId);
        } else{
            itemAdapter = new ItemAdaptor(data, R.layout.item_rv_category, userId);
        }

        vh.itemsRecyclerView.setAdapter(itemAdapter);
        GridLayoutManager lm =new GridLayoutManager(this,calculateNumberOfColumns());
        vh.itemsRecyclerView.setLayoutManager(lm);
    }

    private int calculateNumberOfColumns(){
        DisplayMetrics displayMetrics = getBaseContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int columnNo = (int) dpWidth/170; //170 is item card width
        if (columnNo < 1){ //show at least one column
            columnNo = 1;
        }
        return columnNo;
    }

    public void goDiscover(View v) {
        Intent mainActivityIntent = new Intent(getBaseContext(), MainActivity.class);
        mainActivityIntent.putExtra("User", userId);
        startActivity(mainActivityIntent);
    }
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
