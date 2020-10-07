package ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberme.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import adapter.BarberShopAdapter;
import model.Consumer;
import model.DatabaseFetch;
import userData.BarberShop;

public class MyBarberShopsFragment extends Fragment implements BarberShopAdapter.MyBarberShopListener{

    FloatingActionButton addBarberShop;
    RecyclerView myBarbersList;
    List<BarberShop> barbers;

    @Override
    public void onBarberShopClick(int position, View view) {
        Intent intent = new Intent(MyBarberShopsFragment.this.getContext(),BarberShopActivity.class);
        intent.putExtra("Barbershop",barbers.get(position));
        startActivity(intent);
    }

    @Override
    public void onEditBarberShopClick(int position, View view) {
        Intent intent = new Intent(MyBarberShopsFragment.this.getContext(),EditBarberShopActivity.class);
        intent.putExtra("Barbershop",barbers.get(position));
        startActivity(intent);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_barbershops, container, false);
        addBarberShop = rootView.findViewById(R.id.add_button);
        addBarberShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyBarberShopsFragment.this.getContext(), AddBarberShopActivity.class);
                startActivity(intent);
            }
        });
        myBarbersList = rootView.findViewById(R.id.my_barbershops_recycler);
        myBarbersList.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        Consumer<List<BarberShop>> consumerList = new Consumer<List<BarberShop>>() {
            @Override
            public void apply(List<BarberShop> param) {
                BarberShopAdapter barberShopAdapter = new BarberShopAdapter(param, true);
                barbers = param;
                myBarbersList.setAdapter(barberShopAdapter);
                barberShopAdapter.setListener(MyBarberShopsFragment.this);
                //barberShopAdapter.notifyDataSetChanged();
            }
        };
        DatabaseFetch databaseFetch = new DatabaseFetch();
        databaseFetch.fetchUserBarberShops(consumerList, FirebaseAuth.getInstance().getUid());
        return rootView;
    }
}
