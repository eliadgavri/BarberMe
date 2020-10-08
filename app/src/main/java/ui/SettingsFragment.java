package ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceFragmentCompat;

import com.bumptech.glide.Glide;
import com.example.barberme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.PasswordAuthentication;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import model.Consumer;
import model.DatabaseFetch;
import service.UploadPostService;
import userData.Review;
import userData.User;

public class SettingsFragment extends PreferenceFragmentCompat {

    Button savePassChangesBtn, saveEmailChangesBtn,saveProfilePicChangesBtn,editProfilePicBtn,saveUsernameChangesBtn;
    EditText repeatPassEt, newPassEt, emailEt, oldPassEt, firstNameEt, lastNameEt, passwordEt;
    ImageView profilePicIv;
    File file;
    private final int SELECT_IMAGE = 1;
    private final int CAMERA_REQUEST = 2;
    private final int WRITE_PERMISSION_REQUEST = 3;
    private Uri imageUri;
    DatabaseFetch databaseFetch = new DatabaseFetch();
    int prefMode = 1;
    String imageUrl;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public boolean onPreferenceTreeClick(androidx.preference.Preference preference) {
        switch (preference.getKey()) {
            case "ChangeProfilePicture":
            {
                changeProfilePicture();
                prefMode = 1;
                break;
            }
            case "ChangeUserName":
            {
                changeUserName();
                prefMode = 2;
                break;
            }
            case "ChangeEmail": {
                changeEmail();
                prefMode = 3;
                break;
            }
            case "ChangePassword": {
                changePassword();
                prefMode = 4;
                break;
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    private void changeUserName() {

        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(SettingsFragment.this.getContext());
        final View dialogView = getLayoutInflater().inflate(R.layout.change_username_dialog, null);

        saveUsernameChangesBtn = dialogView.findViewById(R.id.save_changes_username_btn);
        firstNameEt=dialogView.findViewById(R.id.change_first_name_et);
        lastNameEt = dialogView.findViewById(R.id.change_last_name_et);

        saveUsernameChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=firstNameEt.getText().toString() + " " + lastNameEt.getText().toString();
                if(!username.isEmpty()) {
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(username).build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SettingsFragment.this.getContext(), "Username changed", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent("usernameChange");
                                LocalBroadcastManager.getInstance(SettingsFragment.this.getContext()).sendBroadcast(intent);
                                updateFirestore(username);
                            } else {
                                Toast.makeText(SettingsFragment.this.getContext(), "Username isn't changed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(SettingsFragment.this.getContext(), "The username text can't be empty,please fill the text filed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builderDialog.setView(dialogView);
        AlertDialog alertDialog = builderDialog.create();
        alertDialog.show();
    }

    private void changeProfilePicture() {
        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(SettingsFragment.this.getContext());
        final View dialogView = getLayoutInflater().inflate(R.layout.change_profile_pic_dialog, null);

        saveProfilePicChangesBtn = dialogView.findViewById(R.id.save_profile_pic_changes_btn);
        editProfilePicBtn = dialogView.findViewById(R.id.edit_profile_pic_btn);
        profilePicIv=dialogView.findViewById(R.id.profile_pic_image_view);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        Glide.with(this.getContext()).load(user.getPhotoUrl()).into(profilePicIv);
        editProfilePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        saveProfilePicChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfilePicture();
            }
        });
        builderDialog.setView(dialogView);
        AlertDialog alertDialog = builderDialog.create();
        alertDialog.show();
    }

    private void saveProfilePicture() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        user.updateProfile(new UserProfileChangeRequest.Builder().setPhotoUri(imageUri).build()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(SettingsFragment.this.getContext(), "Profile picture changed", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent("profilePictureChanged");
                    LocalBroadcastManager.getInstance(SettingsFragment.this.getContext()).sendBroadcast(intent);
                    updateFirestore(user.getPhotoUrl().toString());
                }
                else
                    Toast.makeText(SettingsFragment.this.getContext(), "There was an error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeEmail() {
        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(SettingsFragment.this.getContext());
        final View dialogView = getLayoutInflater().inflate(R.layout.change_email_dialog, null);

        saveEmailChangesBtn = dialogView.findViewById(R.id.preference_save_email_changes);
        emailEt = dialogView.findViewById(R.id.preference_change_email_et);
        passwordEt = dialogView.findViewById(R.id.preference_change_email_password_et);

        saveEmailChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String password = passwordEt.getText().toString();
                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), password);
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String newEmail = emailEt.getText().toString();
                        user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SettingsFragment.this.getContext(), "Email changed", Toast.LENGTH_SHORT).show();
                                    updateFirestore(newEmail);
                                }
                            }
                        });
                    }
                });
            }
        });
        builderDialog.setView(dialogView);
        AlertDialog alertDialog = builderDialog.create();
        alertDialog.show();
    }

    private void changePassword() {
        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(SettingsFragment.this.getContext());
        final View dialogView = getLayoutInflater().inflate(R.layout.change_password_dialog, null);

        savePassChangesBtn = dialogView.findViewById(R.id.preference_save_password_changes);
        oldPassEt = dialogView.findViewById(R.id.preference_old_password_et);
        newPassEt = dialogView.findViewById(R.id.preference_new_password_et);
        repeatPassEt = dialogView.findViewById(R.id.preference_confirm_password_et);

        savePassChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPass = newPassEt.getText().toString();
                String oldPass = oldPassEt.getText().toString();
                String repeatPass = repeatPassEt.getText().toString();
                if (!oldPass.isEmpty() && !newPass.isEmpty() && !repeatPass.isEmpty() && newPass.equals(repeatPass)) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(user.getEmail(), oldPassEt.getText().toString());

                    String newPassword = newPassEt.getText().toString();
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SettingsFragment.this.getContext(), "Password changed", Toast.LENGTH_SHORT).show();
                                            updateFirestore(newPassword);
                                        } else {
                                            Toast.makeText(SettingsFragment.this.getContext(), "Password isn't changed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                } else {
                    Toast.makeText(SettingsFragment.this.getContext(), "Something invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builderDialog.setView(dialogView);
        AlertDialog alertDialog = builderDialog.create();
        alertDialog.show();
    }

    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(),R.style.AlertDialog_Builder);
        builder.setTitle("Choose your profile picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    if(Build.VERSION.SDK_INT>=23) {
                        int hasWritePermission = getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
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

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void takePicture() {
        String pictureName = String.valueOf(System.currentTimeMillis());
        file = new File(getActivity().getExternalFilesDir(null), pictureName + ".jpg");
        imageUri = FileProvider.getUriForFile(SettingsFragment.this.getContext(), getActivity().getPackageName() + ".provider", file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    addPictureFromGallery(data);
                }
            }
        }
        else if (requestCode == CAMERA_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
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
        Glide.with(this.getContext()).load(imageUri).into(profilePicIv);
    }

    private void addPictureFromCamera() {
        Glide.with(this.getContext()).load(imageUri).into(profilePicIv);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==WRITE_PERMISSION_REQUEST){
            if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this.getContext(), "you need permission to take picture", Toast.LENGTH_SHORT).show();
            }
            else{
                //Has permissions
                takePicture();
            }
        }
    }

    private void updateFirestore(String input) {
        Consumer<User> consumer = new Consumer<User>() {
            @Override
            public void apply(User param) {
                switch (prefMode)
                {
                    case 1:
                        Consumer<String> updateImage = new Consumer<String>() {
                            @Override
                            public void apply(String param1) {

                                param.setProfilePicture(param1);
                                FirebaseFirestore.getInstance().collection("users").document(param.getId())
                                        .set(param, SetOptions.merge());
                            }
                        };
                        uploadPhotoToPhotos(updateImage);
                        return;
                    case 2:
                        param.setFirstName(firstNameEt.getText().toString());
                        param.setLastName(lastNameEt.getText().toString());
                        break;
                    case 3:
                        param.setEmail(input);
                        break;
                    case 4:
                        param.setPassword(input);
                        break;
                }
                FirebaseFirestore.getInstance().collection("users").document(param.getId())
                        .set(param, SetOptions.merge());
            }
        };
        databaseFetch.findUserData(consumer, FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    private void uploadPhotoToPhotos(Consumer<String> consumer) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imagesRef = storage.getReference().child("profiles");;
        final StorageReference photoRef = imagesRef.child(String.valueOf(System.currentTimeMillis()));
        UploadTask uploadTask = photoRef.putFile(imageUri);
        //upload the img + perform a task of getting the download url from the cloud
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                task.getException().printStackTrace();
                return null;
            }
            return photoRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                imageUrl = task.getResult().toString();
                consumer.apply(imageUrl);
            }
        });
    }
}
