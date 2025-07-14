package fpt.edu.vn.stickershop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import fpt.edu.vn.stickershop.R;

public class CheckoutFragment extends Fragment {
    private EditText addressEditText;
    private Button confirmButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);

        addressEditText = view.findViewById(R.id.address_input);
        confirmButton = view.findViewById(R.id.confirm_button);

        confirmButton.setOnClickListener(v -> {
            String address = addressEditText.getText().toString();
            if (address.isEmpty()) {
                Toast.makeText(getContext(), "Please enter address", Toast.LENGTH_SHORT).show();
            } else {
                // TODO: Save order to SQLite
                Navigation.findNavController(v).navigate(R.id.action_checkoutFragment_to_orderConfirmationFragment);
            }
        });

        return view;
    }
}