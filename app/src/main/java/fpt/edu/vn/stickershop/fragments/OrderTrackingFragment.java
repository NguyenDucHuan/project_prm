package fpt.edu.vn.stickershop.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.adapters.OrderAdapter;
import fpt.edu.vn.stickershop.database.DatabaseHelper;
import fpt.edu.vn.stickershop.models.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderTrackingFragment extends Fragment {
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_tracking, container, false);

        recyclerView = view.findViewById(R.id.order_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dbHelper = new DatabaseHelper(getContext());

        // Truyền dbHelper vào adapter thay vì callback
        orderAdapter = new OrderAdapter(getOrders(), dbHelper);
        recyclerView.setAdapter(orderAdapter);

        return view;
    }

    private List<Order> getOrders() {
        List<Order> orders = new ArrayList<>();

        SharedPreferences prefs = requireContext().getSharedPreferences("StickerShopPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) return orders;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Cập nhật query để bao gồm order_type
        String query = "SELECT " +
                DatabaseHelper.COLUMN_ORDER_ID + ", " +
                DatabaseHelper.COLUMN_ORDER_STATUS + ", " +
                DatabaseHelper.COLUMN_ORDER_TOTAL + ", " +
                DatabaseHelper.COLUMN_ORDER_ADDRESS + ", " +
                "COALESCE(" + DatabaseHelper.COLUMN_ORDER_TIMESTAMP + ", 'N/A') as timestamp, " +
                "COALESCE(" + DatabaseHelper.COLUMN_ORDER_ITEM_COUNT + ", 0) as item_count, " +
                "COALESCE(" + DatabaseHelper.COLUMN_ORDER_TYPE + ", 'PURCHASE') as order_type " + // Thêm order_type
                "FROM " + DatabaseHelper.TABLE_ORDERS +
                " WHERE " + DatabaseHelper.COLUMN_USER_ID_FK + " = ? " +
                "ORDER BY " + DatabaseHelper.COLUMN_ORDER_ID + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_ID));
            String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_STATUS));
            double total = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_TOTAL));
            String address = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_ADDRESS));
            String timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));
            int itemCount = cursor.getInt(cursor.getColumnIndexOrThrow("item_count"));
            String orderType = cursor.getString(cursor.getColumnIndexOrThrow("order_type")); // Get order type

            orders.add(new Order(id, status, total, address, timestamp, itemCount, orderType));
        }
        cursor.close();
        db.close();

        return orders;
    }
}