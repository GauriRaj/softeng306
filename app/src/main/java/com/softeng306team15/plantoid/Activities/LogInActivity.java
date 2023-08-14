package com.softeng306team15.plantoid.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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

        vh.btnSignIn.setOnClickListener(this::onSignIn);

    }

    public void onSignIn(View v) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("userName", vh.enterUsername.getText().toString())
                .whereEqualTo("password", vh.enterPassword.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Intent mainIntent = new Intent(getBaseContext(), MainActivity.class);
                                mainIntent.putExtra("User", document.getId());
                                startActivity(mainIntent);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void goCreateAccount(View v) {

    }
}
