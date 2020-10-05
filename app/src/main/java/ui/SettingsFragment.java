package ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.barberme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.PasswordAuthentication;

public class SettingsFragment extends PreferenceFragmentCompat {

    Button savePassChangesBtn,saveEmailChangesBtn;
    EditText repeatPassEt,newPassEt,emailEt,oldPassEt,oldPassEmailEt;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        Preference passwordPref = (Preference) findPreference("ChangePassword");
        passwordPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                final AlertDialog.Builder builderDialog = new AlertDialog.Builder(SettingsFragment.this.getContext());
                final View dialogView = getLayoutInflater().inflate(R.layout.change_password_dialog, null);

                savePassChangesBtn = dialogView.findViewById(R.id.preference_save_password_changes);
                oldPassEt = dialogView.findViewById(R.id.preference_old_password_et);
                newPassEt = dialogView.findViewById(R.id.preference_new_password_et);
                repeatPassEt = dialogView.findViewById(R.id.preference_confirm_password_et);

                String newPass = newPassEt.getText().toString();
                String oldPass = oldPassEt.getText().toString();
                String repeatPass = repeatPassEt.getText().toString();

                savePassChangesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                                                }
                                                else {
                                                    Toast.makeText(SettingsFragment.this.getContext(), "Password isn't changed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(SettingsFragment.this.getContext(), "Something invalid", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builderDialog.setView(dialogView);
                AlertDialog alertDialog = builderDialog.create();
                alertDialog.show();
                return true;
            }
        });


        Preference emailPref = (Preference) findPreference("ChangeEmail");
        emailPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                final AlertDialog.Builder builderDialog = new AlertDialog.Builder(SettingsFragment.this.getContext());
                final View dialogView = getLayoutInflater().inflate(R.layout.change_email_dialog, null);

                saveEmailChangesBtn = dialogView.findViewById(R.id.preference_save_email_changes);
                emailEt=dialogView.findViewById(R.id.preference_change_email_et);

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
                return true;
            }
        });

    }
}
