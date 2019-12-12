package eu.javimar.notitas;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity
{
    @BindView(R.id.signInMail) TextInputEditText mSignInMail;
    @BindView(R.id.signInPass) TextInputEditText mSignInPass;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        mFirebaseAuth = FirebaseAuth.getInstance();

        // Go to main if registered
        if(mFirebaseAuth.getCurrentUser() != null)
        {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @OnClick(R.id.registerButton)
    public void registerAction()
    {
        String email = mSignInMail.getText().toString();
        String password = mSignInPass.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toasty.error(this, R.string.sign_in_email_empty_err,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password))
        {
            Toasty.error(this, R.string.sign_in_pass_empty_err,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length() < 6)
        {
            Toasty.error(this, R.string.sign_in_pass_length_err,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task ->
                {
                    if(task.isSuccessful())
                    {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                    else
                    {
                        Toasty.error(getApplicationContext(), R.string.sign_in_login_err,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}