package fpt.edu.vn.stickershop.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.database.DatabaseHelper;
import fpt.edu.vn.stickershop.models.Product;

public class AddToCartDialog extends Dialog {
    private Product product;
    private ImageView productImage;
    private TextView productName, productPrice, quantityText, totalPrice;
    private Button btnDecrease, btnIncrease, btnCancel, btnAddToCart;
    private int quantity = 1;
    private OnCartUpdatedListener listener;

    public interface OnCartUpdatedListener {
        void onCartUpdated();
    }

    public AddToCartDialog(@NonNull Context context, Product product, OnCartUpdatedListener listener) {
        super(context);
        this.product = product;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_to_cart_dialog);

        initViews();
        setupProduct();
        setupListeners();
        updateTotalPrice();
    }

    private void initViews() {
        productImage = findViewById(R.id.dialog_product_image);
        productName = findViewById(R.id.dialog_product_name);
        productPrice = findViewById(R.id.dialog_product_price);
        quantityText = findViewById(R.id.tv_quantity);
        totalPrice = findViewById(R.id.tv_total_price);
        btnDecrease = findViewById(R.id.btn_decrease);
        btnIncrease = findViewById(R.id.btn_increase);
        btnCancel = findViewById(R.id.btn_cancel);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);
    }

    private void setupProduct() {
        productName.setText(product.getName());
        productPrice.setText(String.format("$%.2f", product.getPrice()));

        // Load product image
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Picasso.get()
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_product_placeholder)
                    .error(R.drawable.ic_product_placeholder)
                    .into(productImage);
        } else {
            productImage.setImageResource(R.drawable.ic_product_placeholder);
        }
    }

    private void setupListeners() {
        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantityDisplay();
                updateTotalPrice();
            }
        });

        btnIncrease.setOnClickListener(v -> {
            quantity++;
            updateQuantityDisplay();
            updateTotalPrice();
        });

        btnCancel.setOnClickListener(v -> dismiss());

        btnAddToCart.setOnClickListener(v -> {
            addToCart();
        });
    }

    private void updateQuantityDisplay() {
        quantityText.setText(String.valueOf(quantity));
    }

    private void updateTotalPrice() {
        double total = product.getPrice() * quantity;
        totalPrice.setText(String.format("$%.2f", total));
    }

    private void addToCart() {
        // Get user ID from SharedPreferences
        SharedPreferences prefs = getContext().getSharedPreferences("StickerShopPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            dbHelper.addToCart(userId, product.getId(), quantity);

            Toast.makeText(getContext(),
                    String.format("Added %d %s to cart", quantity, quantity > 1 ? "items" : "item"),
                    Toast.LENGTH_SHORT).show();

            // Notify listener that cart was updated
            if (listener != null) {
                listener.onCartUpdated();
            }

            dismiss();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error adding to cart", Toast.LENGTH_SHORT).show();
        }
    }
}