package eu.javimar.notitas;

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

public class ForgotPassActivity extends AppCompatActivity
{
    @BindView(R.id.forgotPassEmail) TextInputEditText forgotPassEmail;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.submitNewPassButton)
    public void submitNewPass()
    {
        String email = forgotPassEmail.getText().toString();
        if(TextUtils.isEmpty(email))
        {
            Toasty.warning(getApplicationContext(),R.string.sign_in_email_empty_err, Toast.LENGTH_SHORT).show();
            return;
        }
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task ->
                {
                    if(task.isSuccessful())
                    {
                        Toasty.info(getApplicationContext(),R.string.sign_in_submit_new_pass_sent,
                                Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toasty.error(getApplicationContext(),R.string.sign_in_submit_new_pass_error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
