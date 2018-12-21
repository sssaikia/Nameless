package com.sstudio.nameless;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (firebaseAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
                    //intent.putExtra("Email",firebaseAuth.getCurrentUser().getEmail());
                    startActivity(intent);
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        ((Button) findViewById(R.id.signIn))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivityForResult(
                                AuthUI.getInstance().createSignInIntentBuilder()
                                        .setTheme(R.style.AppTheme)
                                        .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                        .setTosUrl("https://www.google.com/policies/terms/")
                                        .setIsSmartLockEnabled(false)
                                        .build(),
                                111);
                    }

                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
                //intent.putExtra("Email",firebaseAuth.getCurrentUser().getEmail());
                startActivity(intent);
                finish();
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Action cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    boolean quit = false;

    @Override
    public void onBackPressed() {
        if (!quit) {
            CoordinatorLayout frameLayout = findViewById(R.id.frameLay);
            Snackbar snackbar = Snackbar.make(frameLayout, "Press again to exit.", Snackbar.LENGTH_SHORT);
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onShown(Snackbar sb) {
                    quit = true;
                    super.onShown(sb);
                }

                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    quit = false;
                    super.onDismissed(transientBottomBar, event);
                }
            });
            snackbar.show();
        } else {
            finish();
        }

    }
}
