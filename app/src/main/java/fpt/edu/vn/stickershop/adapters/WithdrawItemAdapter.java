package fpt.edu.vn.stickershop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.models.InventoryItem;
import java.util.ArrayList;
import java.util.List;

public class WithdrawItemAdapter extends RecyclerView.Adapter<WithdrawItemAdapter.WithdrawViewHolder> {
    private List<InventoryItem> inventoryItems;
    private List<InventoryItem> selectedItems;
    private OnSelectionChangeListener listener;

    public interface OnSelectionChangeListener {
        void onSelectionChanged();
    }

    public WithdrawItemAdapter(List<InventoryItem> inventoryItems, OnSelectionChangeListener listener) {
        this.inventoryItems = inventoryItems;
        this.selectedItems = new ArrayList<>();
        this.listener = listener;

        // Initialize all items with 0 withdraw quantity
        for (InventoryItem item : inventoryItems) {
            // Add a field to track withdraw quantity (we'll use a temporary solution)
            item.setWithdrawQuantity(0);
        }
    }

    @NonNull
    @Override
    public WithdrawViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_withdraw_selection, parent, false);
        return new WithdrawViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WithdrawViewHolder holder, int position) {
        InventoryItem item = inventoryItems.get(position);

        holder.productName.setText(item.getProductName());
        holder.availableQuantity.setText("Available: " + item.getQuantity());
        holder.withdrawQuantity.setText(String.valueOf(item.getWithdrawQuantity()));

        // Load product image
        if (item.getProductImage() != null && !item.getProductImage().isEmpty()) {
            if (item.getProductImage().startsWith("http")) {
                Picasso.get()
                        .load(item.getProductImage())
                        .placeholder(R.drawable.ic_product_placeholder)
                        .error(R.drawable.ic_product_placeholder)
                        .fit()
                        .centerCrop()
                        .into(holder.productImage);
            } else {
                try {
                    int resourceId = Integer.parseInt(item.getProductImage());
                    holder.productImage.setImageResource(resourceId);
                } catch (NumberFormatException e) {
                    holder.productImage.setImageResource(R.drawable.ic_product_placeholder);
                }
            }
        } else {
            holder.productImage.setImageResource(R.drawable.ic_product_placeholder);
        }

        // Setup quantity controls
        holder.decreaseButton.setOnClickListener(v -> {
            if (item.getWithdrawQuantity() > 0) {
                item.setWithdrawQuantity(item.getWithdrawQuantity() - 1);
                holder.withdrawQuantity.setText(String.valueOf(item.getWithdrawQuantity()));
                updateSelectedItems();
                if (listener != null) listener.onSelectionChanged();
            }
        });

        holder.increaseButton.setOnClickListener(v -> {
            if (item.getWithdrawQuantity() < item.getQuantity()) {
                item.setWithdrawQuantity(item.getWithdrawQuantity() + 1);
                holder.withdrawQuantity.setText(String.valueOf(item.getWithdrawQuantity()));
                updateSelectedItems();
                if (listener != null) listener.onSelectionChanged();
            }
        });
    }

    private void updateSelectedItems() {
        selectedItems.clear();
        for (InventoryItem item : inventoryItems) {
            if (item.getWithdrawQuantity() > 0) {
                // Create a copy with the withdraw quantity
                InventoryItem selectedItem = new InventoryItem(
                        item.getId(),
                        item.getUserId(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getProductImage(),
                        item.getWithdrawQuantity(), // Use withdraw quantity instead of total
                        item.getDateObtained()
                );
                selectedItems.add(selectedItem);
            }
        }
    }

    public List<InventoryItem> getSelectedItems() {
        updateSelectedItems();
        return selectedItems;
    }

    @Override
    public int getItemCount() {
        return inventoryItems.size();
    }

    static class WithdrawViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, availableQuantity, withdrawQuantity;
        Button decreaseButton, increaseButton;

        WithdrawViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            availableQuantity = itemView.findViewById(R.id.available_quantity);
            withdrawQuantity = itemView.findViewById(R.id.withdraw_quantity);
            decreaseButton = itemView.findViewById(R.id.decrease_button);
            increaseButton = itemView.findViewById(R.id.increase_button);
        }
    }
}