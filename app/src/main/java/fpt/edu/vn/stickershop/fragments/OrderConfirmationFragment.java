package fpt.edu.vn.stickershop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import fpt.edu.vn.stickershop.R;

public class OrderConfirmationFragment extends Fragment {
    private TextView orderIdTextView, orderTotalTextView, orderAddressTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_confirmation, container, false);

        orderIdTextView = view.findViewById(R.id.order_id);
        orderTotalTextView = view.findViewById(R.id.order_total);
        orderAddressTextView = view.findViewById(R.id.order_address);

        // TODO: Get order details from arguments or SQLite
        Bundle args = getArguments();
        if (args != null) {
            orderIdTextView.setText("Order ID: " + args.getInt("order_id", 0));
            orderTotalTextView.setText(String.format("Total: $%.2f", args.getDouble("order_total", 0.0)));
            orderAddressTextView.setText("Address: " + args.getString("order_address", ""));
        }

        return view;
    }
}