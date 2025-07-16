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
import fpt.edu.vn.stickershop.adapters.CartAdapter;
import fpt.edu.vn.stickershop.adapters.ProductAdapter;
import fpt.edu.vn.stickershop.database.DatabaseHelper;
import fpt.edu.vn.stickershop.models.CartItem;
import fpt.edu.vn.stickershop.models.Product;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment implements CartAdapter.OnCartUpdateListener {
    private TextView cartTotalTextView;
    private Button checkoutButton;
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
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
        loadCartData();

        checkoutButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_cartFragment_to_checkoutFragment);
        });

        return view;
    }

    private void loadCartData() {
        List<CartItem> cartItems = getCartItems();
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
            cartAdapter = new CartAdapter(cartItems, getContext(), this);
            cartRecyclerView.setAdapter(cartAdapter);
        }
    }

    private List<CartItem> getCartItems() {
        try {
            SharedPreferences prefs = getContext().getSharedPreferences("StickerShopPrefs", Context.MODE_PRIVATE);
            int userId = prefs.getInt("user_id", -1);

            if (userId != -1) {
                return dbHelper.getCartItems(userId);
            }
        } catch (Exception e) {
            Log.e("CartFragment", "Error loading cart items", e);
        }
        return new ArrayList<>();
    }

    private double calculateTotal(List<CartItem> items) {
        double total = 0.0;
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    @Override
    public void onCartUpdated() {
        // Reload cart data when cart is updated
        loadCartData();
    }
}