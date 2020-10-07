package ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberme.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import adapter.BarberShopAdapter;
import adapter.PictureAdapter;
import model.Consumer;
import model.DatabaseFetch;
import userData.BarberShop;

public class AllBarberShopsFragment extends Fragment implements BarberShopAdapter.MyBarberShopListener {

    RecyclerView barbersList;
    LinearLayout searchLayout;
    EditText searchTitle;
    Button searchBT;
    Button resetBT;
    BarberShopAdapter barberShopAdapter;
    DatabaseFetch databaseFetch = new DatabaseFetch();
    List<BarberShop> barbers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_barbershops, container, false);
        barbersList = rootView.findViewById(R.id.barbers_list);
        searchLayout = rootView.findViewById(R.id.search_layout);
        searchTitle = rootView.findViewById(R.id.search_title);
        searchBT = rootView.findViewById(R.id.search_bt);
        resetBT = rootView.findViewById(R.id.reset_bt);
        barbersList.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        Consumer<List<BarberShop>> consumerList = new Consumer<List<BarberShop>>() {
            @Override
            public void apply(List<BarberShop> param) {
                barbers = param;
                barberShopAdapter = new BarberShopAdapter(param, false);
                barbersList.setAdapter(barberShopAdapter);
                barberShopAdapter.setListener(AllBarberShopsFragment.this);
            }
        };
        databaseFetch.fetchAllBarberShops(consumerList);
        searchBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchString = searchTitle.getText().toString();
                List<BarberShop> data = new ArrayList<>();
                for(BarberShop barber : barbers) {
                    if (barber.getName().contains(searchString))
                        data.add(barber);
                }
                barberShopAdapter = new BarberShopAdapter(data, false);
                barbersList.setAdapter(barberShopAdapter);
                barberShopAdapter.setListener(AllBarberShopsFragment.this);
            }
        });
        resetBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barberShopAdapter = new BarberShopAdapter(barbers, false);
                barbersList.setAdapter(barberShopAdapter);
                barberShopAdapter.setListener(AllBarberShopsFragment.this);
            }
        });

        return rootView;
    }

    public void showHideSearch(boolean b)
    {
        searchLayout.setVisibility(b? View.VISIBLE : View.GONE);
    }

    @Override
    public void onBarberShopClick(int position, View view) {
        Intent intent = new Intent(AllBarberShopsFragment.this.getContext(),BarberShopActivity.class);
        intent.putExtra("Barbershop",barbers.get(position));
        startActivity(intent);
    }

    @Override
    public void onEditBarberShopClick(int position, View view) {

    }
}
