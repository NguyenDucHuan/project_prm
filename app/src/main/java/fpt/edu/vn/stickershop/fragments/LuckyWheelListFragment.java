package fpt.edu.vn.stickershop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import fpt.edu.vn.stickershop.R;
import fpt.edu.vn.stickershop.adapters.LuckyWheelListAdapter;
import fpt.edu.vn.stickershop.database.DatabaseHelper;
import fpt.edu.vn.stickershop.models.LuckyWheel;

public class LuckyWheelListFragment extends Fragment implements LuckyWheelListAdapter.OnWheelClickListener {
    private RecyclerView recyclerView;
    private LuckyWheelListAdapter adapter;
    private DatabaseHelper dbHelper;
    private LinearLayout emptyView; // Thay đổi từ TextView thành LinearLayout

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lucky_wheel_list, container, false);

        initViews(view);
        setupRecyclerView();
        loadWheels();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.wheels_recycler_view);
        emptyView = view.findViewById(R.id.empty_view); // Cast thành LinearLayout
        dbHelper = new DatabaseHelper(getContext());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadWheels() {
        List<LuckyWheel> wheels = dbHelper.getAllLuckyWheels();

        if (wheels.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            adapter = new LuckyWheelListAdapter(wheels, this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onWheelClick(LuckyWheel wheel) {
        // Truyền wheel ID qua Bundle
        Bundle bundle = new Bundle();
        bundle.putInt("wheel_id", wheel.getId());

        // Navigate to LuckyBoxFragment với wheel đã chọn
        Navigation.findNavController(requireView())
                .navigate(R.id.action_luckyWheelListFragment_to_luckyBoxFragment, bundle);
    }
}