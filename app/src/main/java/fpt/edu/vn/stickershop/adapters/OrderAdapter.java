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
import fpt.edu.vn.stickershop.models.Order;
import fpt.edu.vn.stickershop.models.OrderDetails;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orders;
    private DatabaseHelper dbHelper;
    private int expandedPosition = -1;

    public OrderAdapter(List<Order> orders, DatabaseHelper dbHelper) {
        this.orders = orders;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        boolean isExpanded = position == expandedPosition;

        // Bind order data
        holder.orderIdText.setText(String.format("Order #%d", order.getId()));
        holder.statusText.setText(order.getStatus());
        holder.totalText.setText(String.format("$%.2f", order.getTotal()));
        holder.addressText.setText(order.getAddress());
        holder.timestampText.setText(order.getTimestamp());
        holder.itemCountText.setText(String.format("%d items", order.getItemCount()));

        // Set status color
        int statusColor = getStatusColor(holder.itemView.getContext(), order.getStatus());
        holder.statusText.setTextColor(statusColor);

        // Set expand/collapse state
        holder.detailsContainer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.expandIcon.setImageResource(isExpanded ? R.drawable.ic_expand_less : R.drawable.ic_expand_more);

        // Load order items if expanded
        if (isExpanded) {
            loadOrderItems(holder.orderItemsRecycler, order.getId());
        }

        // Handle click to expand/collapse
        holder.orderHeader.setOnClickListener(v -> {
            int previousExpandedPosition = expandedPosition;

            if (isExpanded) {
                expandedPosition = -1;
            } else {
                expandedPosition = position;
            }

            // Notify changes with animation
            if (previousExpandedPosition != -1) {
                notifyItemChanged(previousExpandedPosition);
            }
            if (expandedPosition != -1) {
                notifyItemChanged(expandedPosition);
            }
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
            itemCountText = itemView.findViewById(R.id.order_item_count_text);
            expandIcon = itemView.findViewById(R.id.expand_icon);
            orderItemsRecycler = itemView.findViewById(R.id.order_items_recycler);
        }
    }
}