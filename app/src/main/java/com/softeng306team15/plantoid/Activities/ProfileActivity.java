package com.softeng306team15.plantoid.Activities;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.softeng306team15.plantoid.Models.IUser;
import com.softeng306team15.plantoid.Models.User;
import com.softeng306team15.plantoid.MyCallback;
import com.softeng306team15.plantoid.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private class ViewHolder {
        TextView textUsername, textEmail, textPicChange;
        LinearLayout discoverButton, wishlistButton, profileButton;
        Button btnCustomiseProfile, btnLogOut;

        ImageView profilePic;

        public ViewHolder() {
            textUsername = findViewById(R.id.textUsername);
            textEmail = findViewById(R.id.textEmail);
            textPicChange = findViewById(R.id.textPicChange);

            btnCustomiseProfile = findViewById(R.id.btnCustomise);
            btnLogOut = findViewById(R.id.btnLogout);

            discoverButton = findViewById(R.id.discover_navbar_button);
            wishlistButton = findViewById(R.id.wishlist_navbar_button);
            profileButton = findViewById(R.id.profile_navbar_button);

            profilePic = findViewById(R.id.imageProfile);
        }
    }

    ViewHolder vh;
    String userId;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();

        vh = new ProfileActivity.ViewHolder();

        userId = getIntent().getStringExtra("User");

        vh.btnCustomiseProfile.setOnClickListener(view -> imageChooser());
        vh.btnLogOut.setOnClickListener(this::goLogOut);

        vh.discoverButton.setOnClickListener(view -> goDiscover(view, userId));

        vh.wishlistButton.setOnClickListener(this::goWishlist);

        setUserDisplay();
    }

    public void setUserDisplay() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // set to actual user's name
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Change the 1 to whichever user is being displayed
        DocumentReference userDoc = db.collection("users").document(userId);
        userDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot userData = task.getResult();
                if (userData.exists()) {
                    IUser user = userData.toObject(User.class);
                    user.setId(userData.getId());
                    vh.textUsername.setText("Welcome " + currentUser.getDisplayName());
                    vh.textEmail.setText(currentUser.getEmail());
                    Picasso.get().load(user.getUserImage()).into(vh.profilePic);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

    }

    public void setNewProfilePic(IUser user, MyCallback callback) {

        String path = String.format("userPics/%s.jpg", user.getId());

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();
        StorageReference profilePicsRef = storageRef.child(path);

        Bitmap bitmap = ((BitmapDrawable) vh.profilePic.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] data = stream.toByteArray();

        // Upload the picture to firebase storage
        UploadTask uploadTask = profilePicsRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> vh.textPicChange.setText("Failed"))
                .addOnSuccessListener(taskSnapshot -> {
                    // Assign pic to user
                    storageRef.child(path).getDownloadUrl().addOnSuccessListener(uri -> {
                        user.updateUserImage(uri.toString());
                        vh.textPicChange.setText("Profile picture change successful");
                        callback.onCallback();
                    });
                });
    }


    /**
     * The following code snippet is from the last code snippet on:
     * https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/
     * By user adityamshidlyali
     * Some changes were made to adapt the code to work with mine
     * These are variable name changes and initialising selectedImageBitmap to null.
     * Copyright article for the website: https://www.geeksforgeeks.org/legal/copyright-information/
     * License: CCBY-SA
     */

    public void imageChooser() {

        Intent chooseImage = new Intent();
        chooseImage.setType("image/*");
        chooseImage.setAction(Intent.ACTION_GET_CONTENT);

        launchImageActivity.launch(chooseImage);

        MyCallback callback = new MyCallback() {
            @Override
            public void onCallback() {
                setUserDisplay();
            }
        };

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get user doc
        DocumentReference userDoc = db.collection("users").document(userId);
        userDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot userDoc1 = task.getResult();
                if (userDoc1.exists()) {
                    IUser user = userDoc1.toObject(User.class);
                    user.setId(userDoc1.getId());
                    setNewProfilePic(user, callback);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    ActivityResultLauncher<Intent> launchImageActivity
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent imageData = result.getData();
                    if (imageData != null
                            && imageData.getData() != null) {
                        Uri selectedImageUri = imageData.getData();
                        Bitmap selectedImageBitmap = null;
                        try {
                            selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(),
                                    selectedImageUri);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        vh.profilePic.setImageBitmap(
                                selectedImageBitmap);
                    }
                }
            });

    /**
     * End of copied code
     */

    public void goLogOut(View v) {
        FirebaseAuth.getInstance().signOut();
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
        wishlistIntent.putExtra("User", userId);
        startActivity(wishlistIntent);
    }
}
