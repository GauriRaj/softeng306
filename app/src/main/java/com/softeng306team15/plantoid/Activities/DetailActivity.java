package com.softeng306team15.plantoid.Activities;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.softeng306team15.plantoid.Adaptors.ItemAdaptor;
import com.softeng306team15.plantoid.Fragments.ImageSlidePageFragment;
import com.softeng306team15.plantoid.Models.DetailedItem;
import com.softeng306team15.plantoid.Models.IItem;
import com.softeng306team15.plantoid.Models.IUser;
import com.softeng306team15.plantoid.Models.MainItem;
import com.softeng306team15.plantoid.Models.PlantCareDecorItem;
import com.softeng306team15.plantoid.Models.PlantTreeItem;
import com.softeng306team15.plantoid.Models.PotPlanterItem;
import com.softeng306team15.plantoid.Models.SeedSeedlingItem;
import com.softeng306team15.plantoid.Models.User;
import com.softeng306team15.plantoid.MyCallback;
import com.softeng306team15.plantoid.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class DetailActivity extends FragmentActivity {

    private IItem item;
    private IUser user;
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;

    private class ViewHolder {
        TextView itemTitleTextView;
        TextView itemPriceTextView;
        TextView itemDescTextView;
        TextView itemScientificTextView;
        CompoundButton wishlistButton;

        LinearLayout tagsLayout;
        LinearLayout tag1Layout;
        LinearLayout tag2Layout;
        LinearLayout tag3Layout;
        ImageView tag1ImageView;
        ImageView tag2ImageView;
        ImageView tag3ImageView;
        TextView tag1TextView;
        TextView tag2TextView;
        TextView tag3TextView;

        public ViewHolder(){
            itemTitleTextView = findViewById(R.id.detail_title_textView);
            itemPriceTextView = findViewById(R.id.detail_price_textView);
            itemDescTextView = findViewById(R.id.detail_description_textView);
            itemScientificTextView = findViewById(R.id.detail_scientific_textView);
            wishlistButton = findViewById(R.id.wishlist_button);
            tagsLayout = findViewById(R.id.detail_tags_layout);
            tag1Layout = findViewById(R.id.tag1Layout);
            tag2Layout = findViewById(R.id.tag2Layout);
            tag3Layout = findViewById(R.id.tag3Layout);
            tag1ImageView = findViewById(R.id.tag1ImageView);
            tag2ImageView = findViewById(R.id.tag2ImageView);
            tag3ImageView = findViewById(R.id.tag3ImageView);
            tag1TextView = findViewById(R.id.tag1TextView);
            tag2TextView = findViewById(R.id.tag2TextView);
            tag3TextView = findViewById(R.id.tag3TextView);
        }
    }

    private void fetchUserData(String userId, MyCallback callback){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Change the 1 to whichever user is being displayed
        DocumentReference userDoc = db.collection("users").document(userId);
        userDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot userDoc1 = task.getResult();
                if (userDoc1.exists()) {
                    user = userDoc1.toObject(User.class);
                    user.setId(userDoc1.getId());
                    Log.d(TAG, "got me a user " + user.getUserName());
                    callback.onCallback();
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private void fetchItemData(String itemId, MyCallback callback){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.document("/items/"+itemId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot itemDoc = task.getResult();
                if(itemDoc != null){
                    item = itemDoc.toObject(DetailedItem.class);
                    if(item != null){
                        item.setId(itemDoc.getId());
                    }
                }
                if (item != null) {
                    // Once the task is successful and data is fetched, get the tag and image data
                    callback.onCallback();

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

    private void fetchItemTags(IItem item, MyCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                callback.onCallback();
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                Toast.makeText(getBaseContext(), "Loading items tags failed from Firestore!", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void fetchItemImages(IItem item, MyCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("/items/" + item.getId() + "/images").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot results = task.getResult();
                List<String> images = new ArrayList<>();
                for (QueryDocumentSnapshot imageDoc : results) {
                    images.add((String) imageDoc.get("image"));
                }
                //TODO remove
                for (String s: images) {
                    Log.d(TAG, "image " + s + "loaded");
                }
                item.setImages(images);
                callback.onCallback();
            }
        });
    }

    ViewHolder vh;
    String navigateFrom;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // Back button functionality
        vh = new ViewHolder();
        // Get the user id and item id from previous activity
        String itemId, userId;
        Intent intent = getIntent();
        itemId = intent.getStringExtra("itemId");
        userId = intent.getStringExtra("userId");
        Log.d(TAG, "Detail view item id: " + itemId);
        Log.d(TAG, "Detail view user id: " + userId);

        if(intent.hasExtra("from")){
            navigateFrom = intent.getStringExtra("from");
        }

        pagerAdapter = new ImageSlidePagerAdapter(this);
        viewPager = findViewById(R.id.image_pager);

        fetchUserData(userId, () ->{
            Log.d(TAG, "fetched user " + user.getUserName());
            user.loadWishlist(() -> {
                Log.d(TAG, "fetched wishlist");
                fetchItemData(itemId, () -> {
                    Log.d(TAG, "fetched item " + item.getItemName());
                    float price = item.getItemPrice();
                    vh.itemTitleTextView.setText(item.getItemName());
                    vh.itemPriceTextView.setText("$" + price);
                    vh.itemDescTextView.setText(item.getItemDesc());

                    user.incrementCategoryHit(item.getCategory());
                    if (price < 5) {
                        user.incrementPriceRangeHit("0 - 4.99");
                    } else if (price < 15) {
                        user.incrementPriceRangeHit("5 - 14.99");
                    } else if (price < 25) {
                        user.incrementPriceRangeHit("15 - 24.99");
                    } else if (price < 50) {
                        user.incrementPriceRangeHit("25 - 49.99");
                    } else {
                        user.incrementPriceRangeHit("50+");
                    }

                    // Set wishlist button initial state
                    List<String> currentUserWishlist = user.getWishlist();
                    boolean isInWishlist = false;
                    for (String wishlistItemId : currentUserWishlist){
                        if (wishlistItemId.equals(itemId)) {
                            isInWishlist = true;
                            break;
                        }
                    }
                    if (isInWishlist){
                        vh.wishlistButton.setChecked(true);
                        vh.wishlistButton.setOnClickListener(v -> {
                            removeFromWishlist(itemId);
                        });
                    } else {
                        vh.wishlistButton.setChecked(false);
                        vh.wishlistButton.setOnClickListener(v2 -> {
                            addToWishlist(itemId);
                        });
                    }

                    // Set up wishlist button animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
                    scaleAnimation.setDuration(500);
                    BounceInterpolator bounceInterpolator = new BounceInterpolator();
                    scaleAnimation.setInterpolator(bounceInterpolator);
                    vh.wishlistButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                            //animation
                            compoundButton.startAnimation(scaleAnimation);
                        }
                    });

                    // Fetch and set scientific name if product is a botanical
                    if(item.getCategory().equals("Plants and Trees") || item.getCategory().equals("Seeds and Seedlings")){
                        String scientific = item.getScientific();
                        if(!scientific.equals("")){
                            vh.itemScientificTextView.setText(scientific);
                            vh.itemScientificTextView.setVisibility(View.VISIBLE);
                        } else{
                            vh.itemScientificTextView.setVisibility(View.GONE);
                        }
                    } else {
                        vh.itemScientificTextView.setVisibility(View.GONE);
                    }

                    // Use loaded item tag data to populate tag icons
                    fetchItemTags(item, this::updateTagIcons);

                    fetchItemImages(item, () -> {
                        // ViewPager acts as parent to the fragment collection,
                        // ImageSlidePagerAdapter handles each fragment (for displaying images)
                        viewPager.setAdapter(pagerAdapter);
                    });

                });});});
    }

    private void addToWishlist(String itemId){
        user.addToWishlist(itemId);
        CharSequence text = "Added to Wishlist";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this /* MyActivity */, text, duration);
        toast.show();
        vh.wishlistButton.setOnClickListener(v -> {
            removeFromWishlist(itemId);
        });
    }

    private void removeFromWishlist(String itemId){
        user.removeFromWishlist(itemId);
        CharSequence text = "Removed from Wishlist";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this /* MyActivity */, text, duration);
        toast.show();
        vh.wishlistButton.setOnClickListener(v -> {
            addToWishlist(itemId);
        });
    }

    private class ImageSlidePagerAdapter extends FragmentStateAdapter {
        public ImageSlidePagerAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {

            String image = item.getImages().get(position);
            Log.d(TAG, "Image string " + position + ": " + item.getImages().get(position));

            // Load image from the item model class list of images,
            // corresponding to the position of this fragment in the collection
            return new ImageSlidePageFragment(image);
        }

        @Override
        public int getItemCount() {
            return item.getImages().size();
        }
    }

    private void updateTagIcons(){
        String category = item.getCategory();
        List<String> tags = item.getTags();

        if(tags == null){
            Log.d(TAG, "tags was null!");
            vh.tagsLayout.setVisibility(View.GONE);
            return;
        }

        if(tags.size() == 0){
            Log.d(TAG, "Could not find any tags on item.");
            vh.tagsLayout.setVisibility(View.GONE);
            return;
        }

        if(category.equals("Plants and Trees") || category.equals("Seeds and Seedlings")){
            boolean tagged = false;
            vh.tagsLayout.setVisibility(View.VISIBLE);
            for (String subCategory : tags){
                switch (subCategory){
                    case "Evergreen":
                        vh.tag2ImageView.setImageResource(R.drawable.icon_evergreen);
                        vh.tag2TextView.setText(R.string.tag_evergreen);
                        tagged = true;
                        break;
                    case "Deciduous":
                        vh.tag2ImageView.setImageResource(R.drawable.icon_deciduous);
                        vh.tag2TextView.setText(R.string.tag_deciduous);
                        tagged = true;
                        break;
                    case "Flowering":
                        vh.tag2ImageView.setImageResource(R.drawable.icon_flowering);
                        vh.tag2TextView.setText(R.string.tag_flowering);
                        tagged = true;
                        break;
                    case "Fruit":
                        vh.tag2ImageView.setImageResource(R.drawable.icon_fruit);
                        vh.tag2TextView.setText(R.string.tag_fruit);
                        tagged = true;
                        break;
                    case "Vegetable":
                        vh.tag2ImageView.setImageResource(R.drawable.icon_vegetable);
                        vh.tag2TextView.setText(R.string.tag_vegetable);
                        tagged = true;
                        break;
                    case "Herb":
                        vh.tag2ImageView.setImageResource(R.drawable.icon_herb);
                        vh.tag2TextView.setText(R.string.tag_herb);
                        tagged = true;
                        break;
                    case "Succulent":
                        vh.tag2ImageView.setImageResource(R.drawable.icon_succulent);
                        vh.tag2TextView.setText(R.string.tag_succulent);
                        tagged = true;
                        break;
                }
            }
            if(!tagged){
                vh.tag2Layout.setVisibility(View.GONE);
            }
            if(category.equals("Plants and Trees")) {
                vh.tag1Layout.setVisibility(View.GONE);
            } else{
                tagged = false;
                for (String seedSubCategory : tags){
                    if(seedSubCategory.equals("Seed")){
                        vh.tag1ImageView.setImageResource(R.drawable.icon_seed);
                        vh.tag1TextView.setText(R.string.tag_seed);
                        tagged = true;
                    } else if (seedSubCategory.equals("Seedling")) {
                        vh.tag1ImageView.setImageResource(R.drawable.icon_seedling);
                        vh.tag1TextView.setText(R.string.tag_seedling);
                        tagged = true;
                    }
                }
                if(!tagged){
                    vh.tag1Layout.setVisibility(View.GONE);
                }
            }

        }else if(category.equals("Pots and Planters")){
            boolean tagged = false;
            vh.tagsLayout.setVisibility(View.VISIBLE);
            for(String size : tags){
                switch (size){
                    case "S":
                        vh.tag2ImageView.setImageResource(R.drawable.icon_small_pot);
                        vh.tag2TextView.setText(R.string.tag_smallPot);
                        tagged = true;
                        break;
                    case "M":
                        vh.tag2ImageView.setImageResource(R.drawable.icon_medium_pot);
                        vh.tag2TextView.setText(R.string.tag_mediumPot);
                        tagged = true;
                        break;
                    case "L":
                        vh.tag2ImageView.setImageResource(R.drawable.icon_large_pot);
                        vh.tag2TextView.setText(R.string.tag_largePot);
                        tagged = true;
                        break;
                }
            }
            if(!tagged){
                vh.tag2Layout.setVisibility(View.GONE);
            }
            vh.tag1Layout.setVisibility(View.GONE);

        }else{
            // Category does not have sub-categories
            vh.tag1Layout.setVisibility(View.GONE);
            vh.tag2Layout.setVisibility(View.GONE);
        }


        boolean tagged = false;
        // Global tags
        for(String globalTag : tags) {
            if (globalTag.equals("BestSeller")) {
                vh.tagsLayout.setVisibility(View.VISIBLE);
                vh.tag3ImageView.setImageResource(R.drawable.icon_best_seller);
                vh.tag3TextView.setText(R.string.tag_bestSeller);
                tagged = true;
            }
        }
        for(String globalTag : tags) {
            if (globalTag.equals("NewItem") && !tagged) {
                vh.tagsLayout.setVisibility(View.VISIBLE);
                vh.tag3ImageView.setImageResource(R.drawable.icon_best_seller);
                vh.tag3TextView.setText(R.string.tag_bestSeller);
                tagged = true;
            }
        }
        if(!tagged){
            vh.tag3Layout.setVisibility(View.GONE);
            if(vh.tag2Layout.getVisibility() == View.GONE && vh.tag1Layout.getVisibility() == View.GONE) {
                vh.tagsLayout.setVisibility(View.GONE);
            }
        }
    }
}
