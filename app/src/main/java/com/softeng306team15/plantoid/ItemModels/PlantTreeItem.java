package com.softeng306team15.plantoid.ItemModels;

import java.util.List;

public class PlantTreeItem extends Item{

    @Override
    public String getPlantSubTag(){
        for(String tag: tags){
            if (plantAndTreeSubTags.contains(tag)){
                return tag;
            }
        }
        return "Misc";
    }

    public PlantTreeItem(String id, String category, String itemName, float itemPrice, List<String> pics, List<String> tags){
        this.id = id;
        this.category = category;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.images = pics;
        this.tags = tags;
    }

    public PlantTreeItem(){

    }
}
