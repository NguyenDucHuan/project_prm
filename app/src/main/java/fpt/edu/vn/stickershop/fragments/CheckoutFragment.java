package fpt.edu.vn.stickershop.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.database.DatabaseHelper;
import fpt.edu.vn.stickershop.models.CartItem;
import org.json.JSONObject;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;
import fpt.edu.vn.stickershop.Api.CreateOrder;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CheckoutFragment extends Fragment {
    private EditText addressInput;
    private Button confirmButton;
    private CardView confirmationCard;
    private TextView orderIdText;
    private TextView shippingAddressText;
    private TextView totalAmountText;
    private Button continueShoppingButton;
    private RadioGroup paymentMethodGroup;
    private DatabaseHelper dbHelper;
    private double totalAmount = 0.0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);
        
        // Initialize ZaloPay SDK
        ZaloPaySDK.init(2553, Environment.SANDBOX);
        
        // Initialize views
        addressInput = view.findViewById(R.id.address_input);
        confirmButton = view.findViewById(R.id.confirm_button);
        confirmationCard = view.findViewById(R.id.confirmation_card);
        orderIdText = view.findViewById(R.id.order_id_text);
        shippingAddressText = view.findViewById(R.id.shipping_address_text);
        totalAmountText = view.findViewById(R.id.total_amount);
        continueShoppingButton = view.findViewById(R.id.continue_shopping_button);
        paymentMethodGroup = view.findViewById(R.id.payment_method_group);
        dbHelper = new DatabaseHelper(getContext());

        // Load cart total
        loadCartTotal();

        confirmButton.setOnClickListener(v -> processOrder());
        continueShoppingButton.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_checkoutFragment_to_homeFragment));

        return view;
    }

    private void loadCartTotal() {
        SharedPreferences prefs = requireContext().getSharedPreferences("StickerShopPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            List<CartItem> cartItems = dbHelper.getCartItems(userId);
            totalAmount = 0.0;
            for (CartItem item : cartItems) {
                totalAmount += item.getTotalPrice();
            }
            // Convert USD to VND (1 USD = 25,000 VND)
            double vndAmount = totalAmount * 25000;
            NumberFormat vnd = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            totalAmountText.setText("Total Amount: " + vnd.format(vndAmount));
        }
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

            // Check payment method
            if (paymentMethodGroup.getCheckedRadioButtonId() == R.id.zalopay_radio) {
                processZaloPayPayment();
            } else {
                processCODOrder(userId, address, cartItems);
            }

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error processing order", Toast.LENGTH_SHORT).show();
            Log.e("CheckoutFragment", "Error processing order", e);
        }
    }

    private void processZaloPayPayment() {
        new Thread(() -> {
            try {
                CreateOrder orderApi = new CreateOrder();
                // Convert USD to VND (1 USD = 25,000 VND) and convert to long
                long vndAmount = (long)(totalAmount * 25000);
                JSONObject data = orderApi.createOrder(String.valueOf(vndAmount));

                if (!"1".equals(data.getString("return_code"))) {
                    requireActivity().runOnUiThread(() -> 
                        Toast.makeText(getContext(), "Failed to create ZaloPay order", Toast.LENGTH_SHORT).show());
                    return;
                }

                String zpTransToken = data.getString("zp_trans_token");

                requireActivity().runOnUiThread(() -> {
                    // Use the app's scheme URL for callback
                    ZaloPaySDK.getInstance().payOrder(requireActivity(), 
                        zpTransToken, 
                        "stickershop://app", // Use our app's scheme URL
                        new PayOrderListener() {
                            @Override
                            public void onPaymentSucceeded(final String transactionId, final String transToken, final String appTransID) {
                                // Process successful payment
                                requireActivity().runOnUiThread(() -> {
                                    // Get user info and cart items
                                    SharedPreferences prefs = requireContext().getSharedPreferences("StickerShopPrefs", Context.MODE_PRIVATE);
                                    int userId = prefs.getInt("user_id", -1);
                                    String address = addressInput.getText().toString().trim();
                                    List<CartItem> cartItems = dbHelper.getCartItems(userId);

                                    // Save order to database
                                    long orderId = dbHelper.saveOrderWithItems(userId, address, cartItems);
                                    
                                    if (orderId != -1) {
                                        // Clear cart after successful order
                                        dbHelper.clearCart(userId);

                                        // Save payment details
                                        savePaymentDetails(transactionId, transToken, appTransID, vndAmount);

                                        // Show success message
                                        NumberFormat vnd = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                                        String message = String.format("Payment successful!\nOrder ID: #%d\nTotal: %s", 
                                            orderId, vnd.format(vndAmount));
                                        
                                        new android.app.AlertDialog.Builder(requireContext())
                                            .setTitle("Order Confirmation")
                                            .setMessage(message)
                                            .setPositiveButton("Continue Shopping", (dialog, which) -> {
                                                // Navigate to home screen
                                                Navigation.findNavController(requireView())
                                                    .navigate(R.id.action_checkoutFragment_to_homeFragment);
                                            })
                                            .setCancelable(false)
                                            .show();
                                    } else {
                                        Toast.makeText(getContext(), "Error saving order", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onPaymentCanceled(String zpTransToken, String appTransID) {
                                requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(), "Payment cancelled", Toast.LENGTH_SHORT).show();
                                    Log.d("CheckoutFragment", "Payment cancelled: " + appTransID);
                                });
                            }

                            @Override
                            public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                                requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(), 
                                        "Payment error: " + zaloPayError.toString(), 
                                        Toast.LENGTH_SHORT).show();
                                    Log.e("CheckoutFragment", "Payment error: " + zaloPayError.toString());
                                });
                            }
                    });
                });

            } catch (Exception e) {
                Log.e("CheckoutFragment", "Error processing ZaloPay payment", e);
                requireActivity().runOnUiThread(() -> 
                    Toast.makeText(getContext(), "Error processing payment", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void processCODOrder(int userId, String address, List<CartItem> cartItems) {
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
            }
            // Convert USD to VND
            double vndAmount = totalAmount * 25000;

            Toast.makeText(getContext(),
                    String.format("Order placed successfully! %d items, Total: %s", 
                        totalItems, NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(vndAmount)),
                    Toast.LENGTH_LONG).show();

            // Show confirmation with detailed info
            showOrderConfirmation(String.valueOf(orderId), address, totalItems, vndAmount);
        } else {
            Toast.makeText(getContext(), "Error processing order", Toast.LENGTH_SHORT).show();
        }
    }

    private void showOrderConfirmation(String orderId, String address, int itemCount, double totalAmount) {
        // Hide input section
        addressInput.setEnabled(false);
        confirmButton.setVisibility(View.GONE);
        paymentMethodGroup.setEnabled(false);

        // Show confirmation section
        confirmationCard.setVisibility(View.VISIBLE);
        orderIdText.setText(String.format("Order ID: #%s", orderId));
        NumberFormat vnd = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        shippingAddressText.setText(String.format("Shipping Address: %s\nItems: %d\nTotal: %s",
                address, itemCount, vnd.format(totalAmount)));
    }

    private void savePaymentDetails(String transactionId, String transToken, String appTransID, long amount) {
        try {
            // TODO: Save payment details to database
            Log.d("CheckoutFragment", String.format(
                "Payment details - TransactionID: %s, Token: %s, AppTransID: %s, Amount: %d VND",
                transactionId, transToken, appTransID, amount));
        } catch (Exception e) {
            Log.e("CheckoutFragment", "Error saving payment details", e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ZaloPaySDK.getInstance().onResult(data); // Rất quan trọng nếu bạn không truyền từ Fragment
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}