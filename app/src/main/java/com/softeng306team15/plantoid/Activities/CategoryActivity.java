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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.softeng306team15.plantoid.Adaptors.ItemAdaptor;
import com.softeng306team15.plantoid.Models.IItem;
import com.softeng306team15.plantoid.Models.PlantCareDecorItem;
import com.softeng306team15.plantoid.Models.PlantTreeItem;
import com.softeng306team15.plantoid.Models.PotPlanterItem;
import com.softeng306team15.plantoid.Models.SeedSeedlingItem;
import com.softeng306team15.plantoid.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private class ViewHolder {
        LinearLayout discoverButton, wishlistButton, profileButton;
        ImageView backButton;
        SearchView searchBar;
        TextView categoryNameText;
        RecyclerView itemsRecyclerView;

        public ViewHolder() {
            discoverButton = findViewById(R.id.discover_navbar_button);
            wishlistButton = findViewById(R.id.wishlist_navbar_button);
            profileButton = findViewById(R.id.profile_navbar_button);
            backButton = findViewById(R.id.back_button);

            searchBar = findViewById(R.id.searchView);
            categoryNameText = findViewById(R.id.category_title_textView);

            itemsRecyclerView = findViewById(R.id.categoryRecyclerView);
        }
    }
    ViewHolder vh;
    String userId, category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        userId = intent.getStringExtra("User");
        category = intent.getStringExtra("Category");

        setContentView(R.layout.activity_category);

        vh = new CategoryActivity.ViewHolder();

        switch (category) {
            case "Plants":
                category = "Plants and Trees";
                vh.categoryNameText.setText("Plants and Trees");
                break;
            case "Seeds":
                category = "Seeds and Seedlings";
                vh.categoryNameText.setText("Seeds and Seedlings");
                break;
            case "Planters":
                category = "Pots and Planters";
                vh.categoryNameText.setText("Pots and Planters");
                break;
            default:  //must be Care
                category = "Plant Care and Decor";
                vh.categoryNameText.setText("Plant Care and Decor");
                break;
        }

        fetchCategoryItemData();

        vh.backButton.setOnClickListener(this::goDiscover);
        vh.discoverButton.setOnClickListener(this::goDiscover);
        vh.wishlistButton.setOnClickListener(this::goWishlist);
        vh.profileButton.setOnClickListener(this::goProfile);
    }

    /**
     * load all items in category from firestore
     */
    private void fetchCategoryItemData(){

        List<IItem> itemList = new LinkedList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
            itemAdapter = new ItemAdaptor(data, R.layout.item_rv_category_seeds);
        }else{
            itemAdapter = new ItemAdaptor(data, R.layout.item_rv_category);
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

    public void goProfile(View v) {
        Intent profileIntent = new Intent(getBaseContext(), ProfileActivity.class);
        profileIntent.putExtra("User", userId);
        startActivity(profileIntent);
    }

    public void goSearch(View v) {

    }

}
