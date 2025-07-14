package fpt.edu.vn.stickershop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.database.DatabaseHelper;

public class RegisterFragment extends Fragment {
    private EditText emailEditText, passwordEditText, nameEditText;
    private Button registerButton;
    private TextView loginTextView;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        emailEditText = view.findViewById(R.id.email_input);
        passwordEditText = view.findViewById(R.id.password_input);
        nameEditText = view.findViewById(R.id.name_input);
        registerButton = view.findViewById(R.id.register_button);
        loginTextView = view.findViewById(R.id.login_text);
        dbHelper = new DatabaseHelper(getContext());

        registerButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String name = nameEditText.getText().toString();

            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (dbHelper.addUser(email, password, name)) {
                Toast.makeText(getContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigate(R.id.action_registerFragment_to_loginFragment);
            } else {
                Toast.makeText(getContext(), "Email already exists", Toast.LENGTH_SHORT).show();
            }
        });

        loginTextView.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_registerFragment_to_loginFragment);
        });

        return view;
    }
}