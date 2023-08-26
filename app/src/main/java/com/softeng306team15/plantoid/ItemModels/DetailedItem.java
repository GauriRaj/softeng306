package com.softeng306team15.plantoid.ItemModels;

import java.util.List;

public class DetailedItem extends Item{

    protected String itemDesc;

    @Override
    public List<String> getImages(){
        return images;
    }

    @Override
    public String getItemDesc(){
        return itemDesc;
    }

    public DetailedItem(String id, String category, String itemName, float itemPrice, List<String> images, List<String> tags, String itemDesc){
        this.id = id;
        this.category = category;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.images = images;
        this.tags = tags;
        this.itemDesc = itemDesc;
    }

    public DetailedItem(){

    }
}
