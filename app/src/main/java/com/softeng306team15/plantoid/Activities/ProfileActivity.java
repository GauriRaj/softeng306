package com.softeng306team15.plantoid.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softeng306team15.plantoid.Models.IUser;
import com.softeng306team15.plantoid.Models.User;
import com.softeng306team15.plantoid.R;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private class ViewHolder {
        TextView textUsername, textPassword, textEmail, textPhone, textAddr;
        LinearLayout discoverButton, wishlistButton, profileButton;
        Button btnCustomiseProfile, btnSettings, btnLogOut;

        ImageView profilePic;

        public ViewHolder() {
            textUsername = findViewById(R.id.textUsername);
            textPassword = findViewById(R.id.textPassword);
            textEmail = findViewById(R.id.textEmail);
            textPhone = findViewById(R.id.textPhone);
            textAddr = findViewById(R.id.textAddress);

            btnCustomiseProfile = findViewById(R.id.btnCustomise);
            btnSettings = findViewById(R.id.btnSettings);
            btnLogOut = findViewById(R.id.btnLogout);

            discoverButton = findViewById(R.id.discover_navbar_button);
            wishlistButton = findViewById(R.id.wishlist_navbar_button);
            profileButton = findViewById(R.id.profile_navbar_button);

            profilePic = findViewById(R.id.imageProfile);
        }
    }

    ViewHolder vh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        vh = new ProfileActivity.ViewHolder();

        String userId = getIntent().getStringExtra("User");

        vh.btnCustomiseProfile.setOnClickListener(this::goCustomise);
        vh.btnSettings.setOnClickListener(this::goSettings);
        vh.btnLogOut.setOnClickListener(this::goLogOut);

        vh.discoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goDiscover(view, userId);
            }
        });

        vh.wishlistButton.setOnClickListener(this::goWishlist);

        setUserDisplay(userId);
    }

    public void setUserDisplay(String id) {
        // set to actual user's name
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Change the 1 to whichever user is being displayed
        DocumentReference userDoc = db.collection("users").document(id);
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot userData = task.getResult();
                    if (userData.exists()) {
                        IUser user = userData.toObject(User.class);
                        vh.textUsername.setText((String) user.getUserName());
                        vh.textEmail.setText((String) user.getEmail());
                        vh.textPhone.setText((String) user.getPhoneNumber());
                        vh.textAddr.setText((String) user.getAddress());
                        Picasso.get().load(user.getUserImage()).into(vh.profilePic);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

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
        Intent logOutIntent = new Intent(getBaseContext(), LogInActivity.class);
        startActivity(logOutIntent);
    }
    public void goDiscover(View v, String userId) {
        Intent discoverIntent = new Intent(getBaseContext(), MainActivity.class);
        discoverIntent.putExtra("User", userId);
        startActivity(discoverIntent);
    }
    public void goWishlist(View v) {
        Intent wishlistIntent = new Intent(getBaseContext(), WishlistActivity.class);
        startActivity(wishlistIntent);
    }
}
