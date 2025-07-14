package fpt.edu.vn.stickershop.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.adapters.ProductAdapter;
import fpt.edu.vn.stickershop.database.DatabaseHelper;
import fpt.edu.vn.stickershop.models.Product;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {
    private TextView cartTotalTextView;
    private Button checkoutButton;
    private RecyclerView cartRecyclerView;
    private ProductAdapter cartAdapter;
    private DatabaseHelper dbHelper;
    private TextView emptyCartTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartTotalTextView = view.findViewById(R.id.cart_total);
        checkoutButton = view.findViewById(R.id.checkout_button);
        cartRecyclerView = view.findViewById(R.id.cart_recycler_view);
        emptyCartTextView = view.findViewById(R.id.empty_cart_text);
        dbHelper = new DatabaseHelper(getContext());

        // Load cart items
        List<Product> cartItems = getCartItems();
        double total = calculateTotal(cartItems);
        
        if (cartItems.isEmpty()) {
            cartRecyclerView.setVisibility(View.GONE);
            emptyCartTextView.setVisibility(View.VISIBLE);
            checkoutButton.setEnabled(false);
        cartTotalTextView.setText("Total: $0.00");
        } else {
            cartRecyclerView.setVisibility(View.VISIBLE);
            emptyCartTextView.setVisibility(View.GONE);
            checkoutButton.setEnabled(true);
            cartTotalTextView.setText(String.format("Total: $%.2f", total));
            
            cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            cartAdapter = new ProductAdapter(cartItems);
            cartRecyclerView.setAdapter(cartAdapter);
        }

        checkoutButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_cartFragment_to_checkoutFragment);
        });

        return view;
    }

    private List<Product> getCartItems() {
        List<Product> cartItems = new ArrayList<>();
        try {
            SharedPreferences prefs = getContext().getSharedPreferences("StickerShopPrefs", Context.MODE_PRIVATE);
            int userId = prefs.getInt("user_id", -1);
            
            if (userId != -1) {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String query = "SELECT p.*, c.quantity FROM " + DatabaseHelper.TABLE_PRODUCTS + " p " +
                             "INNER JOIN " + DatabaseHelper.TABLE_CART + " c ON p." + DatabaseHelper.COLUMN_PRODUCT_ID + 
                             " = c." + DatabaseHelper.COLUMN_PRODUCT_ID_FK + 
                             " WHERE c." + DatabaseHelper.COLUMN_USER_ID_FK + " = ?";
                
                Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
                Log.d("CartFragment", "Found " + cursor.getCount() + " cart items");
                
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE));
                    String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_IMAGE));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_TYPE));
                    cartItems.add(new Product(id, name, price, imageUrl, type));
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("CartFragment", "Error loading cart items", e);
        }
        return cartItems;
    }

    private double calculateTotal(List<Product> items) {
        double total = 0.0;
        for (Product item : items) {
            total += item.getPrice();
        }
        return total;
    }
}