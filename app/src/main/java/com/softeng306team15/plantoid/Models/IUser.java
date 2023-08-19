package com.softeng306team15.plantoid.Models;

import java.util.List;
import java.util.Map;

public interface IUser {
    final String plantsAndTrees = "Plants and Trees";
    final String potsAndPlanters = "Pots and Planters";
    final String seedsAndSeedlings = "Seeds and Seedlings";
    final String plantCardAndDecor = "Plant Care and Decor";

    final String price0To5 = "0 - 4.99";
    final String price5To15 = "5 - 14.99";
    final String price15To25 = "15 - 24.99";
    final String price25To50 = "25 - 49.99";
    final String price50plus = "50+";

    public String getId();

    public void setId(String id);
    public String getUserName();

    public String getUserImage();

    public String getPhoneNumber();

    public String getPassword();

    public String getEmail();


    public String getAddress();

    public Map<String, Integer> getCategoryHits();

    public Map<String, Integer> getPriceRangeHits();

    public List<String> getWishlist();

    public void setWishlist(List<String> wishlist);

    public void addToWishlist(String itemId);

    public void removeFromWishlist(String itemId);

    public String getTopCategory();

    public String getTopPriceRange();

    public void incrementCategoryHit(String category);

    public void incrementPriceRangeHit(String priceRange);

    public void updateUserName(String newUserName);

    public void updateUserImage(String newUserImage);

    public void updatePassword(String newPassword);

    public void updatePhoneNumber(String newPhoneNumber);

    public void updateEmail(String newEmail);

    public void updateAddress(String newAddress);


    public void createNewUserDocument();

}
