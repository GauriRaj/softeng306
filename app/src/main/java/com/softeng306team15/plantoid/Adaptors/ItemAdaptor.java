package com.softeng306team15.plantoid.Adaptors;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.softeng306team15.plantoid.Activities.CategoryActivity;
import com.softeng306team15.plantoid.Models.IItem;
import com.softeng306team15.plantoid.Models.MainItem;
import com.softeng306team15.plantoid.Models.PlantCareDecorItem;
import com.softeng306team15.plantoid.Models.PlantTreeItem;
import com.softeng306team15.plantoid.Models.PotPlanterItem;
import com.softeng306team15.plantoid.Models.SeedSeedlingItem;
import com.softeng306team15.plantoid.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import kotlin.reflect.KVisibility;

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

        }

    }

    private class CategoryViewHolder extends ViewHolder{
        /*
        This View Holder is used as a basis for category items, Plant Care and Decor uses this directly
         */
        RelativeLayout globalTagLayout;
        ImageView globalTagImageView;
        TextView globalTagTextView;

        public CategoryViewHolder(View currentItemView){
            super(currentItemView);
            globalTagLayout = currentItemView.findViewById(R.id.global_tag_layout);
            globalTagImageView = currentItemView.findViewById(R.id.global_tag_imageView);
            globalTagLayout = currentItemView.findViewById(R.id.global_tag_textView);
        }

    }

    private class PlantAndTreeViewHolder extends CategoryViewHolder{
        RelativeLayout subCategoryLayout;
        TextView subCategoryTextView;
        ImageView subCategoryImageView;

        private PlantAndTreeViewHolder(View currentItemView){
            super(currentItemView);
            subCategoryLayout = currentItemView.findViewById(R.id.category_tag_layout);
            subCategoryImageView = currentItemView.findViewById(R.id.category_tag_imageView);
            subCategoryTextView = currentItemView.findViewById(R.id.category_tag_textView);
        }

    }

    private class SeedAndSeedlingViewHolder extends CategoryViewHolder{
        RelativeLayout subCategoryLayout;
        RelativeLayout seedSeedlingTagLayout;
        TextView subCategoryTextView;
        ImageView subCategoryImageView;
        TextView seedSeedlingTagTextView;
        ImageView seedSeedlingTagImageView;

        private SeedAndSeedlingViewHolder(View currentItemView){
            super(currentItemView);
            subCategoryLayout = currentItemView.findViewById(R.id.category_tag_layout);
            subCategoryImageView = currentItemView.findViewById(R.id.category_tag_imageView);
            subCategoryTextView = currentItemView.findViewById(R.id.category_tag_textView);
            seedSeedlingTagLayout = currentItemView.findViewById(R.id.seed_tag_layout);
            seedSeedlingTagImageView = currentItemView.findViewById(R.id.seed_tag_imageView);
            seedSeedlingTagTextView = currentItemView.findViewById(R.id.seed_tag_textView);
        }
    }

    private class PotAndPlanterViewHolder extends CategoryViewHolder{
        RelativeLayout sizeTagLayout;
        ImageView sizeTagImageView;
        TextView sizeTagTextView;

        private PotAndPlanterViewHolder(View currentItemView){
            super(currentItemView);
            sizeTagLayout = currentItemView.findViewById(R.id.category_tag_layout);
            sizeTagImageView = currentItemView.findViewById(R.id.category_tag_imageView);
            sizeTagTextView = currentItemView.findViewById(R.id.category_tag_textView);
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
            ((PlantAndTreeViewHolder) vh).subCategoryLayout.setVisibility(View.VISIBLE);

            String subCategory = currentItem.getPlantSubTag();
            switch (subCategory){
                case "Evergreen":
                    ((PlantAndTreeViewHolder) vh).subCategoryImageView.setImageResource(R.drawable.icon_evergreen);
                    ((PlantAndTreeViewHolder) vh).subCategoryTextView.setText(R.string.tag_evergreen);
                    break;
                case "Deciduous":
                    ((PlantAndTreeViewHolder) vh).subCategoryImageView.setImageResource(R.drawable.icon_deciduous);
                    ((PlantAndTreeViewHolder) vh).subCategoryTextView.setText(R.string.tag_deciduous);
                    break;
                case "Flowering":
                    ((PlantAndTreeViewHolder) vh).subCategoryImageView.setImageResource(R.drawable.icon_flowering);
                    ((PlantAndTreeViewHolder) vh).subCategoryTextView.setText(R.string.tag_flowering);
                    break;
                case "Fruit":
                    ((PlantAndTreeViewHolder) vh).subCategoryImageView.setImageResource(R.drawable.icon_fruit);
                    ((PlantAndTreeViewHolder) vh).subCategoryTextView.setText(R.string.tag_fruit);
                    break;
                case "Vegetable":
                    ((PlantAndTreeViewHolder) vh).subCategoryImageView.setImageResource(R.drawable.icon_vegetable);
                    ((PlantAndTreeViewHolder) vh).subCategoryTextView.setText(R.string.tag_vegetable);
                    break;
                case "Herb":
                    ((PlantAndTreeViewHolder) vh).subCategoryImageView.setImageResource(R.drawable.icon_herb);
                    ((PlantAndTreeViewHolder) vh).subCategoryTextView.setText(R.string.tag_herb);
                    break;
                case "Succulent":
                    ((PlantAndTreeViewHolder) vh).subCategoryImageView.setImageResource(R.drawable.icon_succulent);
                    ((PlantAndTreeViewHolder) vh).subCategoryTextView.setText(R.string.tag_succulent);
                    break;
                default:
                    // Disable the tag if we cannot get the category or category not valid
                    ((PlantAndTreeViewHolder) vh).subCategoryLayout.setVisibility(View.INVISIBLE);
            }

            if(currentItem.isBestSeller()){
                ((PlantAndTreeViewHolder) vh).globalTagLayout.setVisibility(View.VISIBLE);
                ((PlantAndTreeViewHolder) vh).globalTagImageView.setImageResource(R.drawable.icon_best_seller);
                ((PlantAndTreeViewHolder) vh).globalTagTextView.setText(R.string.tag_bestSeller);
            } else if(currentItem.isNewItem()){
                ((PlantAndTreeViewHolder) vh).globalTagLayout.setVisibility(View.VISIBLE);
                ((PlantAndTreeViewHolder) vh).globalTagImageView.setImageResource(R.drawable.icon_new_in);
                ((PlantAndTreeViewHolder) vh).globalTagTextView.setText(R.string.tag_newIn);
            } else{
                ((PlantAndTreeViewHolder) vh).globalTagLayout.setVisibility(View.INVISIBLE);
            }

        }else if(vh.getClass() == SeedAndSeedlingViewHolder.class){

            if(currentItem.isBestSeller()){
                ((SeedAndSeedlingViewHolder) vh).globalTagLayout.setVisibility(View.VISIBLE);
                ((SeedAndSeedlingViewHolder) vh).globalTagImageView.setImageResource(R.drawable.icon_best_seller);
                ((SeedAndSeedlingViewHolder) vh).globalTagTextView.setText(R.string.tag_bestSeller);
            } else if(currentItem.isNewItem()){
                ((SeedAndSeedlingViewHolder) vh).globalTagLayout.setVisibility(View.VISIBLE);
                ((SeedAndSeedlingViewHolder) vh).globalTagImageView.setImageResource(R.drawable.icon_new_in);
                ((SeedAndSeedlingViewHolder) vh).globalTagTextView.setText(R.string.tag_newIn);
            } else{
                ((SeedAndSeedlingViewHolder) vh).globalTagLayout.setVisibility(View.INVISIBLE);
            }

            String subCategory = currentItem.getPlantSubTag();
            switch (subCategory) {
                case "Evergreen":
                    ((SeedAndSeedlingViewHolder) vh).subCategoryImageView.setImageResource(R.drawable.icon_evergreen);
                    ((SeedAndSeedlingViewHolder) vh).subCategoryTextView.setText(R.string.tag_evergreen);
                    break;
                case "Deciduous":
                    ((SeedAndSeedlingViewHolder) vh).subCategoryImageView.setImageResource(R.drawable.icon_deciduous);
                    ((SeedAndSeedlingViewHolder) vh).subCategoryTextView.setText(R.string.tag_deciduous);
                    break;
                case "Flowering":
                    ((SeedAndSeedlingViewHolder) vh).subCategoryImageView.setImageResource(R.drawable.icon_flowering);
                    ((SeedAndSeedlingViewHolder) vh).subCategoryTextView.setText(R.string.tag_flowering);
                    break;
                case "Fruit":
                    ((SeedAndSeedlingViewHolder) vh).subCategoryImageView.setImageResource(R.drawable.icon_fruit);
                    ((SeedAndSeedlingViewHolder) vh).subCategoryTextView.setText(R.string.tag_fruit);
                    break;
                case "Vegetable":
                    ((SeedAndSeedlingViewHolder) vh).subCategoryImageView.setImageResource(R.drawable.icon_vegetable);
                    ((SeedAndSeedlingViewHolder) vh).subCategoryTextView.setText(R.string.tag_vegetable);
                    break;
                case "Herb":
                    ((SeedAndSeedlingViewHolder) vh).subCategoryImageView.setImageResource(R.drawable.icon_herb);
                    ((SeedAndSeedlingViewHolder) vh).subCategoryTextView.setText(R.string.tag_herb);
                    break;
                case "Succulent":
                    ((SeedAndSeedlingViewHolder) vh).subCategoryImageView.setImageResource(R.drawable.icon_succulent);
                    ((SeedAndSeedlingViewHolder) vh).subCategoryTextView.setText(R.string.tag_succulent);
                    break;
                default:
                    // Disable the tag if we cannot get the category or category not valid
                    ((SeedAndSeedlingViewHolder) vh).subCategoryLayout.setVisibility(View.INVISIBLE);
            }

            ((SeedAndSeedlingViewHolder) vh).seedSeedlingTagLayout.setVisibility(View.VISIBLE);
            if(currentItem.getSeedSubTag().equals("Seed")){
                ((SeedAndSeedlingViewHolder) vh).seedSeedlingTagImageView.setImageResource(R.drawable.icon_seed);
                ((SeedAndSeedlingViewHolder) vh).seedSeedlingTagTextView.setText(R.string.tag_seed);
            } else if (currentItem.getSeedSubTag().equals("Seedling")) {
                ((SeedAndSeedlingViewHolder) vh).seedSeedlingTagImageView.setImageResource(R.drawable.icon_seedling);
                ((SeedAndSeedlingViewHolder) vh).seedSeedlingTagTextView.setText(R.string.tag_seedling);
            } else{
                ((SeedAndSeedlingViewHolder) vh).seedSeedlingTagLayout.setVisibility(View.INVISIBLE);
            }

        }else if(vh.getClass() == PotAndPlanterViewHolder.class){
            if(currentItem.isBestSeller()){
                ((PotAndPlanterViewHolder) vh).globalTagLayout.setVisibility(View.VISIBLE);
                ((PotAndPlanterViewHolder) vh).globalTagImageView.setImageResource(R.drawable.icon_best_seller);
                ((PotAndPlanterViewHolder) vh).globalTagTextView.setText(R.string.tag_bestSeller);
            } else if(currentItem.isNewItem()){
                ((PotAndPlanterViewHolder) vh).globalTagLayout.setVisibility(View.VISIBLE);
                ((PotAndPlanterViewHolder) vh).globalTagImageView.setImageResource(R.drawable.icon_new_in);
                ((PotAndPlanterViewHolder) vh).globalTagTextView.setText(R.string.tag_newIn);
            } else{
                ((PotAndPlanterViewHolder) vh).globalTagLayout.setVisibility(View.INVISIBLE);
            }

            ((PotAndPlanterViewHolder) vh).sizeTagLayout.setVisibility(View.VISIBLE);
            String size = currentItem.getSize();
            switch (size){
                case "S":
                    ((PotAndPlanterViewHolder) vh).sizeTagImageView.setImageResource(R.drawable.icon_small_pot);
                    ((PotAndPlanterViewHolder) vh).sizeTagTextView.setText(R.string.tag_smallPot);
                    break;
                case "M":
                    ((PotAndPlanterViewHolder) vh).sizeTagImageView.setImageResource(R.drawable.icon_medium_pot);
                    ((PotAndPlanterViewHolder) vh).sizeTagTextView.setText(R.string.tag_mediumPot);
                    break;
                case "L":
                    ((PotAndPlanterViewHolder) vh).sizeTagImageView.setImageResource(R.drawable.icon_large_pot);
                    ((PotAndPlanterViewHolder) vh).sizeTagTextView.setText(R.string.tag_largePot);
                    break;
                default:
                    ((PotAndPlanterViewHolder) vh).sizeTagLayout.setVisibility(View.INVISIBLE);
            }

        }else if(vh.getClass() == CategoryViewHolder.class){ //Plant care and decor category
            if(currentItem.isBestSeller()){
                ((CategoryViewHolder) vh).globalTagLayout.setVisibility(View.VISIBLE);
                ((CategoryViewHolder) vh).globalTagImageView.setImageResource(R.drawable.icon_best_seller);
                ((CategoryViewHolder) vh).globalTagTextView.setText(R.string.tag_bestSeller);
            } else if(currentItem.isNewItem()){
                ((CategoryViewHolder) vh).globalTagLayout.setVisibility(View.VISIBLE);
                ((CategoryViewHolder) vh).globalTagImageView.setImageResource(R.drawable.icon_new_in);
                ((CategoryViewHolder) vh).globalTagTextView.setText(R.string.tag_newIn);
            } else{
                ((CategoryViewHolder) vh).globalTagLayout.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
