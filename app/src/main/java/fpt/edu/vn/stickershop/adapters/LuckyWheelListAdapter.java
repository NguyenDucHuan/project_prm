package fpt.edu.vn.stickershop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.models.LuckyWheel;

public class LuckyWheelListAdapter extends RecyclerView.Adapter<LuckyWheelListAdapter.WheelViewHolder> {
    private List<LuckyWheel> wheelList;
    private OnWheelClickListener onWheelClickListener;

    public interface OnWheelClickListener {
        void onWheelClick(LuckyWheel wheel);
    }

    public LuckyWheelListAdapter(List<LuckyWheel> wheelList, OnWheelClickListener listener) {
        this.wheelList = wheelList;
        this.onWheelClickListener = listener;
    }

    @NonNull
    @Override
    public WheelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lucky_wheel, parent, false);
        return new WheelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WheelViewHolder holder, int position) {
        LuckyWheel wheel = wheelList.get(position);
        holder.bind(wheel);
    }

    @Override
    public int getItemCount() {
        return wheelList.size();
    }

    class WheelViewHolder extends RecyclerView.ViewHolder {
        private TextView wheelName, wheelDescription, wheelCost;
        private ImageView wheelImage;

        public WheelViewHolder(@NonNull View itemView) {
            super(itemView);
            wheelName = itemView.findViewById(R.id.wheel_name);
            wheelDescription = itemView.findViewById(R.id.wheel_description);
            wheelCost = itemView.findViewById(R.id.wheel_cost);
            wheelImage = itemView.findViewById(R.id.wheel_image);

            itemView.setOnClickListener(v -> {
                if (onWheelClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onWheelClickListener.onWheelClick(wheelList.get(position));
                    }
                }
            });
        }

        public void bind(LuckyWheel wheel) {
            wheelName.setText(wheel.getName());
            wheelDescription.setText(wheel.getDescription());
            wheelCost.setText(String.format("Cost: $%.2f per spin", wheel.getCost()));

            // Set wheel image using existing resource
            wheelImage.setImageResource(R.drawable.ic_lucky_wheel);
            // Note: tint is set in XML using app:tint
        }
    }
}