package fpt.edu.vn.stickershop.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
            NumberFormat vnd = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            totalAmountText.setText("Total Amount: " + vnd.format(totalAmount));
        }
    }

    private void processOrder() {
        String address = addressInput.getText().toString().trim();
        
        if (address.isEmpty()) {
            addressInput.setError("Please enter shipping address");
            Log.e("CheckoutFragment", "Shipping address is empty");
            return;
        }

        // Get user ID
        SharedPreferences prefs = requireContext().getSharedPreferences("StickerShopPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        Log.d("CheckoutFragment", "User ID: " + userId);
        
        if (userId == -1) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            Log.d("CheckoutFragment", "User not logged in");
            return;
        }

        try {
            // Get current cart items
            List<CartItem> cartItems = dbHelper.getCartItems(userId);

            if (cartItems.isEmpty()) {
                Toast.makeText(getContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
                Log.d("CheckoutFragment", "Cart is empty");
                return;
            }

            // Check payment method
            if (paymentMethodGroup.getCheckedRadioButtonId() == R.id.zalopay_radio) {
                Log.d("CheckoutFragment", "Processing ZaloPay payment");
                processZaloPayPayment();
            } else {
                Log.d("CheckoutFragment", "Processing COD order");
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
                // Chuyển đổi số tiền đúng định dạng ZaloPay (VND, không có phần lẻ)
                long amount = (long) (totalAmount * 1000); // Ví dụ: 5.97 USD → 5970 VND
                Log.d("CheckoutFragment", "Creating ZaloPay order with amount: " + amount);

                // Gọi API tạo đơn hàng
                CreateOrder orderApi = new CreateOrder();
                JSONObject data = orderApi.createOrder(String.valueOf(amount));

                if (data == null) {
                    throw new Exception("ZaloPay response is null");
                }

                Log.d("CheckoutFragment", "ZaloPay order response: " + data.toString());

                // Kiểm tra return_code
                if (!data.has("return_code") || !"1".equals(data.getString("return_code"))) {
                    String errorMessage = data.has("return_message") ? data.getString("return_message") : "Unknown error";
                    Log.e("CheckoutFragment", "Failed to create ZaloPay order: " + errorMessage);

                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "ZaloPay Error: " + errorMessage, Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                // Nhận zp_trans_token và gọi ZaloPay SDK
                String zpTransToken = data.getString("zp_trans_token");

                requireActivity().runOnUiThread(() -> {
                    ZaloPaySDK.getInstance().payOrder(requireActivity(), zpTransToken, "zalopay://app", new PayOrderListener() {
                        @Override
                        public void onPaymentSucceeded(String transactionId, String transToken, String appTransID) {
                            Toast.makeText(getContext(), "Payment successful!", Toast.LENGTH_SHORT).show();
                            Log.d("CheckoutFragment", "Payment succeeded: " + transactionId);

                            // Sau khi thanh toán thành công → lưu đơn hàng
                            int userId = requireContext().getSharedPreferences("StickerShopPrefs", Context.MODE_PRIVATE)
                                    .getInt("user_id", -1);
                            List<CartItem> cartItems = dbHelper.getCartItems(userId);
                            processCODOrder(userId, addressInput.getText().toString().trim(), cartItems);
                        }

                        @Override
                        public void onPaymentCanceled(String zpTransToken, String appTransID) {
                            Toast.makeText(getContext(), "Payment cancelled", Toast.LENGTH_SHORT).show();
                            Log.d("CheckoutFragment", "Payment cancelled: " + zpTransToken);
                        }

                        @Override
                        public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                            Toast.makeText(getContext(), "Payment error: " + zaloPayError.toString(), Toast.LENGTH_SHORT).show();
                            Log.e("CheckoutFragment", "Payment error: " + zaloPayError.toString());
                        }
                    });
                });

            } catch (Exception e) {
                Log.e("CheckoutFragment", "Error processing ZaloPay payment", e);
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "ZaloPay Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
                totalItems += item.getQuantity();
            }

            Toast.makeText(getContext(),
                    String.format("Order placed successfully! %d items, Total: %s", 
                        totalItems, NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(totalAmount)),
                    Toast.LENGTH_LONG).show();

            // Show confirmation with detailed info
            showOrderConfirmation(String.valueOf(orderId), address, totalItems, totalAmount);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ZaloPaySDK.getInstance().onResult(data);
    }
}