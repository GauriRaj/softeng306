package com.softeng306team15.plantoid.Models;

import java.util.List;

public class MainItem extends Item{

    @Override
    public boolean isBestSeller(){
        throw new RuntimeException(this.getClass().getSimpleName() + " doesn't have this method");
    }

    @Override
    public  boolean isNewItem(){
        throw new RuntimeException(this.getClass().getSimpleName() + " doesn't have this method");
    }
    public MainItem(String id, String category, String itemName, float itemPrice, List<String> pics, List<String> tags){
        this.id = id;
        this.category = category;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.pics = pics;
        this.tags = tags;
    }

    public MainItem(){

    }
}
