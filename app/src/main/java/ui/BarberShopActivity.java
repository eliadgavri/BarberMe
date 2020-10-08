package ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.barberme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adapter.PictureAdapter;
import adapter.ReviewAdapter;
import userData.BarberShop;
import userData.Review;
import userData.User;

public class BarberShopActivity extends AppCompatActivity {

    final int REQUEST_CALL_PERMISSION = 1;
    BarberShop barberShop;
    ImageView barberPicture;
    Button addReview;
    LinearLayout newReviewLayout;
    EditText newReviewText;
    Button submitNewReview;
    List<Review> reviews;
    ArrayList<Uri> pictures;
    RecyclerView picturesRecycler;
    RecyclerView reviewRecycler;
    FloatingActionButton phoneBtn, navigateBtn, messageBtn, websiteBtn;
    ImageView imageViewPic;
    StringBuilder barberAddress = new StringBuilder();
    ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_shop);
        barberPicture = findViewById(R.id.barbershop_activity_image_view);
        picturesRecycler = findViewById(R.id.recycler_show_barber_pictures);
        reviewRecycler = findViewById(R.id.recycler_barber_reviews);
        addReview = findViewById(R.id.add_review_bt);
        newReviewLayout = findViewById(R.id.new_review_layout);
        newReviewText = findViewById(R.id.review_text_et);
        submitNewReview = findViewById(R.id.submit_review_bt);
        newReviewLayout.setVisibility(View.GONE);
        barberShop = (BarberShop) getIntent().getSerializableExtra("Barbershop");
        Glide.with(this).load(barberShop.getImages().get(0)).into(barberPicture);
        pictures = new ArrayList<>();
        for (String url : barberShop.getImages())
            pictures.add(Uri.parse(url));
        picturesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        PictureAdapter pictureAdapter = new PictureAdapter(pictures);
        picturesRecycler.setAdapter(pictureAdapter);
        reviewRecycler.setLayoutManager(new LinearLayoutManager(this));
        reviews = barberShop.getReviews();
        if(reviews == null)
            reviews = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviews);
        reviewRecycler.setAdapter(reviewAdapter);

        phoneBtn = findViewById(R.id.image_btn_phone);
        navigateBtn = findViewById(R.id.image_btn_navigation);
        messageBtn = findViewById(R.id.image_btn_message);
        websiteBtn = findViewById(R.id.image_btn_internet);

        addReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newReviewLayout.setVisibility(View.VISIBLE);
            }
        });

        submitNewReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadNewReview();
            }
        });

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

        buildAddress();
        navigateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    // Launch Waze to look for Hawaii:
                    String url = "https://waze.com/ul?q=" + barberAddress.toString();
                    Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
                    startActivity( intent );
                }
                catch ( ActivityNotFoundException ex  )
                {
                    // If Waze is not installed, open it in Google Play:
                    Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
                    startActivity(intent);
                }
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

    private void uploadNewReview() {
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    User user = task.getResult().toObject(User.class);
                    SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date date = new Date(System.currentTimeMillis());
                    Review newReview = new Review(user, newReviewText.getText().toString(), formatter.format(date), 5);
                    reviews.add(newReview);
                    reviewAdapter.notifyDataSetChanged();
                    updateBarberReviews();
                    newReviewLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void updateBarberReviews() {
        barberShop.setReviews(reviews);
        FirebaseFirestore.getInstance().collection("shops").document(barberShop.getId())
                .set(barberShop, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(BarberShopActivity.this, "New review published", Toast.LENGTH_SHORT).show();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void buildAddress() {
        String city = barberShop.getCity();
        city.replaceAll(" ", "%20");
        String address = barberShop.getAddress();
        address.replaceAll(" ", "%20");
        barberAddress.append(city);
        barberAddress.append("%20");
        barberAddress.append(address);
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