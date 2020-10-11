package ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    ImageButton searchBT;
    BarberShopAdapter barberShopAdapter;
    DatabaseFetch databaseFetch = new DatabaseFetch();
    List<BarberShop> barbers;
    SwipeRefreshLayout refreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_barbershops, container, false);
        barbersList = rootView.findViewById(R.id.barbers_list);
        searchLayout = rootView.findViewById(R.id.search_layout);
        searchTitle = rootView.findViewById(R.id.search_title);
        searchBT = rootView.findViewById(R.id.search_bt);
        refreshLayout = rootView.findViewById(R.id.pullToRefresh);
        barbersList.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDatabase();
                refreshLayout.setRefreshing(false);
            }
        });
        searchBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchString = searchTitle.getText().toString().toLowerCase();
                List<BarberShop> data = new ArrayList<>();
                for(BarberShop barber : barbers) {
                    if (barber.getName().toLowerCase().contains(searchString))
                        data.add(barber);
                }
                barberShopAdapter = new BarberShopAdapter(data, false);
                barbersList.setAdapter(barberShopAdapter);
                barberShopAdapter.setListener(AllBarberShopsFragment.this);
            }
        });

        searchTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchString = searchTitle.getText().toString().toLowerCase();
                List<BarberShop> data = new ArrayList<>();
                for(BarberShop barber : barbers) {
                    if (barber.getName().toLowerCase().contains(searchString))
                        data.add(barber);
                }
                barberShopAdapter = new BarberShopAdapter(data, false);
                barbersList.setAdapter(barberShopAdapter);
                barberShopAdapter.setListener(AllBarberShopsFragment.this);
            }

            @Override
            public void afterTextChanged(Editable editable) {

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

    @Override
    public void onStart() {
        super.onStart();
        refreshDatabase();
    }

    private void refreshDatabase()
    {
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
    }
}
