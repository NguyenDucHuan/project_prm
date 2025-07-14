package fpt.edu.vn.stickershop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.adapters.PromotionAdapter;

import java.util.ArrayList;
import java.util.List;

public class PromotionFragment extends Fragment {
    private RecyclerView recyclerView;
    private PromotionAdapter promotionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promotion, container, false);

        recyclerView = view.findViewById(R.id.promotion_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        promotionAdapter = new PromotionAdapter(getPromotions());
        recyclerView.setAdapter(promotionAdapter);

        return view;
    }

    private List<String> getPromotions() {
        // Static promotions for now
        List<String> promotions = new ArrayList<>();
        promotions.add("10% Off on First Purchase");
        promotions.add("Buy 2 Get 1 Free");
        promotions.add("Free Shipping on Orders Over $20");
        return promotions;
    }
}