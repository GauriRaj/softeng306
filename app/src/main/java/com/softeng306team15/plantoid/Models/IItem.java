package com.softeng306team15.plantoid.Models;

import java.util.List;

public interface IItem {

    public String getId();

    public String getCategory();

    public String getItemDesc();

    public String getItemName();

    public float getItemPrice();

    public List<String> getPics();

    public String getKeyPic();

    public List<String> getTags();

    public boolean isBestSeller();

    public boolean isNewItem();

    public String getSize();

    public String getPlantSubTag();

    public String getSeedSubTag();

}
