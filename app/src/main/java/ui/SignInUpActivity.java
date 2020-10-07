package ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.barberme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import dialog.ForgotPasswordDialog;
import service.UploadNewUserService;
import userData.User;

public class SignInUpActivity extends AppCompatActivity
    implements SignInFragment.SignInListener, SignUpFragment.SignUpListener{

    final String TAG = "SignInUpActivity";
    FirebaseAuth firebaseAuth;
    final String annonymousPicture = "https://firebasestorage.googleapis.com/v0/b/barberme-83e8b.appspot.com/o/images%2FprofilePicture.png?alt=media&token=8c5ed008-5852-453b-83ec-0d53c8dc5f07";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signinup);
        firebaseAuth = FirebaseAuth.getInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.container, new SignInFragment(), TAG).commit();
    }

    @Override
    public void onSignInFragmentLoginClick(String email, String password) {
        if(!email.isEmpty() || !password.isEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(SignInUpActivity.this, "Signin Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignInUpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(SignInUpActivity.this, "Signin failed", Toast.LENGTH_SHORT).show();
            });
        }
        else
            Toast.makeText(SignInUpActivity.this, "email and password can't be empty!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSignInFragmentRegisterClick() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new SignUpFragment(), TAG).addToBackStack(null).commit();
    }

    @Override
    public void onForgotPasswordClick() {
        ForgotPasswordDialog dialog = new ForgotPasswordDialog(this);
        dialog.show();
    }

    @Override
    public void onGuestLoginClick() {
        firebaseAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Intent intent = new Intent(SignInUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignInUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onSignUpFragmentLoginClick() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new SignInFragment(), TAG).addToBackStack(null).commit();
    }

    @Override
    public void onSignUpFragmentRegisterClick(String firstName, String lastName, String email, String password, String repeatPassword,String gender,String birthday,String address) {
        if(!firstName.isEmpty() && !lastName.isEmpty() && !address.isEmpty() && !birthday.isEmpty() && !email.isEmpty() && !password.isEmpty() && !repeatPassword.isEmpty() && password.equals(repeatPassword)) {
            Uri profilePicture = Uri.parse(annonymousPicture);
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    firebaseAuth.getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(firstName +" "+ lastName).setPhotoUri(profilePicture).build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(SignInUpActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                            User user =new User(firebaseAuth.getCurrentUser().getUid(),firstName,lastName,password,email,annonymousPicture,gender,birthday,address);
                            publishNewUser(user);
                            Intent intent = new Intent(SignInUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
                else
                    Toast.makeText(SignInUpActivity.this, "SignuUp failed", Toast.LENGTH_SHORT).show();
            });
        }
        else
            Toast.makeText(SignInUpActivity.this, "Passwords not equal or something empty", Toast.LENGTH_SHORT).show();
    }

    private void publishNewUser(User user) {

        Intent intent = new Intent(this, UploadNewUserService.class)
                .putExtra("uID",user.getuID())
                .putExtra("firstName",user.getFirstName())
                .putExtra("lastName",user.getLastName())
                .putExtra( "password",user.getPassword())
                .putExtra("email",user.getEmail())
                .putExtra("profilePicture",user.getProfilePicture())
                .putExtra("gender",user.getGender())
                .putExtra("birthday",user.getBirthday())
                .putExtra("address",user.getAddress());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(intent);
        else
            startService(intent);

    }

}
