package fpt.edu.vn.stickershop.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
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
        OrderDetails orderDetails = dbHelper.getOrderDetails(orderId);

        if (orderDetails != null) {
            orderIdText.setText(String.format("Order #%d", orderDetails.getOrderId()));
            orderStatusText.setText(orderDetails.getStatus());
            orderTotalText.setText(String.format("Total: $%.2f", orderDetails.getTotal()));
            orderAddressText.setText(String.format("Address: %s", orderDetails.getAddress()));
            orderTimestampText.setText(String.format("Ordered: %s", orderDetails.getTimestamp()));

            // Setup items recycler view
            OrderItemAdapter adapter = new OrderItemAdapter(orderDetails.getOrderItems());
            orderItemsRecyclerView.setAdapter(adapter);
        }
    }
}