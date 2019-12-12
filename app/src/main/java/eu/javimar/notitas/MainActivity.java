package eu.javimar.notitas;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity
{
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = firebaseAuth ->
        {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if(user == null)
            {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        };

        fab.setOnClickListener(view ->
        {

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch(id)
        {
            case R.id.action_settings:
                break;
            case R.id.action_delete_user:
                deleteUser();
                break;
            case R.id.action_logout:
                logOut();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteUser()
    {
        final FirebaseUser user  = mFirebaseAuth.getCurrentUser();
        if(user != null)
        {
            user.delete()
                    .addOnCompleteListener(task ->
                    {
                        if(task.isSuccessful())
                        {
                            Toasty.info(getApplicationContext(), R.string.sign_in_user_deleted,
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
                            finish();
                        }
                    });
        }
    }

    private void logOut()
    {
        mFirebaseAuth.signOut();
        Toasty.info(getApplicationContext(), R.string.sign_in_user_logout,
                Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if(mAuthStateListener != null)
        {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}
