package fpt.edu.vn.stickershop.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.database.DatabaseHelper;
import fpt.edu.vn.stickershop.models.CartItem;

import java.util.List;
import java.util.UUID;

public class CheckoutFragment extends Fragment {
    private EditText addressInput;
    private Button confirmButton;
    private CardView confirmationCard;
    private TextView orderIdText;
    private TextView shippingAddressText;
    private Button continueShoppingButton;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);
        
        // Initialize views
        addressInput = view.findViewById(R.id.address_input);
        confirmButton = view.findViewById(R.id.confirm_button);
        confirmationCard = view.findViewById(R.id.confirmation_card);
        orderIdText = view.findViewById(R.id.order_id_text);
        shippingAddressText = view.findViewById(R.id.shipping_address_text);
        continueShoppingButton = view.findViewById(R.id.continue_shopping_button);
        dbHelper = new DatabaseHelper(getContext());

        confirmButton.setOnClickListener(v -> processOrder());
        continueShoppingButton.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_checkoutFragment_to_homeFragment));

        return view;
    }

    private void processOrder() {
        String address = addressInput.getText().toString().trim();

        if (address.isEmpty()) {
            addressInput.setError("Please enter shipping address");
            return;
        }

        // Get user ID
        SharedPreferences prefs = requireContext().getSharedPreferences("StickerShopPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Get current cart items
            List<CartItem> cartItems = dbHelper.getCartItems(userId);

            if (cartItems.isEmpty()) {
                Toast.makeText(getContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save order with items
            long orderId = dbHelper.saveOrderWithItems(userId, address, cartItems);

            if (orderId != -1) {
                // Clear cart after successful order
                dbHelper.clearCart(userId);

                // Calculate summary for display
                double totalAmount = 0;
                int totalItems = 0;
                for (CartItem item : cartItems) {
                    totalAmount += item.getTotalPrice();
                    totalItems += item.getQuantity();
                }

                Toast.makeText(getContext(),
                        String.format("Order placed successfully! %d items, Total: $%.2f", totalItems, totalAmount),
                        Toast.LENGTH_LONG).show();

                // Show confirmation with detailed info
                showOrderConfirmation(String.valueOf(orderId), address, totalItems, totalAmount);
            } else {
                Toast.makeText(getContext(), "Error processing order", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error processing order", Toast.LENGTH_SHORT).show();
            Log.e("CheckoutFragment", "Error processing order", e);
        }
    }
    private void showOrderConfirmation(String orderId, String address, int itemCount, double totalAmount) {
        // Hide input section
        addressInput.setEnabled(false);
        confirmButton.setVisibility(View.GONE);

        // Show confirmation section
        confirmationCard.setVisibility(View.VISIBLE);
        orderIdText.setText(String.format("Order ID: #%s", orderId));
        shippingAddressText.setText(String.format("Shipping Address: %s\nItems: %d\nTotal: $%.2f",
                address, itemCount, totalAmount));
    }
}