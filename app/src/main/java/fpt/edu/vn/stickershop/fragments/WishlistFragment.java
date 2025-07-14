package fpt.edu.vn.stickershop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.adapters.ProductAdapter;
import fpt.edu.vn.stickershop.database.DatabaseHelper;
import fpt.edu.vn.stickershop.models.Product;

import java.util.ArrayList;
import java.util.List;

public class WishlistFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);

        recyclerView = view.findViewById(R.id.wishlist_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dbHelper = new DatabaseHelper(getContext());

        productAdapter = new ProductAdapter(getWishlist());
        recyclerView.setAdapter(productAdapter);

        return view;
    }

    private List<Product> getWishlist() {
        // TODO: Fetch from wishlist table or simulate for now
        List<Product> products = new ArrayList<>();
        // Example: Add sample product
        products.add(new Product(1, "Cute Cat Sticker", 1.99, "https://example.com/cat_sticker.png", "sticker"));
        return products;
    }
}