package fpt.edu.vn.stickershop.fragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
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

        // Setup RecyclerView
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initially hide results
        resultsLayout.setVisibility(View.GONE);
    }

    private void loadLuckyWheel() {
        List<LuckyWheel> wheels = dbHelper.getAllLuckyWheels();
        if (!wheels.isEmpty()) {
            currentWheel = wheels.get(0); // Get first wheel
            wheelNameTextView.setText(currentWheel.getName());
            wheelDescriptionTextView.setText(currentWheel.getDescription());
            updateTotalCost();
        }
    }

    private void setupSpinCountSelection() {
        spinCountRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadio = group.findViewById(checkedId);
            if (selectedRadio != null) {
                String text = selectedRadio.getText().toString();
                selectedSpinCount = Integer.parseInt(text.replace("x", ""));
                updateTotalCost();
            }
        });

        payAndSpinButton.setOnClickListener(v -> {
            if (currentWheel != null) {
                showPaymentConfirmation();
            }
        });

        saveToInventoryButton.setOnClickListener(v -> {
            if (lastSpinResult != null) {
                saveResultsToInventory();
            }
        });
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
        List<WheelItem> wonItems = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < selectedSpinCount; i++) {
            WheelItem wonItem = spinWheel(random);
            if (wonItem != null) {
                wonItems.add(wonItem);
            }
        }

        double totalCost = currentWheel.getCost() * selectedSpinCount;
        lastSpinResult = new SpinResult(wonItems, totalCost, selectedSpinCount);

        // Show results after animation completes
        wheelImageView.postDelayed(() -> showSpinResults(), 3200);
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
        if (lastSpinResult != null && !lastSpinResult.getWonItems().isEmpty()) {
            resultsLayout.setVisibility(View.VISIBLE);

            // Setup adapter
            resultAdapter = new SpinResultAdapter(lastSpinResult.getWonItems());
            resultsRecyclerView.setAdapter(resultAdapter);

            Toast.makeText(getContext(),
                    String.format("Congratulations! You won %d items!", lastSpinResult.getWonItems().size()),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "No items won this time. Try again!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveResultsToInventory() {
        if (lastSpinResult == null || lastSpinResult.getWonItems().isEmpty()) {
            Toast.makeText(getContext(), "No items to save!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current user ID (you might need to implement user session management)
        int currentUserId = getCurrentUserId();

        boolean allSaved = true;
        for (WheelItem item : lastSpinResult.getWonItems()) {
            boolean saved = dbHelper.addToInventory(currentUserId, item.getProductId(), item.getQuantity());
            if (!saved) {
                allSaved = false;
            }
        }

        if (allSaved) {
            Toast.makeText(getContext(), "All items saved to inventory!", Toast.LENGTH_SHORT).show();
            resultsLayout.setVisibility(View.GONE);
            lastSpinResult = null;

            // Navigate to inventory to show saved items
            try {
                Navigation.findNavController(requireView()).navigate(R.id.action_luckyBoxFragment_to_inventoryFragment);
            } catch (Exception e) {
                // If navigation fails, just show success message
                Toast.makeText(getContext(), "Items saved! Check your inventory.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Error saving some items. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private int getCurrentUserId() {
        // Implement user session management
        // For now, return a default user ID
        return 1;
    }
}