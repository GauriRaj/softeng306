package com.softeng306team15.plantoid.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.softeng306team15.plantoid.R;

public class ProfileActivity extends AppCompatActivity {

    private class ViewHolder {
        TextView textUsername, textPassword, textEmail, textPhone, textCardNo, textBillAddr, textShipAddr;
        LinearLayout discoverButton, wishlistButton, profileButton;
        Button btnCustomiseProfile, btnSettings, btnLogOut;

        public ViewHolder() {
            textUsername = findViewById(R.id.textUsername);
            textPassword = findViewById(R.id.textPassword);
            textEmail = findViewById(R.id.textEmail);
            textPhone = findViewById(R.id.textPhone);
            textCardNo = findViewById(R.id.textCardNo);
            textBillAddr = findViewById(R.id.textBillAddress);
            textShipAddr = findViewById(R.id.textShipAddress);

            btnCustomiseProfile = findViewById(R.id.btnCustomise);
            btnSettings = findViewById(R.id.btnSettings);
            btnLogOut = findViewById(R.id.btnLogout);

            discoverButton = findViewById(R.id.discover_navbar_button);
            wishlistButton = findViewById(R.id.wishlist_navbar_button);
            profileButton = findViewById(R.id.profile_navbar_button);
        }
    }

    ViewHolder vh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        vh = new ProfileActivity.ViewHolder();

        vh.btnCustomiseProfile.setOnClickListener(this::goCustomise);
        vh.btnSettings.setOnClickListener(this::goSettings);
        vh.btnLogOut.setOnClickListener(this::goLogOut);

        vh.discoverButton.setOnClickListener(this::goDiscover);
        vh.wishlistButton.setOnClickListener(this::goWishlist);

    }


    public void goCustomise(View v) {
        Intent customiseIntent = new Intent(getBaseContext(), CustomiseProfileActivity.class);
        startActivity(customiseIntent);
    }
    public void goSettings(View v) {
        Intent settingsIntent = new Intent(getBaseContext(), SettingsActivity.class);
        startActivity(settingsIntent);
    }
    public void goLogOut(View v) {
        //Intent logOutIntent = new Intent(getBaseContext(), CustomiseProfileActivity.class);
        //startActivity(logOutIntent);
    }
    public void goDiscover(View v) {
        Intent discoverIntent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(discoverIntent);
    }
    public void goWishlist(View v) {
        Intent wishlistIntent = new Intent(getBaseContext(), WishlistActivity.class);
        startActivity(wishlistIntent);
    }
}
