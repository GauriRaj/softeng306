package com.softeng306team15.plantoid.Activities;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softeng306team15.plantoid.Models.IUser;
import com.softeng306team15.plantoid.Models.User;
import com.softeng306team15.plantoid.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class ChangeProfileActivity extends AppCompatActivity {

    private class ViewHolder {

        TextView textUsername, textEmail, textPhone, textAddress, textError;
        EditText editUsername, editPass, editPassConfirm, editEmail, editPhone, editAddress, editConfirmChanges;
        ImageView imageProfilePic;
        Button btnProfilePic, btnConfirm;
        LinearLayout discoverButton, wishlistButton, profileButton;

        public ViewHolder() {
            textUsername = findViewById(R.id.textUsername);
            textEmail = findViewById(R.id.textEmail);
            textPhone = findViewById(R.id.textPhone);
            textAddress = findViewById(R.id.textAddress);
            textError = findViewById(R.id.textErrorMess);

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
        vh = new ViewHolder();
        String userId = getIntent().getStringExtra("User");

        setUserDisplay(userId);

        vh.btnConfirm.setOnClickListener(view -> onConfirmChanges(userId));

        vh.btnProfilePic.setOnClickListener(view -> onChangePicture());
    }

    public void setUserDisplay(String id) {
        // set to actual user's name
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Change the 1 to whichever user is being displayed
        DocumentReference userDoc = db.collection("users").document(id);
        userDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot userData = task.getResult();
                if (userData.exists()) {
                    IUser user = userData.toObject(User.class);
                    vh.textUsername.setText(user.getUserName());
                    vh.textEmail.setText(user.getEmail());
                    vh.textPhone.setText(user.getPhoneNumber());
                    vh.textAddress.setText(user.getAddress());
                    Picasso.get().load(user.getUserImage()).into(vh.imageProfilePic);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

    }

    public void setNewData(IUser user) {

        String newUsername = vh.editUsername.getText().toString();
        String newPassword = vh.editPass.getText().toString();
        String newPassConfirm = vh.editPassConfirm.getText().toString();
        String newEmail = vh.editEmail.getText().toString();
        String newPhone = vh.editPhone.getText().toString();
        String newAddress = vh.editAddress.getText().toString();

        vh.textError.setText("");

        // Ignore empty fields
        // Check if input data is same as old data

        // Username check
        if (!newUsername.isEmpty()) {
            if (newUsername.equals(user.getUserName())) {
                vh.textError.append("New username is same as old username\n");
            } else {
                user.updateUserName(newUsername);
            }
        }

        // Password check
        if (!newPassword.isEmpty()) {
            if (newPassword.equals(newPassConfirm)) {
                if (newPassword.equals(user.getPassword())) {
                    vh.textError.append("New password is same as old password\n");
                } else {
                    user.updatePassword(newPassword);
                }
            } else {
                vh.textError.append("Passwords do not match\n");
            }
        }

        // Email check

        if (!newEmail.isEmpty()) {
            if (newEmail.equals(user.getEmail())) {
                vh.textError.append("New email is same as old email\n");
            } else {
                user.updateEmail(newEmail);
            }
        }

        // Phone check

        if (!newPhone.isEmpty()) {
            if (newPhone.equals(user.getPhoneNumber())) {
                vh.textError.append("New phone number is same as old phone number\n");
            } else {
                user.updatePhoneNumber(newPhone);
            }
        }

        // Address check

        if (!newAddress.isEmpty()) {
            if (newEmail.equals(user.getAddress())) {
                vh.textError.append("New address is same as old address\n");
            } else {
                user.updateAddress(newAddress);
            }
        }

    }

    public void onConfirmChanges(String id) {


        String confirm = vh.editConfirmChanges.getText().toString();

        // Run all data verification:

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get user doc
        DocumentReference userDoc = db.collection("users").document(id);
        userDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot userDoc1 = task.getResult();
                if (userDoc1.exists()) {
                    IUser user = userDoc1.toObject(User.class);
                    user.setId(userDoc1.getId());
                    // Check if confirm password is correct
                    if (!confirm.equals(user.getId())) {
                        vh.textError.setText("Confirmation password is incorrect");
                        return;
                    }
                    setNewData(user);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

    }

    public void onChangePicture() {
        imageChooser();
    }

    public void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeActivity.launch(i);

    }

    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // do your operation from here....
                    if (data != null
                            && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap = null;
                        try {
                            selectedImageBitmap
                                    = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(),
                                    selectedImageUri);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        vh.imageProfilePic.setImageBitmap(
                                selectedImageBitmap);
                    }
                }
            });

}
