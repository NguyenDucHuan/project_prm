package fpt.edu.vn.stickershop.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.adapters.WithdrawItemAdapter;
import fpt.edu.vn.stickershop.database.DatabaseHelper;
import fpt.edu.vn.stickershop.models.InventoryItem;
import java.util.ArrayList;
import java.util.List;

public class WithdrawDialog extends Dialog {
    private List<InventoryItem> inventoryItems;
    private List<InventoryItem> selectedItems;
    private DatabaseHelper dbHelper;
    private Runnable onWithdrawComplete;
    private WithdrawItemAdapter adapter;
    private EditText addressInput;
    private Button confirmButton;
    private TextView totalItemsText;

    public WithdrawDialog(Context context, List<InventoryItem> inventoryItems, DatabaseHelper dbHelper, Runnable onWithdrawComplete) {
        super(context);
        this.inventoryItems = inventoryItems;
        this.dbHelper = dbHelper;
        this.onWithdrawComplete = onWithdrawComplete;
        this.selectedItems = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_withdraw);

        initViews();
        setupRecyclerView();
        updateTotalItems();
    }

    private void initViews() {
        RecyclerView recyclerView = findViewById(R.id.withdraw_recycler_view);
        addressInput = findViewById(R.id.address_input);
        confirmButton = findViewById(R.id.confirm_withdraw_button);
        totalItemsText = findViewById(R.id.total_items_text);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        confirmButton.setOnClickListener(v -> processWithdrawal());
    }

    private void setupRecyclerView() {
        adapter = new WithdrawItemAdapter(inventoryItems, this::onItemSelectionChanged);
        RecyclerView recyclerView = findViewById(R.id.withdraw_recycler_view);
        recyclerView.setAdapter(adapter);
    }

    private void onItemSelectionChanged() {
        selectedItems = adapter.getSelectedItems();
        updateTotalItems();
    }

    private void updateTotalItems() {
        int totalSelected = selectedItems.stream().mapToInt(InventoryItem::getQuantity).sum();
        totalItemsText.setText("Selected items: " + totalSelected);
        confirmButton.setEnabled(totalSelected > 0);
    }

    private void processWithdrawal() {
        String address = addressInput.getText().toString().trim();

        if (address.isEmpty()) {
            addressInput.setError("Please enter shipping address");
            return;
        }

        if (selectedItems.isEmpty()) {
            Toast.makeText(getContext(), "Please select items to withdraw", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getContext().getSharedPreferences("StickerShopPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Save withdrawal order
            long orderId = dbHelper.saveWithdrawalOrder(userId, address, selectedItems);

            if (orderId != -1) {
                Toast.makeText(getContext(), "Withdrawal order placed successfully! Order ID: #WD" + String.format("%03d", (int)orderId), Toast.LENGTH_LONG).show();

                if (onWithdrawComplete != null) {
                    onWithdrawComplete.run();
                }

                dismiss();
            } else {
                Toast.makeText(getContext(), "Error processing withdrawal", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error processing withdrawal", Toast.LENGTH_SHORT).show();
            android.util.Log.e("WithdrawDialog", "Error processing withdrawal", e);
        }
    }
}