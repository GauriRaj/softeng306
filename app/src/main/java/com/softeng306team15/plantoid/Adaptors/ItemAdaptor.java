package com.softeng306team15.plantoid.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.softeng306team15.plantoid.Models.IItem;
import com.softeng306team15.plantoid.Models.MainItem;
import com.softeng306team15.plantoid.Models.PlantCareDecorItem;
import com.softeng306team15.plantoid.Models.PlantTreeItem;
import com.softeng306team15.plantoid.Models.PotPlanterItem;
import com.softeng306team15.plantoid.Models.SeedSeedlingItem;
import com.softeng306team15.plantoid.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ItemAdaptor extends ArrayAdapter {

    private class ViewHolder{
        /*
        This View Holder is to be used for the main activity items, search items and wishlist items
         */
        TextView itemNameTextView;
        TextView itemPriceTextView;
        ImageView itemKeyImageView;

        public ViewHolder(View currentItemView){
            itemNameTextView = currentItemView.findViewById(R.id.item_name_text_view);
            itemPriceTextView = currentItemView.findViewById(R.id.item_price_text_view);
            itemKeyImageView = currentItemView.findViewById(R.id.item_key_image_view);
        }

    }

    private class CategoryViewHolder extends ViewHolder{
        /*
        This View Holder is used as a basis for category items, Plant Care and Decor uses this directly
         */
        ImageView bestSellerImageView;
        ImageView newItemImageView;

        public CategoryViewHolder(View currentItemView){
            super(currentItemView);
            bestSellerImageView = currentItemView.findViewById(R.id.best_seller_image_view);
            newItemImageView = currentItemView.findViewById(R.id.new_item_image_view);
        }

    }

    private class PlantAndTreeViewHolder extends CategoryViewHolder{

        TextView subCategoryTextView;

        private PlantAndTreeViewHolder(View currentItemView){
            super(currentItemView);
            subCategoryTextView = currentItemView.findViewById(R.id.plant_sub_tag_text_view);
        }

    }

    private class SeedAndSeedlingViewHolder extends CategoryViewHolder{

        TextView subCategoryTextView;
        ImageView seedSeedlingTagImageView;

        private SeedAndSeedlingViewHolder(View currentItemView){
            super(currentItemView);
            subCategoryTextView = currentItemView.findViewById(R.id.plant_sub_tag_text_view);
            seedSeedlingTagImageView = currentItemView.findViewById(R.id.seed_seedling_tag_image_view);
        }
    }

    private class PotAndPlanterViewHolder extends CategoryViewHolder{
        TextView sizeTagTextView;

        private PotAndPlanterViewHolder(View currentItemView){
            super(currentItemView);
            sizeTagTextView = currentItemView.findViewById(R.id.size_tag_text_view);
        }
    }

    int layoutId;
    Context context;
    List<IItem> items;

    public ItemAdaptor(@NonNull Context context, int resource, @NonNull List<IItem> items) {
        super(context, resource, items);

        layoutId = resource;
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentListViewItem = convertView; //reference to current view item

        // If not being reused inflate the view
        if (currentListViewItem == null) {
            currentListViewItem = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);
        }

        IItem currentItem = items.get(position);

        if(currentItem.getClass() == MainItem.class){
            return populateMainItem(currentItem, currentListViewItem);
        } else if(currentItem.getClass() == PlantTreeItem.class){
            return populatePlantTreeItem(currentItem, currentListViewItem);
        } else if (currentItem.getClass() == SeedSeedlingItem.class) {
            return populateSeedSeedlingItem(currentItem, currentListViewItem);
        } else if (currentItem.getClass() == PotPlanterItem.class) {
            return populatePotPlanterItem(currentItem, currentListViewItem);
        } else if (currentItem.getClass() == PlantCareDecorItem.class) {
            return populatePlantCareDecorItem(currentItem, currentListViewItem);
        } else{
            return null;
        }

    }

    private View populateMainItem(IItem currentItem, View currentListViewItem) {
        ViewHolder vh = new ViewHolder(currentListViewItem);

        vh.itemNameTextView.setText(currentItem.getItemName());
        vh.itemPriceTextView.setText("$"+currentItem.getItemPrice());
        Picasso.get().load(currentItem.getKeyPic()).into(vh.itemKeyImageView);

        vh.itemKeyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //functionality to open appropriate detailed view
            }
        });

        return  currentListViewItem;
        
    }

    private View populatePlantTreeItem(IItem currentItem, View currentListViewItem) {
        PlantAndTreeViewHolder vh = new PlantAndTreeViewHolder(currentListViewItem);

        vh.itemNameTextView.setText(currentItem.getItemName());
        vh.itemPriceTextView.setText("$"+currentItem.getItemPrice());
        vh.subCategoryTextView.setText(currentItem.getPlantSubTag());

        if(currentItem.isBestSeller()){
            vh.bestSellerImageView.setVisibility(View.VISIBLE);
        }
        if(currentItem.isNewItem()){
            vh.newItemImageView.setVisibility(View.VISIBLE);
        }

        Picasso.get().load(currentItem.getKeyPic()).into(vh.itemKeyImageView);

        vh.itemKeyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //functionality to open appropriate detailed view
            }
        });

        return  currentListViewItem;
    }

    private View populateSeedSeedlingItem(IItem currentItem, View currentListViewItem) {
        SeedAndSeedlingViewHolder vh = new SeedAndSeedlingViewHolder(currentListViewItem);

        vh.itemNameTextView.setText(currentItem.getItemName());
        vh.itemPriceTextView.setText("$"+currentItem.getItemPrice());
        vh.subCategoryTextView.setText(currentItem.getPlantSubTag());
        
        if(currentItem.getSeedSubTag().equals("Seed")){
            //set image view with seed tag image
        } else if (currentItem.getSeedSubTag().equals("Seedling")) {
            //set image view with seedling tag image
        }

        if(currentItem.isBestSeller()){
            vh.bestSellerImageView.setVisibility(View.VISIBLE);
        }
        if(currentItem.isNewItem()){
            vh.newItemImageView.setVisibility(View.VISIBLE);
        }

        Picasso.get().load(currentItem.getKeyPic()).into(vh.itemKeyImageView);

        vh.itemKeyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //functionality to open appropriate detailed view
            }
        });

        return  currentListViewItem;
    }

    private View populatePotPlanterItem(IItem currentItem, View currentListViewItem) {
        PotAndPlanterViewHolder vh = new PotAndPlanterViewHolder(currentListViewItem);

        vh.itemNameTextView.setText(currentItem.getItemName());
        vh.itemPriceTextView.setText("$"+currentItem.getItemPrice());
        vh.sizeTagTextView.setText(currentItem.getSize());

        if(currentItem.isBestSeller()){
            vh.bestSellerImageView.setVisibility(View.VISIBLE);
        }
        if(currentItem.isNewItem()){
            vh.newItemImageView.setVisibility(View.VISIBLE);
        }

        Picasso.get().load(currentItem.getKeyPic()).into(vh.itemKeyImageView);

        vh.itemKeyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //functionality to open appropriate detailed view
            }
        });

        return  currentListViewItem;
    }

    private View populatePlantCareDecorItem(IItem currentItem, View currentListViewItem) {
        CategoryViewHolder vh = new CategoryViewHolder(currentListViewItem);

        vh.itemNameTextView.setText(currentItem.getItemName());
        vh.itemPriceTextView.setText("$"+currentItem.getItemPrice());

        if(currentItem.isBestSeller()){
            vh.bestSellerImageView.setVisibility(View.VISIBLE);
        }
        if(currentItem.isNewItem()){
            vh.newItemImageView.setVisibility(View.VISIBLE);
        }

        Picasso.get().load(currentItem.getKeyPic()).into(vh.itemKeyImageView);

        vh.itemKeyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //functionality to open appropriate detailed view
            }
        });

        return  currentListViewItem;
    }
}
