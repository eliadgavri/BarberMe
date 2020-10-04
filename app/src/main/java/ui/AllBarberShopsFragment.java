package ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberme.R;

import java.util.List;

import adapter.BarberShopAdapter;
import adapter.PictureAdapter;
import model.Consumer;
import model.DatabaseFetch;
import userData.BarberShop;

public class AllBarberShopsFragment extends Fragment {

    RecyclerView barbersList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_barbershops, container, false);
        barbersList = rootView.findViewById(R.id.barbers_list);
        barbersList.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        Consumer<List<BarberShop>> consumerList = new Consumer<List<BarberShop>>() {
            @Override
            public void apply(List<BarberShop> param) {
                BarberShopAdapter barberShopAdapter = new BarberShopAdapter(param);
                barbersList.setAdapter(barberShopAdapter);
                //barberShopAdapter.notifyDataSetChanged();
            }
        };
        DatabaseFetch databaseFetch = new DatabaseFetch();
        databaseFetch.fetchAllBarberShops(consumerList);
        return rootView;
    }
}
