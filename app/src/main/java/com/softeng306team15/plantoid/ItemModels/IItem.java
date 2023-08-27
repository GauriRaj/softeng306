package com.softeng306team15.plantoid.ItemModels;

import java.util.List;

public interface IItem {

    public String getId();

    public void setId(String id);

    public String getCategory();

    public String getItemDesc();

    public String getItemName();

    public float getItemPrice();

    public List<String> getImages();

    public void setImages(List<String> images);

    public String getKeyPic();

    public List<String> getTags();

    public void setTags(List<String> tags);

    public boolean isBestSeller();

    public boolean isNewItem();

    public String getSize();

    public String getPlantSubTag();

    public String getSeedSubTag();

}
