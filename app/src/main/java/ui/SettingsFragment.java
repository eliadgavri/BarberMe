package ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import androidx.preference.PreferenceFragmentCompat;

import com.example.barberme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.net.PasswordAuthentication;

public class SettingsFragment extends PreferenceFragmentCompat {

    Button savePassChangesBtn, saveEmailChangesBtn,saveProfilePicChangesBtn,editProfilePicBtn;
    EditText repeatPassEt, newPassEt, emailEt, oldPassEt, oldPassEmailEt;
    ImageView profilePicIv;
    File file;

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
            }
            case "ChangeEmail": {
                changeEmail();
                break;
            }
            case "ChangePassword": {
                changePassword();
                break;
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    private void changeProfilePicture() {
        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(SettingsFragment.this.getContext());
        final View dialogView = getLayoutInflater().inflate(R.layout.change_profile_pic_dialog, null);

        saveProfilePicChangesBtn = dialogView.findViewById(R.id.save_profile_pic_changes_btn);
        editProfilePicBtn = dialogView.findViewById(R.id.edit_profile_pic_btn);
        profilePicIv=dialogView.findViewById(R.id.profile_pic_image_view);

        editProfilePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }

    private void changeEmail() {
        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(SettingsFragment.this.getContext());
        final View dialogView = getLayoutInflater().inflate(R.layout.change_email_dialog, null);

        saveEmailChangesBtn = dialogView.findViewById(R.id.preference_save_email_changes);
        emailEt = dialogView.findViewById(R.id.preference_change_email_et);

        saveEmailChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String newEmail = emailEt.getText().toString();

                user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SettingsFragment.this.getContext(), "Email changed", Toast.LENGTH_SHORT).show();
                        }
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

    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.AlertDialog_Builder);
        builder.setTitle("Choose your song picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    file=new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"song"+(songList!=null?songList.size():0)+".jpg");
                    Uri imageUri= FileProvider.getUriForFile(MainActivity.this,"com.example.mediaplayerexerciseyossihaigavriel.provider",file);
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    startActivityForResult(takePicture, CAMERA_REQUEST);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickPhoto.setType("image/*");
                    startActivityForResult(pickPhoto , CAMERA_PICK_IMAGE_GALLERY);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
}
