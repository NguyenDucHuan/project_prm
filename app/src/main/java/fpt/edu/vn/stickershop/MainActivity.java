package fpt.edu.vn.stickershop;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import fpt.edu.vn.stickershop.database.DatabaseHelper;

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

        // Initialize database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d("MainActivity", "Database created successfully");

        // Insert initial data
        insertInitialData(db); // Thêm dòng này để gọi phương thức
        db.close(); // Đóng database sau khi sử dụng

        // Setup navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            NavigationUI.setupWithNavController(bottomNav, navController);
        } else {
            Log.e("MainActivity", "NavHostFragment is null");
        }
    }

    private void insertInitialData(SQLiteDatabase db) {
        try {
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_PRODUCT_NAME, "Test Product");
            values.put(DatabaseHelper.COLUMN_PRODUCT_PRICE, 9.99);
            values.put(DatabaseHelper.COLUMN_PRODUCT_IMAGE, "image_url");
            values.put(DatabaseHelper.COLUMN_PRODUCT_TYPE, "sticker");

            long result = db.insert(DatabaseHelper.TABLE_PRODUCTS, null, values);
            Log.d("MainActivity", "Inserted initial product with result: " + result);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("MainActivity", "Error inserting initial data", e);
        } finally {
            db.endTransaction();
        }
    }

}