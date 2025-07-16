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
import fpt.edu.vn.stickershop.models.WheelItem;

public class SpinResultAdapter extends RecyclerView.Adapter<SpinResultAdapter.ResultViewHolder> {
    private List<WheelItem> wonItems;

    public SpinResultAdapter(List<WheelItem> wonItems) {
        this.wonItems = wonItems;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spin_result, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        WheelItem item = wonItems.get(position);

        holder.productNameTextView.setText(item.getProductName());
        holder.quantityTextView.setText(String.format("x%d", item.getQuantity()));

        // Load product image using Picasso (same as other adapters in your project)
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
                // Handle local resource
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
        return wonItems.size();
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView, quantityTextView;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.product_image);
            productNameTextView = itemView.findViewById(R.id.product_name);
            quantityTextView = itemView.findViewById(R.id.quantity);
        }
    }
}