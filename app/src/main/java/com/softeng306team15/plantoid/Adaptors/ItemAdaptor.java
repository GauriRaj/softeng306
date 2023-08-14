package com.softeng306team15.plantoid.Adaptors;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.softeng306team15.plantoid.Models.IItem;
import com.softeng306team15.plantoid.Models.MainItem;
import com.softeng306team15.plantoid.Models.PlantCareDecorItem;
import com.softeng306team15.plantoid.Models.PlantTreeItem;
import com.softeng306team15.plantoid.Models.PotPlanterItem;
import com.softeng306team15.plantoid.Models.SeedSeedlingItem;
import com.softeng306team15.plantoid.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ItemAdaptor extends RecyclerView.Adapter<ItemAdaptor.ViewHolder> {

    //View Holder classes
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        /*
        This View Holder is to be used for the main activity items, search items and wishlist items
         */
        TextView itemNameTextView;
        TextView itemPriceTextView;
        ImageView itemKeyImageView;

        public ViewHolder(View currentItemView){
            super(currentItemView);
            itemNameTextView = currentItemView.findViewById(R.id.title_textView);
            itemPriceTextView = currentItemView.findViewById(R.id.price_textView);
            itemKeyImageView = currentItemView.findViewById(R.id.key_imageView);
        }

        @Override
        public void onClick(View currentItemView){
            //click implementation
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

    private List<IItem> items;
    private Context context;
    private int layoutId;

    public ItemAdaptor(@NonNull List<IItem> items, int resource) {
        this.items = items;
        this.layoutId = resource;
    }

    @NonNull
    @Override
    public ItemAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View currentItemView = inflater.inflate(layoutId, parent, false);

        //Create the appropriate view holder according to item type
        ViewHolder holder;
        if(items.get(0).getClass() == MainItem.class){
            holder = new ViewHolder(currentItemView);
        }else if(items.get(0).getClass() == PlantTreeItem.class){
            holder = new PlantAndTreeViewHolder(currentItemView);
        }else if(items.get(0).getClass() == SeedSeedlingItem.class){
            holder = new SeedAndSeedlingViewHolder(currentItemView);
        }else if(items.get(0).getClass() == PotPlanterItem.class){
            holder = new PotAndPlanterViewHolder(currentItemView);
        }else if(items.get(0).getClass() == PlantCareDecorItem.class){
            holder = new CategoryViewHolder(currentItemView);
        }else{
            return null;
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdaptor.ViewHolder vh, int position) {
        // Get the data object for the item view in this position
        MainItem currentItem = (MainItem) items.get(position);

        //shared view holder functionality
        vh.itemNameTextView.setText(currentItem.getItemName());
        vh.itemPriceTextView.setText("$"+currentItem.getItemPrice());
        Picasso.get().load(currentItem.getKeyPic()).into(vh.itemKeyImageView);

        //Category item specific view holder functionality
        if(vh.getClass() == PlantAndTreeViewHolder.class){
            ((PlantAndTreeViewHolder) vh).subCategoryTextView.setText(currentItem.getPlantSubTag());

            if(currentItem.isBestSeller()){
                ((PlantAndTreeViewHolder) vh).bestSellerImageView.setVisibility(View.VISIBLE);
            }
            if(currentItem.isNewItem()){
                ((PlantAndTreeViewHolder) vh).newItemImageView.setVisibility(View.VISIBLE);
            }
        }else if(vh.getClass() == SeedAndSeedlingViewHolder.class){
            ((SeedAndSeedlingViewHolder) vh).subCategoryTextView.setText(currentItem.getPlantSubTag());

            if(currentItem.isBestSeller()){
                ((SeedAndSeedlingViewHolder) vh).bestSellerImageView.setVisibility(View.VISIBLE);
            }
            if(currentItem.isNewItem()){
                ((SeedAndSeedlingViewHolder) vh).newItemImageView.setVisibility(View.VISIBLE);
            }
            if(currentItem.getSeedSubTag().equals("Seed")){
                //set image view with seed tag image
            } else if (currentItem.getSeedSubTag().equals("Seedling")) {
                //set image view with seedling tag image
            }
        }else if(vh.getClass() == PotAndPlanterViewHolder.class){
            ((PotAndPlanterViewHolder) vh).sizeTagTextView.setText(currentItem.getSize());

            if(currentItem.isBestSeller()){
                ((PotAndPlanterViewHolder) vh).bestSellerImageView.setVisibility(View.VISIBLE);
            }
            if(currentItem.isNewItem()){
                ((PotAndPlanterViewHolder) vh).newItemImageView.setVisibility(View.VISIBLE);
            }
        }else if(vh.getClass() == CategoryViewHolder.class){ //Plant care and decor category
            if(currentItem.isBestSeller()){
                ((CategoryViewHolder) vh).bestSellerImageView.setVisibility(View.VISIBLE);
            }
            if(currentItem.isNewItem()){
                ((CategoryViewHolder) vh).newItemImageView.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
