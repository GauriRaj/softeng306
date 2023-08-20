package com.softeng306team15.plantoid.Activities;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
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
        vh = new ViewHolder();
        String userId = getIntent().getStringExtra("User");

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
                        vh.textAddress.setText((String) user.getAddress());
                        Picasso.get().load(user.getUserImage()).into(vh.imageProfilePic);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    public void onConfirmChanges(View v) {
        String newUsername = vh.editUsername.getText().toString();
        String newPassword = vh.editPass.getText().toString();
        String newPassConfirm = vh.editPassConfirm.getText().toString();
        String newEmail = vh.editEmail.getText().toString();
        String newPhone = vh.editPhone.getText().toString();
        String newAddress = vh.editAddress.getText().toString();

        String confirm = vh.editConfirmChanges.getText().toString();
    }

    public void onChangePicture() {

    }
}
