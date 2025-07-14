package fpt.edu.vn.stickershop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import fpt.edu.vn.stickershop.R;

import java.util.List;

public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.PromotionViewHolder> {
    private List<String> promotions;

    public PromotionAdapter(List<String> promotions) {
        this.promotions = promotions;
    }

    static class PromotionViewHolder extends RecyclerView.ViewHolder {
        TextView promotionTextView;

        PromotionViewHolder(View view) {
            super(view);
            promotionTextView = view.findViewById(R.id.promotion_text);
        }
    }

    @Override
    public PromotionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_promotion, parent, false);
        return new PromotionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PromotionViewHolder holder, int position) {
        String promotion = promotions.get(position);
        holder.promotionTextView.setText(promotion);
    }

    @Override
    public int getItemCount() {
        return promotions.size();
    }
}