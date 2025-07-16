package fpt.edu.vn.stickershop.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.adapters.ProductAdapter;
import fpt.edu.vn.stickershop.database.DatabaseHelper;
import fpt.edu.vn.stickershop.dialogs.AddToCartDialog;
import fpt.edu.vn.stickershop.models.Product;

public class SearchFragment extends Fragment implements AddToCartDialog.OnCartUpdatedListener {
    private EditText searchEditText;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private DatabaseHelper dbHelper;
    private TextView emptyTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize views
        searchEditText = view.findViewById(R.id.search_edit_text);
        recyclerView = view.findViewById(R.id.search_recycler_view);
        emptyTextView = view.findViewById(R.id.empty_text_view);

        dbHelper = new DatabaseHelper(getContext());

        // Setup RecyclerView - CHỈ TẠO MỘT LẦN
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        productAdapter = new ProductAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(productAdapter);

        // Setup search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Show empty state initially
        updateUI(new ArrayList<>());

        return view;
    }

    @Override
    public void onCartUpdated() {
        Log.d("SearchFragment", "Cart updated successfully");
    }

    private void searchProducts(String query) {
        List<Product> products;

        if (query.trim().isEmpty()) {
            products = new ArrayList<>();
        } else {
            products = getProductsBySearch(query);
        }

        // SỬA: Chỉ update products, KHÔNG tạo adapter mới
        productAdapter.updateProducts(products);
        updateUI(products);
    }

    private List<Product> getProductsBySearch(String query) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            String selection = DatabaseHelper.COLUMN_PRODUCT_NAME + " LIKE ?";
            String[] selectionArgs = new String[]{"%" + query + "%"};

            cursor = db.query(
                    DatabaseHelper.TABLE_PRODUCTS,
                    null,
                    selection,
                    selectionArgs,
                    null, null, null
            );

            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE));
                String image = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_IMAGE));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_TYPE));

                Product product = new Product(id, name, price, image, type);
                products.add(product);
            }

        } catch (Exception e) {
            Log.e("SearchFragment", "Error searching products", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return products;
    }

    private void updateUI(List<Product> products) {
        if (products.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);

            String query = searchEditText.getText().toString().trim();
            if (query.isEmpty()) {
                emptyTextView.setText("Enter keywords to search for products");
            } else {
                emptyTextView.setText("No products found for \"" + query + "\"");
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
        }
    }
}