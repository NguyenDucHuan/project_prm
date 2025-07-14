package fpt.edu.vn.stickershop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.models.Order;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orders;

    public OrderAdapter(List<Order> orders) {
        this.orders = orders;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView, orderStatusTextView, orderTotalTextView;

        OrderViewHolder(View view) {
            super(view);
            orderIdTextView = view.findViewById(R.id.order_id);
            orderStatusTextView = view.findViewById(R.id.order_status);
            orderTotalTextView = view.findViewById(R.id.order_total);
        }
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.orderIdTextView.setText("Order ID: " + order.getId());
        holder.orderStatusTextView.setText("Status: " + order.getStatus());
        holder.orderTotalTextView.setText(String.format("Total: $%.2f", order.getTotal()));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}