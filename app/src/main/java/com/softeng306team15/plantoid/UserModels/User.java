package com.softeng306team15.plantoid.UserModels;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.softeng306team15.plantoid.MyCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements IUser {

    String id, userName, email;


    Map<String, Integer> categoryHits, priceRangeHits;

    @Exclude
    List<String> wishlist;

    @Exclude
    public String getId(){
        return this.id;
    }

    @Exclude
    public void setId(String id){
        this.id = id;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getEmail() {
        return this.email;
    }

    public Map<String, Integer> getCategoryHits() {
        return this.categoryHits;
    }

    public Map<String, Integer> getPriceRangeHits() {
        return this.priceRangeHits;
    }

    @Exclude
    @Override
    public List<String> getWishlist() {
        return this.wishlist;
    }

    @Exclude
    public void setWishlist(List<String> wishlist){
        this.wishlist = wishlist;
    }

    public void addToWishlist(String newItemId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d(TAG, "adding to wishlist");
        Map<String, String> newItem = new HashMap<>();
        newItem.put("itemId", newItemId);

        //adding new wishlist item to firestore database
        db.collection("users").document(id).collection("wishlist").add(newItem)
                .addOnSuccessListener((OnSuccessListener) o -> {
                    Log.d(TAG, "Successfully added item to wishlist");
                    loadWishlist(null);
                })
                .addOnFailureListener(e -> Log.d(TAG, "Failed adding item to wishlist"));

    }

    public void removeFromWishlist(String itemId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //get the wishlist item that is to be deleted from firestore
        db.collection("users").document(id).collection("wishlist")
                .whereEqualTo("itemId", itemId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot results = task.getResult();
                        for(QueryDocumentSnapshot wishlistDoc: results){
                            String wishlistDocId = wishlistDoc.getId();
                            //remove the document from wishlist in firestore
                            db.collection("users").document(id)
                                    .collection("wishlist").document(wishlistDocId)
                                    .delete().addOnSuccessListener((OnSuccessListener) o -> {
                                        Log.d(TAG, "Successfully removed item from wishlist");
                                        loadWishlist(null);
                                    })
                                    .addOnFailureListener(e -> Log.d(TAG, "Failed removing item from wishlist"));
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });
    }


    /**
     * Loads user's wishlist from firestore. Method needed for wishlist reloading after adding or
     * deleting item. As wishlist is stored in a subcollection this method is also needed as
     * firestore will not load the wishlist data when loading user data.
     */
    public void loadWishlist(MyCallback callback){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(id).collection("wishlist").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot results = task.getResult();
                        List<String> updatedWishlist = new ArrayList<>();
                        for(QueryDocumentSnapshot wishlistDoc: results){
                            updatedWishlist.add((String) wishlistDoc.get("itemId"));
                        }
                        wishlist = updatedWishlist;
                        if (callback != null){
                            Log.d(TAG, "user loaded wishlist");
                            callback.onCallback();
                        }

                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });
    }

    @Exclude
    public String getTopCategory() {
        int bestHits = 0;
        String topCat = plantsAndTrees;
        for (Map.Entry<String, Integer> entry : categoryHits.entrySet()) {
            String category = entry.getKey();
            Integer hit = entry.getValue();
            if (hit > bestHits) {
                bestHits = hit;
                topCat = category;
            }
        }

        return  topCat;
    }

    @Exclude
    public String getTopPriceRange() {
        int bestHits = 0;
        String topPriceRange = price50plus;
        for (Map.Entry<String, Integer> entry : priceRangeHits.entrySet()) {
            String priceRange = entry.getKey();
            Integer hit = entry.getValue();
            if (hit > bestHits) {
                bestHits = hit;
                topPriceRange = priceRange;
            }
        }

        return  topPriceRange;
    }

    public void incrementCategoryHit(String category) {
        int hit = categoryHits.get(category);
        categoryHits.replace(category, hit+1);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(id).update("categoryHits", categoryHits).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d(TAG, "failed to update category hits");
            }
        });
    }

    public void incrementPriceRangeHit(String priceRange) {
        int hit = priceRangeHits.get(priceRange);
        priceRangeHits.replace(priceRange, hit+1);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(id).update("priceRangeHits", priceRangeHits).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d(TAG, "failed to update price range hits");
            }
        });
    }

    public User(){}

    public User(String userName, String email){
        //set default image
        this.email = email;
        this.userName = userName;

        this.categoryHits = new HashMap<>();
        categoryHits.put(plantsAndTrees, 0);
        categoryHits.put(potsAndPlanters, 0);
        categoryHits.put(seedsAndSeedlings, 0);
        categoryHits.put(plantCardAndDecor, 0);

        this.priceRangeHits = new HashMap<>();
        priceRangeHits.put(price0To5, 0);
        priceRangeHits.put(price5To15, 0);
        priceRangeHits.put(price15To25, 0);
        priceRangeHits.put(price25To50, 0);
        priceRangeHits.put(price50plus, 0);
    }

    /**
     * Creates user in the firestore database
     */
    public void createNewUserDocument(MyCallback myCallback){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").add(this)
                .addOnSuccessListener(documentReference -> {
                    id = documentReference.getId();
                    Log.d(TAG, "Successfully added user");
                    myCallback.onCallback();
                })
                .addOnFailureListener(e -> Log.d(TAG, "Failed adding user"));

    }

}
