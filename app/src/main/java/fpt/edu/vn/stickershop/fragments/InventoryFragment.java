package fpt.edu.vn.stickershop.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.adapters.InventoryAdapter;
import fpt.edu.vn.stickershop.database.DatabaseHelper;
import fpt.edu.vn.stickershop.dialogs.WithdrawDialog;
import fpt.edu.vn.stickershop.models.InventoryItem;

public class InventoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private Button withdrawButton;
    private DatabaseHelper dbHelper;
    private InventoryAdapter inventoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        recyclerView = view.findViewById(R.id.inventory_recycler_view);
        emptyTextView = view.findViewById(R.id.empty_inventory_text);
        withdrawButton = view.findViewById(R.id.withdraw_button);

        dbHelper = new DatabaseHelper(getContext());

        setupRecyclerView();
        loadInventoryItems();

        // Setup withdraw button
        withdrawButton.setOnClickListener(v -> showWithdrawDialog());

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void showWithdrawDialog() {
        List<InventoryItem> inventoryItems = getInventoryItems();

        if (inventoryItems.isEmpty()) {
            Toast.makeText(getContext(), "Your inventory is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        WithdrawDialog dialog = new WithdrawDialog(getContext(), inventoryItems, dbHelper, this::loadInventoryItems);
        dialog.show();
    }

    private List<InventoryItem> getInventoryItems() {
        SharedPreferences prefs = requireContext().getSharedPreferences("StickerShopPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", 1);
        return dbHelper.getUserInventory(userId);
    }

    private void loadInventoryItems() {
        List<InventoryItem> inventoryItems = getInventoryItems();

        if (inventoryItems.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            withdrawButton.setVisibility(View.GONE);
            emptyTextView.setText("Your inventory is empty!\nSpin the Lucky Wheel to collect items.");
        } else {
            inventoryAdapter = new InventoryAdapter(inventoryItems);
            recyclerView.setAdapter(inventoryAdapter);
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
            withdrawButton.setVisibility(View.VISIBLE);
        }
    }

    private int getCurrentUserId() {
        SharedPreferences prefs = requireContext().getSharedPreferences("StickerShopPrefs", Context.MODE_PRIVATE);
        return prefs.getInt("user_id", 1);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadInventoryItems();
    }
}