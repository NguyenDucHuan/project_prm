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
import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.adapters.CategoryAdapter;
import fpt.edu.vn.stickershop.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private DatabaseHelper dbHelper;
    private TextView emptyTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        recyclerView = view.findViewById(R.id.category_recycler_view);
        emptyTextView = view.findViewById(R.id.empty_text);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dbHelper = new DatabaseHelper(getContext());

        List<String> categories = getCategories();
        Log.d("CategoryFragment", "Found " + categories.size() + " categories");
        
        if (categories.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText("No categories available");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
            categoryAdapter = new CategoryAdapter(categories);
            recyclerView.setAdapter(categoryAdapter);
        }

        return view;
    }

    private List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(true, DatabaseHelper.TABLE_PRODUCTS, 
                    new String[]{DatabaseHelper.COLUMN_PRODUCT_TYPE}, 
                    null, null, DatabaseHelper.COLUMN_PRODUCT_TYPE, null, null, null);
            
            Log.d("CategoryFragment", "Database query returned " + cursor.getCount() + " distinct types");
            
            while (cursor.moveToNext()) {
                String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_TYPE));
                if (type != null && !type.isEmpty()) {
                    categories.add(type);
                    Log.d("CategoryFragment", "Added category: " + type);
                }
            }
            cursor.close();
            
            // If no categories from database, add some default ones
            if (categories.isEmpty()) {
                categories.add("Cute");
                categories.add("Funny");
                categories.add("Anime");
                categories.add("Food");
                categories.add("Animals");
            }
        } catch (Exception e) {
            Log.e("CategoryFragment", "Error loading categories", e);
            // Add default categories if database fails
            categories.add("Cute");
            categories.add("Funny");
            categories.add("Anime");
        }
        return categories;
    }
}