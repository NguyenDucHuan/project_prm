package fpt.edu.vn.stickershop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.database.DatabaseHelper;
import fpt.edu.vn.stickershop.models.CartItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private Context context;
    private DatabaseHelper dbHelper;
    private OnCartUpdateListener listener;

    public interface OnCartUpdateListener {
        void onCartUpdated();
    }

    public CartAdapter(List<CartItem> cartItems, Context context, OnCartUpdateListener listener) {
        this.cartItems = cartItems;
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        holder.productName.setText(cartItem.getProductName());
        holder.productPrice.setText(currencyFormat.format(cartItem.getProductPrice()));
        holder.quantity.setText(String.valueOf(cartItem.getQuantity()));
        holder.totalPrice.setText(currencyFormat.format(cartItem.getTotalPrice()));

        if (cartItem.getProductImageUrl() != null && !cartItem.getProductImageUrl().isEmpty()) {
            Picasso.get()
                    .load(cartItem.getProductImageUrl())
                    .placeholder(R.drawable.ic_product_placeholder)
                    .error(R.drawable.ic_product_placeholder)
                    .into(holder.productImage);
        }

        // Increase quantity
        holder.increaseButton.setOnClickListener(v -> {
            int newQuantity = cartItem.getQuantity() + 1;
            updateQuantity(cartItem, newQuantity, position);
        });

        // Decrease quantity
        holder.decreaseButton.setOnClickListener(v -> {
            if (cartItem.getQuantity() > 1) {
                int newQuantity = cartItem.getQuantity() - 1;
                updateQuantity(cartItem, newQuantity, position);
            } else {
                removeFromCart(cartItem, position);
            }
        });

        // Remove item
        holder.removeButton.setOnClickListener(v -> {
            removeFromCart(cartItem, position);
        });
    }

    private void updateQuantity(CartItem cartItem, int newQuantity, int position) {
        // Update in database
        dbHelper.updateCartQuantity(cartItem.getProductId(), newQuantity);

        // Update in model
        cartItem.setQuantity(newQuantity);

        // Notify adapter
        notifyItemChanged(position);

        // Notify parent fragment
        if (listener != null) {
            listener.onCartUpdated();
        }
    }

    private void removeFromCart(CartItem cartItem, int position) {
        // Remove from database
        dbHelper.removeFromCart(cartItem.getProductId());

        // Remove from list
        cartItems.remove(position);

        // Notify adapter
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cartItems.size());

        // Notify parent fragment
        if (listener != null) {
            listener.onCartUpdated();
        }

        Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, quantity, totalPrice;
        Button increaseButton, decreaseButton, removeButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.cart_item_image);
            productName = itemView.findViewById(R.id.cart_item_name);
            productPrice = itemView.findViewById(R.id.cart_item_price);
            quantity = itemView.findViewById(R.id.cart_item_quantity);
            totalPrice = itemView.findViewById(R.id.cart_item_total);
            increaseButton = itemView.findViewById(R.id.increase_quantity_button);
            decreaseButton = itemView.findViewById(R.id.decrease_quantity_button);
            removeButton = itemView.findViewById(R.id.remove_item_button);
        }
    }
}