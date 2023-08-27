package com.softeng306team15.plantoid.UserModels;

import com.softeng306team15.plantoid.MyCallback;

import java.util.List;
import java.util.Map;

public interface IUser {
     String plantsAndTrees = "Plants and Trees";
     String potsAndPlanters = "Pots and Planters";
     String seedsAndSeedlings = "Seeds and Seedlings";
     String plantCardAndDecor = "Plant Care and Decor";

     String price0To5 = "0 - 4.99";
     String price5To15 = "5 - 14.99";
     String price15To25 = "15 - 24.99";
     String price25To50 = "25 - 49.99";
     String price50plus = "50+";

     String getId();

     void setId(String id);
     String getUserName();

     String getUserImage();

     String getPhoneNumber();

     String getPassword();

     String getEmail();


     String getAddress();

     Map<String, Integer> getCategoryHits();

     Map<String, Integer> getPriceRangeHits();

     List<String> getWishlist();

     void setWishlist(List<String> wishlist);

     void addToWishlist(String itemId);

     void removeFromWishlist(String itemId);

     String getTopCategory();

     String getTopPriceRange();

     void incrementCategoryHit(String category);

     void incrementPriceRangeHit(String priceRange);

     void updateUserName(String newUserName);

     void updateUserImage(String newUserImage);

     void updatePassword(String newPassword);

     void updatePhoneNumber(String newPhoneNumber);

     void updateEmail(String newEmail);

     void updateAddress(String newAddress);

     void loadWishlist(MyCallback callback);
     void createNewUserDocument(MyCallback myCallback);

}
