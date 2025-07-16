
package fpt.edu.vn.stickershop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.models.InventoryItem;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {
    private List<InventoryItem> inventoryItems;

    public InventoryAdapter(List<InventoryItem> inventoryItems) {
        this.inventoryItems = inventoryItems;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryItem item = inventoryItems.get(position);

        holder.productNameTextView.setText(item.getProductName());
        holder.quantityTextView.setText(String.format("x%d", item.getQuantity()));
        holder.dateTextView.setText(item.getDateObtained());

        // Load product image
        if (item.getProductImage() != null && !item.getProductImage().isEmpty()) {
            if (item.getProductImage().startsWith("http")) {
                Picasso.get()
                        .load(item.getProductImage())
                        .placeholder(R.drawable.ic_product_placeholder)
                        .error(R.drawable.ic_product_placeholder)
                        .fit()
                        .centerCrop()
                        .into(holder.productImageView);
            } else {
                try {
                    int resourceId = Integer.parseInt(item.getProductImage());
                    holder.productImageView.setImageResource(resourceId);
                } catch (NumberFormatException e) {
                    holder.productImageView.setImageResource(R.drawable.ic_product_placeholder);
                }
            }
        } else {
            holder.productImageView.setImageResource(R.drawable.ic_product_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return inventoryItems.size();
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView, quantityTextView, dateTextView;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.inventory_item_image);
            productNameTextView = itemView.findViewById(R.id.inventory_item_name);
            quantityTextView = itemView.findViewById(R.id.inventory_item_quantity);
            dateTextView = itemView.findViewById(R.id.inventory_item_date);
        }
    }
}