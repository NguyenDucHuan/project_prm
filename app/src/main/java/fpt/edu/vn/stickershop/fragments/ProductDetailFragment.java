package fpt.edu.vn.stickershop.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.database.DatabaseHelper;
import com.squareup.picasso.Picasso;

public class ProductDetailFragment extends Fragment {
    private TextView productNameTextView, productPriceTextView;
    private ImageView productImageView;
    private Button addToCartButton;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        productNameTextView = view.findViewById(R.id.product_name);
        productPriceTextView = view.findViewById(R.id.product_price);
        productImageView = view.findViewById(R.id.product_image);
        addToCartButton = view.findViewById(R.id.add_to_cart_button);
        dbHelper = new DatabaseHelper(getContext());

        // TODO: Get product ID from arguments and fetch from SQLite
        Bundle args = getArguments();
        if (args != null) {
            int productId = args.getInt("product_id", -1);
            loadProductDetails(productId);
        }

        addToCartButton.setOnClickListener(v -> {
            // TODO: Implement add to cart logic
            Toast.makeText(getContext(), "Added to cart", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void loadProductDetails(int productId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCTS,
                null,
                DatabaseHelper.COLUMN_PRODUCT_ID + "=?",
                new String[]{String.valueOf(productId)},
                null, null, null);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE));
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_IMAGE));
            productNameTextView.setText(name);
            productPriceTextView.setText(String.format("$%.2f", price));
            Picasso.get().load(imageUrl).into(productImageView);
        }
        cursor.close();
    }
}