package ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberme.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import adapter.PictureAdapter;
import service.UploadPostService;
import userData.BarberShop;

public class EditBarberShopActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private FirebaseAuth auth;;
    private RecyclerView picturesList;
    private PictureAdapter pictureAdapter;
    private ArrayList<Uri> pictures = new ArrayList<>();
    private Button uploadPicture;
    private Button takePicture;
    private Button finishBT;
    private TextView picturesCountTv;
    private TextInputEditText nameET;
    private TextInputEditText cityET;
    private TextInputEditText addressET;
    private TextInputEditText phoneNumberET;
    private TextInputEditText websiteET;
    private File file;
    private int numOfPictures = 0;
    private final int SELECT_IMAGE = 1;
    private final int CAMERA_REQUEST = 2;
    private final int WRITE_PERMISSION_REQUEST = 3;
    private Uri imageUri;
    BarberShop barberShop;

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = auth.getCurrentUser();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_barber_shop_layout);
        auth = FirebaseAuth.getInstance();
        barberShop = (BarberShop) getIntent().getSerializableExtra("Barbershop");
        uploadPicture = findViewById(R.id.upload_button);
        takePicture = findViewById(R.id.take_picture_button);
        picturesList = findViewById(R.id.recyclerview_pics);
        picturesCountTv = findViewById(R.id.pictures_count_tv);
        picturesCountTv = findViewById(R.id.pictures_count_tv);
        nameET = findViewById(R.id.name_et);
        cityET = findViewById(R.id.city_et);
        addressET = findViewById(R.id.address_et);
        phoneNumberET = findViewById(R.id.phone_et);
        websiteET = findViewById(R.id.website_et);
        finishBT = findViewById(R.id.finish_button);
        setOldData();
        picturesList.setLayoutManager(new GridLayoutManager(this, 3));
        pictureAdapter = new PictureAdapter(pictures);
        picturesList.setAdapter(pictureAdapter);
        finishBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAd();
            }
        });
        uploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPicture();
            }
        });
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Request permissions
                if(Build.VERSION.SDK_INT>=23) {
                    int hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if(hasWritePermission!= PackageManager.PERMISSION_GRANTED){
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST);
                    }
                    else{
                        //has permission
                        takePicture();
                    }
                }
                else {
                    //has permission
                    takePicture();
                }
            }
        });
    }

    private void setOldData() {
        nameET.setText(barberShop.getName());
        cityET.setText(barberShop.getCity());
        addressET.setText(barberShop.getAddress());
        phoneNumberET.setText(barberShop.getPhoneNumber());
        websiteET.setText(barberShop.getWebsite());
        for (String url : barberShop.getImages())
            pictures.add(Uri.parse(url));
        numOfPictures = pictures.size();
        picturesCountTv.setText(this.getResources().getString(R.string.pictures_count) + " " + numOfPictures);
    }

    private void showMsg(String msg) {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.note))
                .setMessage(msg)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(getResources().getString(R.string.ok), null)
                .create()
                .show();
    }

    //Upload picture from gallery
    private void uploadPicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_picture)), SELECT_IMAGE);
    }

    //Take picture from camera
    private void takePicture(){
        String pictureName = String.valueOf(System.currentTimeMillis());
        file = new File(this.getExternalFilesDir(null), pictureName + ".jpg");
        imageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    addPictureFromGallery(data);
                }
            }
        }
        else if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                addPictureFromCamera();
            }
            else {
                try {
                    file.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                file = null;
            }
        }
    }

    private void addPictureFromGallery(Intent data) {
        imageUri = data.getData();
        numOfPictures++;
        pictures.add(imageUri);
        picturesCountTv.setText(this.getResources().getString(R.string.pictures_count) + " " + numOfPictures);
        pictureAdapter.notifyDataSetChanged();
    }

    private void addPictureFromCamera() {
        numOfPictures++;
        pictures.add(imageUri);
        picturesCountTv.setText(this.getResources().getString(R.string.pictures_count) + " " + numOfPictures);
        pictureAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==WRITE_PERMISSION_REQUEST){
            if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, this.getResources().getString(R.string.permission), Toast.LENGTH_SHORT).show();
            }
            else{
                //Has permissions
                takePicture();
            }
        }
    }

    private void updateAd() {
        barberShop.setName(nameET.getText().toString());
        barberShop.setCity(cityET.getText().toString());
        barberShop.setAddress(addressET.getText().toString());
        barberShop.setPhoneNumber(phoneNumberET.getText().toString());
        barberShop.setWebsite(websiteET.getText().toString());
        FirebaseFirestore.getInstance().collection("shops").document(barberShop.getId())
                .set(barberShop, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EditBarberShopActivity.this, EditBarberShopActivity.this.getResources().getString(R.string.update_successful), Toast.LENGTH_SHORT).show();
                Intent mainIntent = new Intent(EditBarberShopActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditBarberShopActivity.this, EditBarberShopActivity.this.getResources().getString(R.string.update_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
