package ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberme.R;

import java.io.File;
import java.util.ArrayList;

import model.PictureAdapter;

public class AddBarberShopActivity extends AppCompatActivity {

    RecyclerView picturesList;
    PictureAdapter pictureAdapter;
    ArrayList<Uri> pictures = new ArrayList<>();
    Button uploadPicture;
    Button takePicture;
    File file;
    int numOfPictures = 0;
    final int MAX_PICTURES = 6;
    private final int SELECT_IMAGE = 1;
    private final int CAMERA_REQUEST = 2;
    private final int WRITE_PERMISSION_REQUEST = 3;
    Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_barbershop);
        uploadPicture = findViewById(R.id.upload_button);
        takePicture = findViewById(R.id.take_picture_button);
        picturesList = findViewById(R.id.recyclerview_pics);
        picturesList.setLayoutManager(new GridLayoutManager(this, MAX_PICTURES/2));
        pictureAdapter = new PictureAdapter(pictures);
        picturesList.setAdapter(pictureAdapter);
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

    //Upload picture from gallery
    private void uploadPicture() {
        if(numOfPictures>=MAX_PICTURES){
            Toast.makeText(AddBarberShopActivity.this, "6 is the maximum pictures you can add", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
    }

    //Take picture from camera
    private void takePicture(){
        if(numOfPictures>=MAX_PICTURES){
            Toast.makeText(AddBarberShopActivity.this, "6 is the maximum pictures you can add", Toast.LENGTH_SHORT).show();
            return;
        }
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
        pictureAdapter.notifyDataSetChanged();
    }

    private void addPictureFromCamera() {
        numOfPictures++;
        pictures.add(imageUri);
        pictureAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==WRITE_PERMISSION_REQUEST){
            if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "you need permission to take picture", Toast.LENGTH_SHORT).show();
            }
            else{
                //Has permissions
                takePicture();
            }
        }
    }
}
