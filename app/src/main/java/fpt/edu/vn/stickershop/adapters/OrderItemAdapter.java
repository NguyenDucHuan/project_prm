package fpt.edu.vn.stickershop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.models.OrderItem;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {
    private List<OrderItem> orderItems;

    public OrderItemAdapter(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem item = orderItems.get(position);

        holder.productNameText.setText(item.getProductName());
        holder.quantityText.setText(String.format("Qty: %d", item.getQuantity()));
        holder.unitPriceText.setText(String.format("$%.2f each", item.getUnitPrice()));
        holder.totalPriceText.setText(String.format("$%.2f", item.getTotalPrice()));

        // Load product image
        String imagePath = item.getProductImage();
        if (imagePath != null && !imagePath.isEmpty()) {
            // Nếu là URL
            if (imagePath.startsWith("http")) {
                Picasso.get()
                        .load(imagePath)
                        .placeholder(R.drawable.ic_product_placeholder)
                        .error(R.drawable.ic_product_placeholder)
                        .fit()
                        .centerCrop()
                        .into(holder.productImage);
            }
            // Nếu là resource ID (từ drawable)
            else {
                try {
                    int resourceId = Integer.parseInt(imagePath);
                    holder.productImage.setImageResource(resourceId);
                } catch (NumberFormatException e) {
                    holder.productImage.setImageResource(R.drawable.ic_product_placeholder);
                }
            }
        } else {
            holder.productImage.setImageResource(R.drawable.ic_product_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return orderItems != null ? orderItems.size() : 0;
    }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productNameText, quantityText, unitPriceText, totalPriceText;

        OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.order_item_image);
            productNameText = itemView.findViewById(R.id.order_item_name);
            quantityText = itemView.findViewById(R.id.order_item_quantity);
            unitPriceText = itemView.findViewById(R.id.order_item_unit_price);
            totalPriceText = itemView.findViewById(R.id.order_item_total_price);
        }
    }
}