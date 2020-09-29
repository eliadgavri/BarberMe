package ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.barberme.R;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpFragment extends Fragment {

    TextInputEditText fullname;
    TextInputEditText email;
    TextInputEditText password;
    TextInputEditText repeatpassword;
    Button backToSignin;
    Button signup;
    SignUpListener signUpListener;

    interface SignUpListener {
        void onSignUpFragmentLoginClick();
        void onSignUpFragmentRegisterClick(String fullname, String email, String password, String repeatPassword);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        signUpListener = (SignUpListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_signup, container, false);
        fullname = rootView.findViewById(R.id.fullname_et);
        email = rootView.findViewById(R.id.email_et);
        password = rootView.findViewById(R.id.password_et);
        repeatpassword = rootView.findViewById(R.id.confirm_password_et);
        backToSignin = rootView.findViewById(R.id.login_bt);
        signup = rootView.findViewById(R.id.signup_bt);

        backToSignin.setOnClickListener(view -> {
            if(signUpListener!=null)
                signUpListener.onSignUpFragmentLoginClick();
        });

        signup.setOnClickListener(view -> {
            if(signUpListener!=null)
                signUpListener.onSignUpFragmentRegisterClick(fullname.getText().toString(), email.getText().toString(), password.getText().toString(), repeatpassword.getText().toString());
        });

        return  rootView;
    }
}
