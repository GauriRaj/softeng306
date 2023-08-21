package com.softeng306team15.plantoid.Models;

import com.google.firebase.firestore.Exclude;

import java.util.Arrays;
import java.util.List;

public abstract class Item implements IItem{
    protected String id, category, itemName;
    protected float itemPrice;
    protected List<String> images, tags;

    protected List<String> plantAndTreeSubTags = Arrays.asList("Evergreen", "Deciduous", "Flowering", "Fruit", "Vegetable", "Herb", "Succulent");
    protected List<String> seedAndSeedlingSubTags = Arrays.asList("Seed", "Seedling");

    protected List<String> sizeSubTags = Arrays.asList("S", "M", "L");

    public String getId(){
        return id;
    }

    public void setId(String id){ this.id = id;}

    public String getCategory(){
        return category;
    }

    @Exclude
    public String getItemDesc(){
        throw new RuntimeException(this.getClass().getSimpleName() + " doesn't have this method");
    }

    public String getItemName(){
        return itemName;
    }

    public float getItemPrice(){
        return itemPrice;
    }

    @Exclude
    public List<String> getImages(){
        throw new RuntimeException(this.getClass().getSimpleName() + " doesn't have this method");
    }

    public void setImages(List<String> images){
        this.images = images;
    }

    public String getKeyPic(){
        return images.get(0);
    }

    public List<String> getTags(){
        return tags;
    }

    public void setTags(List<String> tags){
        this.tags = tags;
    }

    public boolean isBestSeller(){
        for(String tag: tags){
            if (tag.equals("BestSeller")){
                return true;
            }
        }
        return false;
    }

    public boolean isNewItem(){
        for(String tag: tags){
            if (tag.equals("NewItem")){
                return true;
            }
        }
        return false;
    }

    @Exclude
    public String getSize(){
        throw new RuntimeException(this.getClass().getSimpleName() + " doesn't have this method");
    }

    @Exclude
    public String getPlantSubTag(){
        throw new RuntimeException(this.getClass().getSimpleName() + " doesn't have this method");
    }

    @Exclude
    public String getSeedSubTag(){
        throw new RuntimeException(this.getClass().getSimpleName() + " doesn't have this method");
    }
}
