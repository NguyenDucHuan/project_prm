package fpt.edu.vn.stickershop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.adapters.InventoryAdapter;
import fpt.edu.vn.stickershop.database.DatabaseHelper;
import fpt.edu.vn.stickershop.models.InventoryItem;

public class InventoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private DatabaseHelper dbHelper;
    private InventoryAdapter inventoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        recyclerView = view.findViewById(R.id.inventory_recycler_view);
        emptyTextView = view.findViewById(R.id.empty_inventory_text);

        dbHelper = new DatabaseHelper(getContext());

        setupRecyclerView();
        loadInventoryItems();

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadInventoryItems() {
        int currentUserId = getCurrentUserId();
        // For now, show a simple message since getUserInventory method needs to be implemented
        emptyTextView.setText("Inventory feature will be implemented soon!\nItems won from Lucky Wheel will appear here.");
        recyclerView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.VISIBLE);
    }

    private int getCurrentUserId() {
        // Implement user session management
        return 1; // Default user ID for now
    }
}