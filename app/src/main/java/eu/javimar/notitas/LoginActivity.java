package eu.javimar.notitas;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener
{
    @BindView(R.id.signInMail) TextInputEditText mSignInMail;
    @BindView(R.id.signInPass) TextInputEditText mSignInPass;

    private FirebaseAuth mFirebaseAuth;
    private GoogleApiClient mGoogleApiClient;

    private static final int GOOGLE_SIGN_IN = 900;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mFirebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        // If logged, go to Main
        if(mFirebaseAuth.getCurrentUser() != null)
        {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @OnClick(R.id.sign_in_button_google)
    public void loginActionWithGoogle()
    {
        Intent signIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signIntent, GOOGLE_SIGN_IN);
    }

    @OnClick(R.id.sign_in_button_user_pass)
    public void loginAction()
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
        mFirebaseAuth.signInWithEmailAndPassword(mSignInMail.getText().toString(),
                            mSignInPass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            Toasty.success(getApplicationContext(),
                                    String.format(getString(R.string.sign_in_user_welcome),
                                            mFirebaseAuth.getCurrentUser().getEmail()),
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),
                                    MainActivity.class));
                        }
                        else
                        {
                            // If sign in fails, display a message to the user.
                            Toasty.error(getApplicationContext(), R.string.sign_in_auth_err,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GOOGLE_SIGN_IN)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess())
            {
                GoogleSignInAccount account = result.getSignInAccount();
                authWithGoogle(account);
            }
        }
    }

    private void authWithGoogle(GoogleSignInAccount account)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(task ->
        {
            if(task.isSuccessful())
            {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
            else{
                Toasty.error(getApplicationContext(), R.string.sign_in_auth_err,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    @OnClick(R.id.registerButton)
    public void registerAction()
    {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    @OnClick(R.id.forgotpassButton)
    public void forgotPassAction()
    {
        startActivity(new Intent(this, ForgotPassActivity.class));
    }
}
