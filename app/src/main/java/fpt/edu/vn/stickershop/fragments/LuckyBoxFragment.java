package fpt.edu.vn.stickershop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.database.DatabaseHelper;
import fpt.edu.vn.stickershop.LuckyBoxLogic;

public class LuckyBoxFragment extends Fragment {
    private TextView boxNameTextView;
    private Button openBoxButton;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lucky_box, container, false);

        boxNameTextView = view.findViewById(R.id.box_name);
        openBoxButton = view.findViewById(R.id.open_box_button);
        dbHelper = new DatabaseHelper(getContext());

        boxNameTextView.setText("Mystery Box");

        openBoxButton.setOnClickListener(v -> {
            // TODO: Fetch items from lucky_box table
            String itemsJson = "[\"Cute Cat Sticker\",\"Funny Dog Sticker\",\"Rare Unicorn Sticker\"]";
            String result = LuckyBoxLogic.openLuckyBox(itemsJson);
            Toast.makeText(getContext(), "You got: " + result, Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}