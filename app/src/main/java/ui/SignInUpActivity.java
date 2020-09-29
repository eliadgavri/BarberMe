package ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.barberme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignInUpActivity extends AppCompatActivity
    implements SignInFragment.SignInListener, SignUpFragment.SignUpListener{

    final String TAG = "MainActivity";
    FirebaseAuth firebaseAuth;
    String fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signinup);
        firebaseAuth = FirebaseAuth.getInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.container, new SignInFragment(), TAG).commit();

/*        firebaseListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if(user != null)
            {
                if(fullName == null) { //signup
                    user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(fullName).build()).addOnCompleteListener(task -> {
                        fullName = null;
                        if(task.isSuccessful())
                            Toast.makeText(SignInUpActivity.this, user.getDisplayName() + " Welcome", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        };*/
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
    public void onSignUpFragmentLoginClick() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new SignInFragment(), TAG).addToBackStack(null).commit();
    }

    @Override
    public void onSignUpFragmentRegisterClick(String fullname, String email, String password, String repeatPassword) {
        if(!fullname.isEmpty() && !email.isEmpty() && !password.isEmpty() && !repeatPassword.isEmpty() && password.equals(repeatPassword)) {
/*
            fullName = fullname;
*/
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    firebaseAuth.getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(fullname).build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(SignInUpActivity.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignInUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
                else
                    Toast.makeText(SignInUpActivity.this, "Signup failed", Toast.LENGTH_SHORT).show();
            });
        }
        else
            Toast.makeText(SignInUpActivity.this, "Passwords not equal or something empty", Toast.LENGTH_SHORT).show();
    }
}
