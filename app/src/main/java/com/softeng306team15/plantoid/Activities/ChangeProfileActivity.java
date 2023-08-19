package com.softeng306team15.plantoid.Activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.softeng306team15.plantoid.R;

public class ChangeProfileActivity extends AppCompatActivity {

    private class ViewHolder {

        TextView textUsername, textEmail, textPhone, textAddress;
        EditText editUsername, editPass, editPassConfirm, editEmail, editPhone, editAddress, editConfirmChanges;
        ImageView imageProfilePic;
        Button btnProfilePic, btnConfirm;
        LinearLayout discoverButton, wishlistButton, profileButton;

        public ViewHolder() {
            textUsername = findViewById(R.id.textUsername);
            textEmail = findViewById(R.id.textEmail);
            textPhone = findViewById(R.id.textPhone);
            textAddress = findViewById(R.id.textAddress);

            editUsername = findViewById(R.id.editUsername);
            editPass = findViewById(R.id.editNewPassword);
            editPassConfirm = findViewById(R.id.editPasswordConfirm);
            editEmail = findViewById(R.id.editEmail);
            editPhone = findViewById(R.id.editPhone);
            editAddress = findViewById(R.id.editAddress);
            editConfirmChanges = findViewById(R.id.textPasswordConfirmation);

            imageProfilePic = findViewById(R.id.imageProfilePic);

            btnProfilePic = findViewById(R.id.btnChangeImage);
            btnConfirm = findViewById(R.id.btnConfirmChanges);

            discoverButton = findViewById(R.id.discover_navbar_button);
            wishlistButton = findViewById(R.id.wishlist_navbar_button);
            profileButton = findViewById(R.id.profile_navbar_button);
        }
    }

    ViewHolder vh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_details);

    }
}
