package ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberme.R;

public class AddBarberShopActivity extends AppCompatActivity {

    RecyclerView picturesList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_barbershop);
        picturesList = findViewById(R.id.recyclerview_pics);
        picturesList.setLayoutManager(new LinearLayoutManager(this));
        final picturesAdapter songAdapter = new SongAdapter(songs);
        picturesList.setAdapter(picturesAdapter);
    }
}
