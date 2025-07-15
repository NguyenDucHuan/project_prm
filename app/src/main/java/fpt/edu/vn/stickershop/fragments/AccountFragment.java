package fpt.edu.vn.stickershop.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.database.DatabaseHelper;

public class AccountFragment extends Fragment {
    private TextView userNameTextView, userEmailTextView;
    private Button logoutButton, myOrdersButton;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        userNameTextView = view.findViewById(R.id.user_name);
        userEmailTextView = view.findViewById(R.id.user_email);
        logoutButton = view.findViewById(R.id.logout_button);
        myOrdersButton = view.findViewById(R.id.my_orders_button);
        dbHelper = new DatabaseHelper(getContext());

        loadUserInfo();

        myOrdersButton.setOnClickListener(v -> {
            SharedPreferences prefs = requireContext().getSharedPreferences("StickerShopPrefs", Context.MODE_PRIVATE);
            int userId = prefs.getInt("user_id", -1);
            
            if (userId != -1) {
                Navigation.findNavController(v).navigate(R.id.action_accountFragment_to_orderTrackingFragment);
            } else {
                Toast.makeText(getContext(), "Please login to view orders", Toast.LENGTH_SHORT).show();
            }
        });

        logoutButton.setOnClickListener(v -> {
            // Clear user session
            SharedPreferences prefs = getContext().getSharedPreferences("StickerShopPrefs", Context.MODE_PRIVATE);
            prefs.edit().clear().apply();
            
            // Navigate back to login and clear back stack
            Navigation.findNavController(v).navigate(R.id.action_accountFragment_to_loginFragment);
        });

        return view;
    }
    
    private void loadUserInfo() {
        try {
            SharedPreferences prefs = getContext().getSharedPreferences("StickerShopPrefs", Context.MODE_PRIVATE);
            int userId = prefs.getInt("user_id", -1);
            Log.d("AccountFragment", "Loading user info for user ID: " + userId);
            
            if (userId != -1) {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.query(DatabaseHelper.TABLE_USERS,
                        new String[]{DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_EMAIL},
                        DatabaseHelper.COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)},
                        null, null, null);
                
                if (cursor.moveToFirst()) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                    String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL));
                    userNameTextView.setText("Name: " + name);
                    userEmailTextView.setText("Email: " + email);
                    Log.d("AccountFragment", "Loaded user info: " + name + " (" + email + ")");
                } else {
                    Log.w("AccountFragment", "No user found for ID: " + userId);
                    userNameTextView.setText("Name: Unknown User");
                    userEmailTextView.setText("Email: unknown@example.com");
                }
                cursor.close();
            } else {
                Log.w("AccountFragment", "No user ID found in preferences");
                userNameTextView.setText("Name: Guest User");
                userEmailTextView.setText("Email: guest@example.com");
                myOrdersButton.setEnabled(false);
            }
        } catch (Exception e) {
            Log.e("AccountFragment", "Error loading user info", e);
            userNameTextView.setText("Name: Error loading user");
            userEmailTextView.setText("Email: error@example.com");
        }
    }
}