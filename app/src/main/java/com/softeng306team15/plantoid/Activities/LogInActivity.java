package com.softeng306team15.plantoid.Activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.softeng306team15.plantoid.R;

public class LogInActivity extends AppCompatActivity {
    private class ViewHolder {
        EditText enterUsername, enterPassword;
        Button btnSignIn, btnCreateAccount;
        LinearLayout discoverButton, wishlistButton, profileButton;

        public ViewHolder() {

            enterUsername = findViewById(R.id.editTextUsername);
            enterPassword = findViewById(R.id.editTextPassword);

            btnSignIn = findViewById(R.id.btnSignIn);
            btnCreateAccount = findViewById(R.id.btnNewAccount);

            discoverButton = findViewById(R.id.discover_navbar_button);
            wishlistButton = findViewById(R.id.wishlist_navbar_button);
            profileButton = findViewById(R.id.profile_navbar_button);
        }
    }

    ViewHolder vh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        vh = new ViewHolder();

    }

    public void onSignIn(View v) {

    }

    public void goCreateAccount(View v) {

    }
}
