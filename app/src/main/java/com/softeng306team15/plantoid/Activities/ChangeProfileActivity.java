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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.softeng306team15.plantoid.Models.IUser;
import com.softeng306team15.plantoid.Models.User;
import com.softeng306team15.plantoid.R;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ChangeProfileActivity extends AppCompatActivity {

    private class ViewHolder {

        TextView textUsername, textEmail, textPhone, textAddress, textError,
                textUserChange, textPassChange, textEmailChange, textPhoneChange, textAddressChange, textPicChange;
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

            textUserChange = findViewById(R.id.textUserChange);
            textPassChange = findViewById(R.id.textPassChange);
            textEmailChange = findViewById(R.id.textEmailChange);
            textPhoneChange = findViewById(R.id.textPhoneChange);
            textAddressChange = findViewById(R.id.textAddressChange);
            textPicChange = findViewById(R.id.textPicChange);

            editUsername = findViewById(R.id.editUsername);
            editPass = findViewById(R.id.editNewPassword);
            editPassConfirm = findViewById(R.id.editPasswordConfirm);
            editEmail = findViewById(R.id.editEmail);
            editPhone = findViewById(R.id.editPhoneNo);
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

    Boolean pfpChanged;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_details);
        vh = new ViewHolder();
        userId = getIntent().getStringExtra("User");
        pfpChanged = Boolean.FALSE;

        setUserDisplay();

        vh.btnConfirm.setOnClickListener(view -> onConfirmChanges());
        vh.btnProfilePic.setOnClickListener(view -> imageChooser());

        vh.profileButton.setOnClickListener(view -> goProfile(view));
        vh.discoverButton.setOnClickListener(view -> goMain(view));
        vh.wishlistButton.setOnClickListener(view -> goWishlist(view));
    }

    public void setUserDisplay() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDoc = db.collection("users").document(userId);
        userDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot userData = task.getResult();
                if (userData.exists()) {
                    IUser user = userData.toObject(User.class);
                    user.setId(userData.getId());
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
                vh.textUserChange.setText("Username change successful");
            }
            vh.editUsername.setText("");
        }

        // Password check
        if (!newPassword.isEmpty()) {
            if (newPassword.equals(newPassConfirm)) {
                if (newPassword.equals(user.getPassword())) {
                    vh.textError.append("New password is same as old password\n");
                } else {
                    user.updatePassword(newPassword);
                    vh.textPassChange.setText("Password change successful");
                }
            } else {
                vh.textError.append("Passwords do not match\n");
            }
            vh.editPass.setText("");
            vh.editPassConfirm.setText("");
        }

        // Email check

        if (!newEmail.isEmpty()) {
            if (newEmail.equals(user.getEmail())) {
                vh.textError.append("New email is same as old email\n");
            } else {
                user.updateEmail(newEmail);
                vh.textEmailChange.setText("Email change successful");
            }
            vh.editEmail.setText("");
        }

        // Phone check

        if (!newPhone.isEmpty()) {
            if (newPhone.equals(user.getPhoneNumber())) {
                vh.textError.append("New phone number is same as old phone number\n");
            } else {
                user.updatePhoneNumber(newPhone);
                vh.textPhoneChange.setText("Phone number change successful");
            }
            vh.editPhone.setText("");
        }

        // Address check

        if (!newAddress.isEmpty()) {
            if (newEmail.equals(user.getAddress())) {
                vh.textError.append("New address is same as old address\n");
            } else {
                user.updateAddress(newAddress);
                vh.textAddressChange.setText("Address change successful");
            }
            vh.editAddress.setText("");
        }

        // Set new profile pic
        if (pfpChanged) {
            String path = String.format("userPics/%s.jpg", user.getId());

            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference storageRef = storage.getReference();
            StorageReference profilePicsRef = storageRef.child(path);

            Bitmap bitmap = ((BitmapDrawable) vh.imageProfilePic.getDrawable()).getBitmap();
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
                        }).addOnFailureListener(exception -> vh.textError.append("Profile picture change failed"));
            });
        }
    }

    public void onConfirmChanges() {

        String confirm = vh.editConfirmChanges.getText().toString();

        // Run all data verification:

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get user doc
        DocumentReference userDoc = db.collection("users").document(userId);
        userDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot userDoc1 = task.getResult();
                if (userDoc1.exists()) {
                    IUser user = userDoc1.toObject(User.class);
                    user.setId(userDoc1.getId());
                    // Check if confirm password is correct
                    if (!confirm.equals(user.getPassword())) {
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

        vh.editConfirmChanges.setText("");
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
        pfpChanged = Boolean.TRUE;
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
                        vh.imageProfilePic.setImageBitmap(
                                selectedImageBitmap);
                    }
                }
            });

    /**
     * End of copied code
     */

    // Navbar
    public void goWishlist(View v) {
        Intent wishlistIntent = new Intent(getBaseContext(), WishlistActivity.class);
        wishlistIntent.putExtra("User", userId);
        startActivity(wishlistIntent);
    }

    public void goProfile(View v) {
        Intent profileIntent = new Intent(getBaseContext(), ProfileActivity.class);
        profileIntent.putExtra("User", userId);
        startActivity(profileIntent);
    }

    public void goMain(View v) {
        Intent mainIntent = new Intent(getBaseContext(), MainActivity.class);
        mainIntent.putExtra("User", userId);
        startActivity(mainIntent);
    }

}
