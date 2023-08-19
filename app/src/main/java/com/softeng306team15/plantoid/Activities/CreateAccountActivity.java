package com.softeng306team15.plantoid.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.softeng306team15.plantoid.Models.IUser;
import com.softeng306team15.plantoid.Models.User;
import com.softeng306team15.plantoid.R;

public class CreateAccountActivity  extends AppCompatActivity {

    private class ViewHolder {

        TextView createErrorText;

        EditText usernameText, passText, confirmPassText, emailText, phoneText;

        Button btnCreate, btnLogin;

        public ViewHolder() {
            createErrorText = findViewById(R.id.textErrorCreate);

            usernameText = findViewById(R.id.editUsername);
            passText = findViewById(R.id.editPassword);
            confirmPassText = findViewById(R.id.editPasswordConfirm);
            emailText = findViewById(R.id.editEmail);
            phoneText = findViewById(R.id.editPhone);

            btnCreate = findViewById(R.id.btnCreateAccount);
            btnLogin = findViewById(R.id.btnLogIn);
        }
    }

    ViewHolder vh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        vh = new ViewHolder();

        vh.btnCreate.setOnClickListener(this::createNewUser);

        vh.btnLogin.setOnClickListener(this::goLogin);
    }

    public void goMain(View v, String username, String password) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("userName", username)
                .whereEqualTo("password", password)
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

    public void createNewUser(View v) {
        // Check if passwords match
        String password = vh.passText.getText().toString();
        String confPassword = vh.confirmPassText.getText().toString();

        if (!password.equals(confPassword)) {
            vh.createErrorText.setText("Passwords must match");
            vh.passText.setText("");
            vh.confirmPassText.setText("");
            return;
        }

        String username = vh.usernameText.getText().toString();
        String email = vh.emailText.getText().toString();
        String phone = vh.phoneText.getText().toString();

        if (username.equals("") || password.equals("") || email.equals("") || phone.equals("")) {
            vh.createErrorText.setText("All fields must be filled");
            return;
        }

        IUser user = new User(username, email, password, phone);
        user.createNewUserDocument();

        goMain(v, username, password);
    }

    public void goLogin(View v) {
        Intent loginIntent = new Intent(getBaseContext(), LogInActivity.class);
        startActivity(loginIntent);
    }
}