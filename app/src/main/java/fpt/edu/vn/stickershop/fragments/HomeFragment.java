package fpt.edu.vn.stickershop.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.adapters.ProductAdapter;
import fpt.edu.vn.stickershop.database.DatabaseHelper;
import fpt.edu.vn.stickershop.models.Product;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private DatabaseHelper dbHelper;
    private TextView emptyTextView;

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_home, container, false);
//        Log.d("HomeFragment", "onCreateView started");
//
//
//        recyclerView = view.findViewById(R.id.product_recycler_view);
//        emptyTextView = view.findViewById(R.id.empty_text);
//
//        if (getContext() == null) {
//            Log.e("HomeFragment", "Context is null");
//            return view;
//        }
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        dbHelper = new DatabaseHelper(getContext());
//
//        List<Product> products = getProducts();
//        Log.d("HomeFragment", "Found " + products.size() + " products");
//
//        if (products.isEmpty()) {
//            recyclerView.setVisibility(View.GONE);
//            emptyTextView.setVisibility(View.VISIBLE);
//            emptyTextView.setText("No products available");
//        } else {
//            recyclerView.setVisibility(View.VISIBLE);
//            emptyTextView.setVisibility(View.GONE);
//            productAdapter = new ProductAdapter(products);
//            recyclerView.setAdapter(productAdapter);
//        }
//
//        return view;
//    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Log.d("HomeFragment", "onCreateView started");

        // Remove duplicate initialization
        recyclerView = view.findViewById(R.id.product_recycler_view);
        emptyTextView = view.findViewById(R.id.empty_text);

        if (getContext() == null) {
            Log.e("HomeFragment", "Context is null");
            return view;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dbHelper = new DatabaseHelper(getContext());

        List<Product> products = getProducts();
        updateUI(products); // Use the existing updateUI method

        return view;
    }
//    private List<Product> getProducts() {
//        List<Product> products = new ArrayList<>();
//        try {
//            SQLiteDatabase db = dbHelper.getReadableDatabase();
//            Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCTS, null, null, null, null, null, null);
//            Log.d("HomeFragment", "Database query returned " + cursor.getCount() + " rows");
//
//            while (cursor.moveToNext()) {
//                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_ID));
//                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME));
//                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE));
//                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_IMAGE));
//                String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_TYPE));
//                products.add(new Product(id, name, price, imageUrl, type));
//                Log.d("HomeFragment", "Added product: " + name);
//            }
//            cursor.close();
//        } catch (Exception e) {
//            Log.e("HomeFragment", "Error loading products", e);
//        }
//        return products;
//    }
private List<Product> getProducts() {
    List<Product> products = new ArrayList<>();
    SQLiteDatabase db = null;
    Cursor cursor = null;

    try {
        db = dbHelper.getReadableDatabase();
        Log.d("HomeFragment", "Database opened successfully");

        // Kiểm tra số lượng records trong bảng
        Cursor countCursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_PRODUCTS, null);
        countCursor.moveToFirst();
        int count = countCursor.getInt(0);
        countCursor.close();
        Log.d("HomeFragment", "Total products in database: " + count);

        cursor = db.query(DatabaseHelper.TABLE_PRODUCTS, null, null, null, null, null, null);
        Log.d("HomeFragment", "Query executed, found " + cursor.getCount() + " products");

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE));
            String image = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_IMAGE));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_TYPE));

            Product product = new Product(cursor.getPosition(), name, price, image, type);
            products.add(product);
            Log.d("HomeFragment", "Loaded product: " + name);
        }
    } catch (Exception e) {
        Log.e("HomeFragment", "Error loading products", e);
        e.printStackTrace();
    } finally {
        if (cursor != null) cursor.close();
        if (db != null) db.close();
    }

    Log.d("HomeFragment", "Returning " + products.size() + " products");
    return products;
}
    private void updateUI(List<Product> products) {
        if (products.isEmpty()) {
            Log.d("HomeFragment", "No products found, showing empty state");
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText("No products available");
        } else {
            Log.d("HomeFragment", "Products found, showing list");
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
            productAdapter = new ProductAdapter(products);
            recyclerView.setAdapter(productAdapter);
        }
    }
}