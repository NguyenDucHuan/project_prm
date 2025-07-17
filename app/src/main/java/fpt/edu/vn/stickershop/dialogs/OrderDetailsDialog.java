package fpt.edu.vn.stickershop.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import fpt.edu.vn.stickershop.models.Order;
import android.view.Window;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.adapters.OrderItemAdapter;
import fpt.edu.vn.stickershop.database.DatabaseHelper;
import fpt.edu.vn.stickershop.models.OrderDetails;

public class OrderDetailsDialog extends Dialog {
    private DatabaseHelper dbHelper;
    private int orderId;

    private TextView orderIdText, orderStatusText, orderTotalText, orderAddressText, orderTimestampText;
    private RecyclerView orderItemsRecyclerView;

    public OrderDetailsDialog(Context context, int orderId, DatabaseHelper dbHelper) {
        super(context);
        this.orderId = orderId;
        this.dbHelper = dbHelper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_order_details);

        initViews();
        loadOrderDetails();
    }

    private void initViews() {
        orderIdText = findViewById(R.id.dialog_order_id);
        orderStatusText = findViewById(R.id.dialog_order_status);
        orderTotalText = findViewById(R.id.dialog_order_total);
        orderAddressText = findViewById(R.id.dialog_order_address);
        orderTimestampText = findViewById(R.id.dialog_order_timestamp);
        orderItemsRecyclerView = findViewById(R.id.dialog_order_items_recycler);

        orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        findViewById(R.id.dialog_close_button).setOnClickListener(v -> dismiss());
    }

    private void loadOrderDetails() {
        try {
            OrderDetails orderDetails = dbHelper.getOrderDetails(orderId);

            if (orderDetails != null) {
                Order order = orderDetails.getOrder();

                // KIỂM TRA NULL trước khi sử dụng
                if (order != null) {
                    // Hiển thị Order ID với prefix
                    String orderPrefix = order.isWithdrawalOrder() ? "WD" : "OR";
                    orderIdText.setText(String.format("Order #%s%03d", orderPrefix, order.getId()));

                    // SỬA: statusText → orderStatusText
                    orderStatusText.setText("Status: " + (order.getStatus() != null ? order.getStatus() : "Unknown"));

                    if (order.isWithdrawalOrder()) {
                        // SỬA: totalText → orderTotalText
                        orderTotalText.setText("Total: FREE WITHDRAWAL");
                    } else {
                        orderTotalText.setText(String.format("Total: $%.2f", order.getTotal()));
                    }

                    // SỬA: addressText → orderAddressText
                    orderAddressText.setText("Address: " + (order.getAddress() != null ? order.getAddress() : "N/A"));
                    // SỬA: timestampText → orderTimestampText
                    orderTimestampText.setText("Date: " + (order.getTimestamp() != null ? order.getTimestamp() : "N/A"));

                    // Setup RecyclerView cho order items
                    if (orderDetails.getOrderItems() != null && !orderDetails.getOrderItems().isEmpty()) {
                        OrderItemAdapter adapter = new OrderItemAdapter(orderDetails.getOrderItems());
                        // SỬA: itemsRecyclerView → orderItemsRecyclerView
                        orderItemsRecyclerView.setAdapter(adapter);
                    } else {
                        // Hiển thị thông báo không có items
                        android.util.Log.w("OrderDetailsDialog", "No order items found");
                    }
                } else {
                    android.util.Log.e("OrderDetailsDialog", "Order object is null");
                    orderIdText.setText("Order #N/A");
                    // SỬA tất cả variable names
                    orderStatusText.setText("Status: Unknown");
                    orderTotalText.setText("Total: N/A");
                    orderAddressText.setText("Address: N/A");
                    orderTimestampText.setText("Date: N/A");
                }
            } else {
                android.util.Log.e("OrderDetailsDialog", "OrderDetails is null for orderId: " + orderId);
            }
        } catch (Exception e) {
            android.util.Log.e("OrderDetailsDialog", "Error loading order details", e);
        }
    }
}