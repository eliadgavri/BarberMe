package ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.barberme.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import adapter.PictureAdapter;
import userData.BarberShop;

public class BarberShopActivity extends AppCompatActivity {

    final int REQUEST_CALL_PERMISSION = 1;
    BarberShop barberShop;
    ImageView barberPicture;
    ArrayList<Uri> pictures;
    RecyclerView picturesRecycler;
    FloatingActionButton phoneBtn, navigateBtn, messageBtn, websiteBtn;
    ImageView imageViewPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_shop);
        barberPicture = findViewById(R.id.barbershop_activity_image_view);
        picturesRecycler = findViewById(R.id.recycler_barber_pictures);
        barberShop = (BarberShop) getIntent().getSerializableExtra("Barbershop");
        Glide.with(this).load(barberShop.getImages().get(0)).into(barberPicture);
        pictures = new ArrayList<>();
        for (String url : barberShop.getImages())
            pictures.add(Uri.parse(url));
        picturesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        PictureAdapter pictureAdapter = new PictureAdapter(pictures);
        picturesRecycler.setAdapter(pictureAdapter);


        phoneBtn = findViewById(R.id.image_btn_phone);
        navigateBtn = findViewById(R.id.image_btn_navigation);
        messageBtn = findViewById(R.id.image_btn_message);
        websiteBtn = findViewById(R.id.image_btn_internet);

        phoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 23) {
                    int hasCallPermission = checkSelfPermission(Manifest.permission.CALL_PHONE);
                    if (hasCallPermission == PackageManager.PERMISSION_GRANTED) {
                        makePhoneCall();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
                    }
                } else {
                    makePhoneCall();
                }

            }
        });


        navigateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.setData(Uri.parse("sms:" + barberShop.getPhoneNumber()));
                startActivity(smsIntent);
            }
        });

        websiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://"+barberShop.getWebsite()); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        pictureAdapter.setListener(new PictureAdapter.PictureListener() {
            @Override
            public void onClickPicture(int position, View view) {

                final AlertDialog.Builder builderDialog = new AlertDialog.Builder(BarberShopActivity.this);
                final View dialogView = getLayoutInflater().inflate(R.layout.pic_layout, null);

                imageViewPic=dialogView.findViewById(R.id.pic_dialog);
                Glide.with(BarberShopActivity.this).load(pictures.get(position)).into(imageViewPic);

                builderDialog.setView(dialogView);
                AlertDialog alertDialog = builderDialog.create();
                alertDialog.show();

            }
        });
    }

    private void makePhoneCall() {

        if (ContextCompat.checkSelfPermission(BarberShopActivity.this,Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BarberShopActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        }
        else {

            String dial = "tel:" +  barberShop.getPhoneNumber();
            startActivity(new Intent(Intent.ACTION_CALL,Uri.parse(dial)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode== REQUEST_CALL_PERMISSION)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                makePhoneCall();
            }
            else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}