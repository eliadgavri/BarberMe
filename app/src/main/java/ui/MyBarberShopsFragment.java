package ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.barberme.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MyBarberShopsFragment extends Fragment {

    MyBarberShopsListener myBarberShopsListener;
    FloatingActionButton addBarberShop;

    interface MyBarberShopsListener {
        void onAddBarberShopClick();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myBarberShopsListener = (MyBarberShopsListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_barbershops, container, false);
        addBarberShop = rootView.findViewById(R.id.add_button);
        addBarberShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myBarberShopsListener != null)
                    myBarberShopsListener.onAddBarberShopClick();
            }
        });
        return rootView;
    }
}
