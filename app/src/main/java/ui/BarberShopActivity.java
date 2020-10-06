package ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.barberme.R;

import java.util.ArrayList;

import adapter.BarberShopAdapter;
import adapter.PictureAdapter;
import userData.BarberShop;

public class BarberShopActivity extends AppCompatActivity {

    BarberShop barberShop;
    ImageView barberPicture;
    ArrayList<Uri> pictures;
    RecyclerView picturesRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_shop);
        barberPicture = findViewById(R.id.barbershop_activity_image_view);
        picturesRecycler = findViewById(R.id.recycler_barber_pictures);
        barberShop=(BarberShop) getIntent().getSerializableExtra("Barbershop");
        Glide.with(this).load(barberShop.getImages().get(0)).into(barberPicture);
        pictures = new ArrayList<>();
        for(String url : barberShop.getImages())
            pictures.add(Uri.parse(url));
        picturesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        PictureAdapter pictureAdapter = new PictureAdapter(pictures);
        picturesRecycler.setAdapter(pictureAdapter);
    }
}