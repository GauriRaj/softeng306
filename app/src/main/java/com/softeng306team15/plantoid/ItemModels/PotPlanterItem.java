package com.softeng306team15.plantoid.ItemModels;

import java.util.List;

public class PotPlanterItem extends Item{

    @Override
    public String getSize(){
        for(String tag: tags){
            if (sizeSubTags.contains(tag)){
                return tag;
            }
        }
        return "Misc";
    }

    public PotPlanterItem(String id, String category, String itemName, float itemPrice, List<String> pics, List<String> tags){
        this.id = id;
        this.category = category;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.images = pics;
        this.tags = tags;
    }

    public PotPlanterItem(){

    }

}
