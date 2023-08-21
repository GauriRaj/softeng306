package com.softeng306team15.plantoid.Activities;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
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
import com.softeng306team15.plantoid.Fragments.ImageSlidePageFragment;
import com.softeng306team15.plantoid.Models.DetailedItem;
import com.softeng306team15.plantoid.Models.IItem;
import com.softeng306team15.plantoid.Models.IUser;
import com.softeng306team15.plantoid.Models.MainItem;
import com.softeng306team15.plantoid.Models.User;
import com.softeng306team15.plantoid.MyCallback;
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

    private class ViewHolder {
        TextView topTitleTextView;
        TextView itemTitleTextView;
        TextView itemPriceTextView;
        TextView itemDescTextView;
        ImageView backArrowImageView;
        Button wishlistButton;
        public ViewHolder(){
            topTitleTextView = findViewById(R.id.detail_top_title);
            itemTitleTextView = findViewById(R.id.detail_title_textView);
            itemPriceTextView = findViewById(R.id.detail_price_textView);
            itemDescTextView = findViewById(R.id.detail_description_textView);
            backArrowImageView = findViewById(R.id.detail_back_imageView);
            wishlistButton = findViewById(R.id.wishlist_button);
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
                    item.setId(itemDoc.getId());
                }
                if (item != null) {
                    // Once the task is successful and data is fetched, get the tag and image data
                    fetchItemSubCollections(item, callback);

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

    private void fetchItemSubCollections(IItem item, MyCallback callback) {
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

        vh.backArrowImageView.setOnClickListener(v -> {
            //need to reload main and wishlist incase wishlist or top category/price has changed
            if(navigateFrom.equals("Main")){
                Intent mainActivityIntent = new Intent(getBaseContext(), MainActivity.class);
                mainActivityIntent.putExtra("User", userId);
                startActivity(mainActivityIntent);
            }else if(navigateFrom.equals("Wishlist")){
                Intent wishlistIntent = new Intent(getBaseContext(), WishlistActivity.class);
                wishlistIntent.putExtra("User", userId);
                startActivity(wishlistIntent);
            }else {
                finish();
            }
        });

        pagerAdapter = new ImageSlidePagerAdapter(this);
        viewPager = findViewById(R.id.image_pager);

        fetchUserData(userId, () ->{
            Log.d(TAG, "fetched user " + user.getUserName());
            user.loadWishlist(() -> {
                Log.d(TAG, "fetched wishlist");
                fetchItemData(itemId, () -> {
                    Log.d(TAG, "fetched item " + item.getItemName());
                    float price = item.getItemPrice();
                    vh.topTitleTextView.setText(item.getItemName());
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

                    List<String> currentUserWishlist = user.getWishlist();
                    boolean isInWishlist = false;
                    for (String wishlistItemId : currentUserWishlist){
                        if (wishlistItemId.equals(itemId)) {
                            isInWishlist = true;
                            break;
                        }
                    }
                    if (isInWishlist){
                        vh.wishlistButton.setText(R.string.remove_wishlist);
                        vh.wishlistButton.setOnClickListener(v -> {
                            removeFromWishlist(itemId);
                        });
                    } else {
                        vh.wishlistButton.setText(R.string.add_wishlist);
                        vh.wishlistButton.setOnClickListener(v2 -> {
                            addToWishlist(itemId);
                        });
                    }

                    // ViewPager acts as parent to the fragment collection,
                    // ImageSlidePagerAdapter handles each fragment (for displaying images)
                    viewPager.setAdapter(pagerAdapter);
                });});});
    }

    private void addToWishlist(String itemId){
        vh.wishlistButton.setText(R.string.remove_wishlist);
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
        vh.wishlistButton.setText(R.string.add_wishlist);
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

}
