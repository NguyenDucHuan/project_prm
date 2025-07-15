package fpt.edu.vn.stickershop.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.database.DatabaseHelper;
import fpt.edu.vn.stickershop.dialogs.AddToCartDialog;
import fpt.edu.vn.stickershop.models.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products;
    private AddToCartDialog.OnCartUpdatedListener cartUpdateListener;

    public ProductAdapter(List<Product> products, AddToCartDialog.OnCartUpdatedListener cartUpdateListener) {
        this.products = products;
        this.cartUpdateListener = cartUpdateListener;
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);

        // Đảm bảo layout parameters đúng cho grid
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        view.setLayoutParams(layoutParams);

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);

        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.format("$%.2f", product.getPrice()));

        // Load product image
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Picasso.get()
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_product_placeholder)
                    .error(R.drawable.ic_product_placeholder)
                    .fit()
                    .centerCrop()
                    .into(holder.productImage);
        } else {
            holder.productImage.setImageResource(R.drawable.ic_product_placeholder);
        }

        // Click vào product để xem detail
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("product_id", product.getId());

            try {
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_productDetailFragment, bundle);
            } catch (Exception e) {
                Toast.makeText(v.getContext(), "Opening product details...", Toast.LENGTH_SHORT).show();
            }
        });

        // Click vào button Add to Cart sẽ hiện modal
        if (holder.addToCartButton != null) {
            holder.addToCartButton.setOnClickListener(v -> {
                if (cartUpdateListener != null) {
                    AddToCartDialog dialog = new AddToCartDialog(v.getContext(), product, cartUpdateListener);
                    dialog.show();
                } else {
                    // Fallback to simple add to cart
                    addToCartSimple(v, product);
                }
            });
        }
    }

    private void addToCartSimple(View view, Product product) {
        SharedPreferences prefs = view.getContext().getSharedPreferences("StickerShopPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            DatabaseHelper dbHelper = new DatabaseHelper(view.getContext());
            dbHelper.addToCart(userId, product.getId(), 1);
            Toast.makeText(view.getContext(), "Added to cart", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(view.getContext(), "Please login first", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice;
        ImageView productImage;
        Button addToCartButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productImage = itemView.findViewById(R.id.product_image);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        }
    }
}