package ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.barberme.R;

import userData.BarberShop;

public class BarberShopActivity extends AppCompatActivity {

    BarberShop barberShop;
    ImageView barberPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_shop);
        barberPicture = findViewById(R.id.barbershop_activity_image_view);
        barberShop=(BarberShop) getIntent().getSerializableExtra("Barbershop");
        Glide.with(this).load(barberShop.getImages().get(0)).into(barberPicture);
    }
}