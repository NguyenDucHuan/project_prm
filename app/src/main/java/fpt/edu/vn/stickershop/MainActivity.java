package fpt.edu.vn.stickershop;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import fpt.edu.vn.stickershop.database.DatabaseHelper;
import vn.zalopay.sdk.ZaloPaySDK;

public class MainActivity extends AppCompatActivity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.nav_host_fragment);
//        if (navHostFragment != null) {
//            NavController navController = navHostFragment.getNavController();
//            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
//            NavigationUI.setupWithNavController(bottomNav, navController);
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // GỌI SDK nếu Intent chứa data từ scheme URL (ví dụ: stickershop://app)
        if (getIntent() != null && getIntent().getData() != null) {
            ZaloPaySDK.getInstance().onResult(getIntent());
            Log.d("ZaloPay", "Handled ZaloPay deep link in onCreate()");
        }
        // Initialize database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d("MainActivity", "Database created successfully");

        // Insert initial data
        db.close(); // Đóng database sau khi sử dụng

        // Setup navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            NavigationUI.setupWithNavController(bottomNav, navController);

            // Ẩn/hiện bottomNav tùy theo fragment
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.loginFragment ||
                        destination.getId() == R.id.registerFragment) {
                    bottomNav.setVisibility(View.GONE);
                } else {
                    bottomNav.setVisibility(View.VISIBLE);
                }
            });
        } else {
            Log.e("MainActivity", "NavHostFragment is null");
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ZaloPaySDK.getInstance().onResult(data); // Thực hiện ở Activity
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // cập nhật lại intent chính
        if (intent.getData() != null) {
            ZaloPaySDK.getInstance().onResult(intent);
            Log.d("ZaloPay", "Handled ZaloPay deep link in onNewIntent()");
        }
    }
}