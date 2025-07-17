package fpt.edu.vn.stickershop.fragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import androidx.navigation.Navigation;
import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.adapters.SpinResultAdapter;
import fpt.edu.vn.stickershop.database.DatabaseHelper;
import fpt.edu.vn.stickershop.models.LuckyWheel;
import fpt.edu.vn.stickershop.models.SpinResult;
import fpt.edu.vn.stickershop.models.WheelItem;

public class LuckyBoxFragment extends Fragment {
    private TextView wheelNameTextView, wheelDescriptionTextView, totalCostTextView;
    private ImageView wheelImageView;
    private RadioGroup spinCountRadioGroup;
    private Button payAndSpinButton, saveToInventoryButton;
    private LinearLayout resultsLayout;
    private RecyclerView resultsRecyclerView;
    private ImageButton backButton;
    private DatabaseHelper dbHelper;
    private LuckyWheel currentWheel;
    private SpinResult lastSpinResult;
    private SpinResultAdapter resultAdapter;
    private int selectedSpinCount = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lucky_wheel, container, false);

        initViews(view);
        dbHelper = new DatabaseHelper(getContext());
        loadLuckyWheel();
        setupSpinCountSelection();
        setupBackButton();
        return view;
    }

    private void initViews(View view) {
        wheelNameTextView = view.findViewById(R.id.wheel_name);
        wheelDescriptionTextView = view.findViewById(R.id.wheel_description);
        wheelImageView = view.findViewById(R.id.wheel_image);
        spinCountRadioGroup = view.findViewById(R.id.spin_count_group);
        totalCostTextView = view.findViewById(R.id.total_cost);
        payAndSpinButton = view.findViewById(R.id.pay_and_spin_button);
        resultsLayout = view.findViewById(R.id.results_layout);
        resultsRecyclerView = view.findViewById(R.id.results_recycler_view);
        saveToInventoryButton = view.findViewById(R.id.save_to_inventory_button);

        // Initialize back button
        backButton = view.findViewById(R.id.back_button);

        // Setup RecyclerView
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initially hide results
        resultsLayout.setVisibility(View.GONE);
    }
    @Override
    public void onResume() {
        super.onResume();

        // Handle system back button
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                android.util.Log.d("LuckyBoxFragment", "System back pressed - navigating to wheel list");

                try {
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_luckyBoxFragment_to_luckyWheelListFragment);
                } catch (Exception e) {
                    // If navigation fails, use default back behavior
                    setEnabled(false);
                    requireActivity().onBackPressed();
                }
            }
        });
    }

    private void setupBackButton() {
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                android.util.Log.d("LuckyBoxFragment", "Back button clicked - navigating to wheel list");

                try {
                    // Navigate back to Lucky Wheel List
                    Navigation.findNavController(v)
                            .navigate(R.id.action_luckyBoxFragment_to_luckyWheelListFragment);
                } catch (Exception e) {
                    android.util.Log.e("LuckyBoxFragment", "Error navigating back", e);

                    // Fallback: use system back
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                }
            });
        } else {
            android.util.Log.e("LuckyBoxFragment", "Back button not found in layout!");
        }
    }

    private void loadLuckyWheel() {
        // Kiểm tra nếu có wheel ID được truyền từ Bundle
        Bundle args = getArguments();
        int wheelId = -1;

        if (args != null) {
            wheelId = args.getInt("wheel_id", -1);
        }

        if (wheelId != -1) {
            // Load specific wheel
            currentWheel = dbHelper.getLuckyWheelById(wheelId);
        } else {
            // Load first available wheel (fallback)
            List<LuckyWheel> wheels = dbHelper.getAllLuckyWheels();
            if (!wheels.isEmpty()) {
                currentWheel = wheels.get(0);
            }
        }

        if (currentWheel != null) {
            wheelNameTextView.setText(currentWheel.getName());
            wheelDescriptionTextView.setText(currentWheel.getDescription());
            updateTotalCost();
        }
    }


   private void setupSpinCountSelection() {
    android.util.Log.d("LuckyBoxFragment", "Setting up spin count selection");
    
    spinCountRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
        RadioButton selectedRadio = group.findViewById(checkedId);
        if (selectedRadio != null) {
            String text = selectedRadio.getText().toString();
            selectedSpinCount = Integer.parseInt(text.replace("x", ""));
            updateTotalCost();
            android.util.Log.d("LuckyBoxFragment", "Selected spin count: " + selectedSpinCount);
        }
    });

    payAndSpinButton.setOnClickListener(v -> {
        android.util.Log.d("LuckyBoxFragment", "Pay and Spin button clicked");
        if (currentWheel != null) {
            showPaymentConfirmation();
        } else {
            android.util.Log.e("LuckyBoxFragment", "currentWheel is null!");
        }
    });

    // Đảm bảo save button được setup đúng cách
    if (saveToInventoryButton != null) {
        saveToInventoryButton.setOnClickListener(v -> {
            android.util.Log.d("LuckyBoxFragment", "=== SAVE TO INVENTORY BUTTON CLICKED ===");
            
            if (lastSpinResult != null) {
                android.util.Log.d("LuckyBoxFragment", "lastSpinResult exists, proceeding to save");
                saveResultsToInventory();
            } else {
                android.util.Log.e("LuckyBoxFragment", "lastSpinResult is NULL - cannot save!");
                Toast.makeText(getContext(), "No spin results to save!", Toast.LENGTH_SHORT).show();
            }
        });
        android.util.Log.d("LuckyBoxFragment", "Save to inventory button listener set");
    } else {
        android.util.Log.e("LuckyBoxFragment", "saveToInventoryButton is NULL!");
    }
}
    private void updateTotalCost() {
        if (currentWheel != null) {
            double totalCost = currentWheel.getCost() * selectedSpinCount;
            totalCostTextView.setText(String.format("Total: $%.2f", totalCost));
        }
    }

    private void showPaymentConfirmation() {
        double totalCost = currentWheel.getCost() * selectedSpinCount;

        new AlertDialog.Builder(getContext())
                .setTitle("Confirm Payment")
                .setMessage(String.format("Pay $%.2f to spin %dx?", totalCost, selectedSpinCount))
                .setPositiveButton("Pay & Spin", (dialog, which) -> {
                    // Simulate payment processing
                    processPaymentAndSpin();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void processPaymentAndSpin() {
        // Simulate payment processing
        Toast.makeText(getContext(), "Payment processed successfully!", Toast.LENGTH_SHORT).show();

        // Start spinning animation
        animateWheelSpin();

        // Perform spins
        performSpins();
    }

    private void animateWheelSpin() {
        if (wheelImageView != null) {
            // Create rotation animation
            float finalRotation = 360f * 3 + (new Random().nextFloat() * 360f); // 3 full rotations + random

            ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(wheelImageView, "rotation", 0f, finalRotation);
            rotationAnimator.setDuration(3000); // 3 seconds
            rotationAnimator.setInterpolator(new DecelerateInterpolator());

            rotationAnimator.addUpdateListener(animation -> {
                // Optional: Add sound effects or other visual feedback
            });

            rotationAnimator.start();
        }
    }

    private void performSpins() {
        android.util.Log.d("LuckyBoxFragment", "=== STARTING SPINS ===");
        android.util.Log.d("LuckyBoxFragment", "Spin count: " + selectedSpinCount);
        android.util.Log.d("LuckyBoxFragment", "Current wheel: " + (currentWheel != null ? currentWheel.getName() : "NULL"));

        List<WheelItem> wonItems = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < selectedSpinCount; i++) {
            WheelItem wonItem = spinWheel(random);
            if (wonItem != null) {
                wonItems.add(wonItem);
                android.util.Log.d("LuckyBoxFragment",
                        String.format("Spin %d result: %s (ProductID: %d, Quantity: %d)",
                                i+1, wonItem.getProductName(), wonItem.getProductId(), wonItem.getQuantity()));
            } else {
                android.util.Log.e("LuckyBoxFragment", "Spin " + (i+1) + " returned null!");
            }
        }

        double totalCost = currentWheel.getCost() * selectedSpinCount;
        lastSpinResult = new SpinResult(wonItems, totalCost, selectedSpinCount);

        android.util.Log.d("LuckyBoxFragment", "=== SPIN COMPLETED ===");
        android.util.Log.d("LuckyBoxFragment", "Total items won: " + wonItems.size());
        android.util.Log.d("LuckyBoxFragment", "lastSpinResult created: " + (lastSpinResult != null));

        // TỰ ĐỘNG LƯU VÀO INVENTORY NGAY SAU KHI SPIN
        autoSaveToInventory();

        // Show results after animation
        wheelImageView.postDelayed(() -> {
            android.util.Log.d("LuckyBoxFragment", "Showing spin results after animation");
            showSpinResults();
        }, 3200);
    }

    private void autoSaveToInventory() {
        android.util.Log.d("LuckyBoxFragment", "=== AUTO SAVING TO INVENTORY ===");

        if (lastSpinResult == null || lastSpinResult.getWonItems().isEmpty()) {
            android.util.Log.e("LuckyBoxFragment", "No items to auto-save!");
            return;
        }

        int currentUserId = getCurrentUserId();
        android.util.Log.d("LuckyBoxFragment", "Auto-saving for User ID: " + currentUserId);
        android.util.Log.d("LuckyBoxFragment", "Items to auto-save: " + lastSpinResult.getWonItems().size());

        boolean allSaved = true;
        int savedCount = 0;

        for (WheelItem item : lastSpinResult.getWonItems()) {
            android.util.Log.d("LuckyBoxFragment",
                    String.format("Auto-saving - ProductID: %d, Quantity: %d, Name: %s",
                            item.getProductId(), item.getQuantity(), item.getProductName()));

            boolean saved = dbHelper.addToInventory(currentUserId, item.getProductId(), item.getQuantity());

            android.util.Log.d("LuckyBoxFragment", "Auto-save result for item " + item.getProductId() + ": " + saved);

            if (saved) {
                savedCount++;
            } else {
                allSaved = false;
            }
        }

        android.util.Log.d("LuckyBoxFragment", String.format("Auto-save summary - Total: %d, Saved: %d, All saved: %b",
                lastSpinResult.getWonItems().size(), savedCount, allSaved));

        // Hiển thị thông báo kết quả
        if (allSaved && savedCount > 0) {
            android.util.Log.d("LuckyBoxFragment", "All items auto-saved successfully!");
        } else {
            android.util.Log.w("LuckyBoxFragment", String.format("Auto-saved %d/%d items", savedCount, lastSpinResult.getWonItems().size()));
        }
    }

    private WheelItem spinWheel(Random random) {
        List<WheelItem> items = currentWheel.getItems();
        double totalProbability = 0;

        // Calculate total probability
        for (WheelItem item : items) {
            totalProbability += item.getProbability();
        }

        // Generate random number
        double randomValue = random.nextDouble() * totalProbability;
        double currentProbability = 0;

        // Find winning item
        for (WheelItem item : items) {
            currentProbability += item.getProbability();
            if (randomValue <= currentProbability) {
                return item;
            }
        }

        // Fallback to first item
        return items.isEmpty() ? null : items.get(0);
    }

    private void showSpinResults() {
        android.util.Log.d("LuckyBoxFragment", "=== SHOWING SPIN RESULTS ===");

        if (lastSpinResult == null || lastSpinResult.getWonItems().isEmpty()) {
            android.util.Log.e("LuckyBoxFragment", "No items to show!");
            return;
        }

        // Show results
        resultsLayout.setVisibility(View.VISIBLE);
        resultAdapter = new SpinResultAdapter(lastSpinResult.getWonItems());
        resultsRecyclerView.setAdapter(resultAdapter);

        // Ẩn save button
        if (saveToInventoryButton != null) {
            saveToInventoryButton.setVisibility(View.GONE);
        }

        // Hiển thị thông báo
        Toast.makeText(getContext(),
                String.format("🎉 Won %d items! Automatically saved to inventory!",
                        lastSpinResult.getWonItems().size()),
                Toast.LENGTH_LONG).show();

        // TỰ ĐỘNG ẨN RESULTS SAU 5 GIÂY
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (resultsLayout != null) {
                resultsLayout.setVisibility(View.GONE);
                lastSpinResult = null;
                Toast.makeText(getContext(), "Check your inventory to see the new items!", Toast.LENGTH_SHORT).show();
            }
        }, 5000); // 5 seconds
    }

    private void addViewInventoryButton() {
        // Tạo button View Inventory động
        Button viewInventoryButton = new Button(getContext());
        viewInventoryButton.setText("View My Inventory");
        viewInventoryButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        viewInventoryButton.setTextColor(getResources().getColor(android.R.color.white));

        viewInventoryButton.setOnClickListener(v -> {
            android.util.Log.d("LuckyBoxFragment", "View Inventory button clicked");

            // Ẩn results layout
            resultsLayout.setVisibility(View.GONE);
            lastSpinResult = null;

            // Navigate to inventory
            try {
                Navigation.findNavController(requireView()).navigate(R.id.action_luckyBoxFragment_to_inventoryFragment);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Please check your inventory manually.", Toast.LENGTH_SHORT).show();
                android.util.Log.e("LuckyBoxFragment", "Error navigating to inventory", e);
            }
        });

        // Add button to results layout
        if (resultsLayout instanceof LinearLayout) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 32, 0, 0);
            viewInventoryButton.setLayoutParams(params);

            ((LinearLayout) resultsLayout).addView(viewInventoryButton);
        }
    }

    private void saveResultsToInventory() {
        android.util.Log.d("LuckyBoxFragment", "=== SAVING RESULTS TO INVENTORY ===");

        if (lastSpinResult == null) {
            android.util.Log.e("LuckyBoxFragment", "lastSpinResult is null!");
            Toast.makeText(getContext(), "No items to save!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (lastSpinResult.getWonItems().isEmpty()) {
            android.util.Log.e("LuckyBoxFragment", "Won items list is empty!");
            Toast.makeText(getContext(), "No items to save!", Toast.LENGTH_SHORT).show();
            return;
        }

        int currentUserId = getCurrentUserId();
        android.util.Log.d("LuckyBoxFragment", "Current User ID: " + currentUserId);
        android.util.Log.d("LuckyBoxFragment", "Items to save: " + lastSpinResult.getWonItems().size());

        boolean allSaved = true;
        int savedCount = 0;

        for (WheelItem item : lastSpinResult.getWonItems()) {
            android.util.Log.d("LuckyBoxFragment",
                    String.format("Attempting to save - ProductID: %d, Quantity: %d, Name: %s",
                            item.getProductId(), item.getQuantity(), item.getProductName()));

            boolean saved = dbHelper.addToInventory(currentUserId, item.getProductId(), item.getQuantity());

            android.util.Log.d("LuckyBoxFragment", "Save result for item " + item.getProductId() + ": " + saved);

            if (saved) {
                savedCount++;
            } else {
                allSaved = false;
            }
        }

        android.util.Log.d("LuckyBoxFragment", String.format("Save summary - Total: %d, Saved: %d, All saved: %b",
                lastSpinResult.getWonItems().size(), savedCount, allSaved));

        if (allSaved && savedCount > 0) {
            Toast.makeText(getContext(),
                    String.format("All %d items saved to inventory!", savedCount),
                    Toast.LENGTH_LONG).show();
            resultsLayout.setVisibility(View.GONE);
            lastSpinResult = null;

            // Navigate to inventory
            try {
                Navigation.findNavController(requireView()).navigate(R.id.action_luckyBoxFragment_to_inventoryFragment);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Items saved! Check your inventory.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(),
                    String.format("Saved %d/%d items. Please try again for failed items.",
                            savedCount, lastSpinResult.getWonItems().size()),
                    Toast.LENGTH_LONG).show();
        }
    }

    private int getCurrentUserId() {
        // Kiểm tra SharedPreferences trước
        if (getContext() != null) {
            android.content.SharedPreferences sharedPref = getContext().getSharedPreferences("StickerShopPrefs", android.content.Context.MODE_PRIVATE);
            int userId = sharedPref.getInt("user_id", -1);

            android.util.Log.d("LuckyBoxFragment", "Current User ID from SharedPrefs: " + userId);

            if (userId != -1) {
                return userId;
            }
        }

        // Fallback to default user ID
        android.util.Log.d("LuckyBoxFragment", "Using default User ID: 1");
        return 1;
    }
}