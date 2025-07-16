package fpt.edu.vn.stickershop.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.database.DatabaseHelper;
import com.squareup.picasso.Picasso;

public class ProductDetailFragment extends Fragment {
    private TextView productNameTextView, productPriceTextView;
    private ImageView productImageView;
    private Button addToCartButton;
    private EditText quantityEditText;
    private DatabaseHelper dbHelper;
    private int currentProductId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        productNameTextView = view.findViewById(R.id.product_name);
        productPriceTextView = view.findViewById(R.id.product_price);
        productImageView = view.findViewById(R.id.product_image);
        addToCartButton = view.findViewById(R.id.add_to_cart_button);
        quantityEditText = view.findViewById(R.id.quantity_edit_text);
        dbHelper = new DatabaseHelper(getContext());

        // Set default quantity
        if (quantityEditText != null) {
            quantityEditText.setText("1");
        }

        // Get product ID from arguments and fetch from SQLite
        Bundle args = getArguments();
        if (args != null) {
            currentProductId = args.getInt("product_id", -1);
            loadProductDetails(currentProductId);
        }

        addToCartButton.setOnClickListener(v -> {
            addToCart();
        });

        return view;
    }

    private void loadProductDetails(int productId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCTS,
                null,
                DatabaseHelper.COLUMN_PRODUCT_ID + "=?",
                new String[]{String.valueOf(productId)},
                null, null, null);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE));
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_IMAGE));

            productNameTextView.setText(name);
            productPriceTextView.setText(String.format("$%.2f", price));

            if (imageUrl != null && !imageUrl.isEmpty()) {
                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_product_placeholder)
                        .error(R.drawable.ic_product_placeholder)
                        .into(productImageView);
            }
        }
        cursor.close();
    }

    private void addToCart() {
        if (currentProductId == -1) {
            Toast.makeText(getContext(), "Product not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get quantity from EditText
        int quantity = 1;
        if (quantityEditText != null) {
            try {
                String quantityText = quantityEditText.getText().toString().trim();
                if (!quantityText.isEmpty()) {
                    quantity = Integer.parseInt(quantityText);
                    if (quantity <= 0) {
                        Toast.makeText(getContext(), "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Get user ID from SharedPreferences
        SharedPreferences prefs = getContext().getSharedPreferences("StickerShopPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add to cart
        try {
            dbHelper.addToCart(userId, currentProductId, quantity);
            Toast.makeText(getContext(), "Added " + quantity + " item(s) to cart", Toast.LENGTH_SHORT).show();

            // Reset quantity to 1
            if (quantityEditText != null) {
                quantityEditText.setText("1");
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error adding to cart", Toast.LENGTH_SHORT).show();
        }
    }
}