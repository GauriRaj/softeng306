package com.softeng306team15.plantoid.Models;

import java.util.List;

public class DetailedItem extends Item{

    protected String itemDesc;
    protected String scientific;

    @Override
    public List<String> getImages(){
        return images;
    }

    @Override
    public String getItemDesc(){
        return itemDesc;
    }
    @Override
    public String getScientific(){
        return scientific;
    }

    public DetailedItem(String id, String category, String itemName, float itemPrice, List<String> images, List<String> tags, String itemDesc, String scientific){
        this.id = id;
        this.category = category;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.images = images;
        this.tags = tags;
        this.itemDesc = itemDesc;
        // Set empty string for items without scientific name
        if(scientific == null){
            this.scientific = "";
        } else {
            this.scientific = scientific;
        }
    }

    public DetailedItem(){

    }
}
