package fpt.edu.vn.stickershop.adapters;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.database.DatabaseHelper;
import fpt.edu.vn.stickershop.dialogs.OrderDetailsDialog;
import fpt.edu.vn.stickershop.models.Order;
import fpt.edu.vn.stickershop.models.OrderDetails;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orders;
    private DatabaseHelper dbHelper;

    public OrderAdapter(List<Order> orders, DatabaseHelper dbHelper) {
        this.orders = orders;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        // Hiển thị Order ID với prefix khác nhau dựa trên type
        String orderPrefix = order.isWithdrawalOrder() ? "WD" : "OR";
        holder.orderIdText.setText(String.format("#%s%03d", orderPrefix, order.getId()));

        // Hiển thị status với màu sắc khác nhau
        holder.statusText.setText(order.getStatus()); // Sửa từ orderStatusText thành statusText
        if (order.isWithdrawalOrder()) {
            holder.statusText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.purple_500));
        } else {
            holder.statusText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPrimary));
        }

        // Hiển thị total với format khác nhau
        if (order.isWithdrawalOrder()) {
            holder.totalText.setText("FREE WITHDRAWAL"); // Sửa từ orderTotalText thành totalText
            holder.totalText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.success_color));
        } else {
            holder.totalText.setText(String.format("$%.2f", order.getTotal()));
            holder.totalText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_primary));
        }

        holder.addressText.setText(order.getAddress()); // Sửa từ orderAddressText thành addressText
        holder.timestampText.setText(order.getTimestamp()); // Sửa từ orderTimestampText thành timestampText

        // Hiển thị item count với icon khác nhau
        String itemText = order.getItemCount() + " items";
        if (order.isWithdrawalOrder()) {
            itemText = "📦 " + itemText + " (Withdrawal)";
        } else {
            itemText = "🛒 " + itemText + " (Purchase)";
        }
        holder.itemCountText.setText(itemText); // Sửa từ orderItemsText thành itemCountText

        // Click listener để xem details
        holder.itemView.setOnClickListener(v -> {
            OrderDetailsDialog dialog = new OrderDetailsDialog(v.getContext(), order.getId(), dbHelper);
            dialog.show();
        });
    }

    private void loadOrderItems(RecyclerView recyclerView, int orderId) {
        OrderDetails orderDetails = dbHelper.getOrderDetails(orderId);

        if (orderDetails != null && orderDetails.getOrderItems() != null) {
            // Setup RecyclerView for order items
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
            OrderItemAdapter itemAdapter = new OrderItemAdapter(orderDetails.getOrderItems());
            recyclerView.setAdapter(itemAdapter);
        }
    }

    private int getStatusColor(Context context, String status) {
        switch (status.toLowerCase()) {
            case "pending":
                return ContextCompat.getColor(context, R.color.status_pending);
            case "processing":
                return ContextCompat.getColor(context, R.color.status_processing);
            case "delivered":
                return ContextCompat.getColor(context, R.color.status_delivered);
            case "cancelled":
                return ContextCompat.getColor(context, R.color.status_cancelled);
            default:
                return ContextCompat.getColor(context, R.color.status_default);
        }
    }


    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        LinearLayout orderHeader, detailsContainer;
        TextView orderIdText, statusText, totalText, addressText, timestampText, itemCountText;
        ImageView expandIcon;
        RecyclerView orderItemsRecycler;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderHeader = itemView.findViewById(R.id.order_header);
            detailsContainer = itemView.findViewById(R.id.order_details_container);
            orderIdText = itemView.findViewById(R.id.order_id_text);
            statusText = itemView.findViewById(R.id.order_status_text);
            totalText = itemView.findViewById(R.id.order_total_text);
            addressText = itemView.findViewById(R.id.order_address_text);
            timestampText = itemView.findViewById(R.id.order_timestamp_text);
            itemCountText = itemView.findViewById(R.id.order_item_count_text); // Đã đúng
            expandIcon = itemView.findViewById(R.id.expand_icon);
            orderItemsRecycler = itemView.findViewById(R.id.order_items_recycler);
        }
    }
}