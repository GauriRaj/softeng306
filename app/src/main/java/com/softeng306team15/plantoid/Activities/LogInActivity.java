package com.softeng306team15.plantoid.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.softeng306team15.plantoid.R;

public class LogInActivity extends AppCompatActivity {
    private class ViewHolder {
        EditText enterUsername, enterEmail, enterPassword;
        Button btnSignIn, btnCreateAccount;

        TextView textLoginError;

        public ViewHolder() {

            enterUsername = findViewById(R.id.editTextUsername);
            enterEmail = findViewById(R.id.editTextEmail);
            enterPassword = findViewById(R.id.editTextPassword);

            btnSignIn = findViewById(R.id.btnSignIn);
            btnCreateAccount = findViewById(R.id.btnNewAccount);

            textLoginError = findViewById(R.id.textError);
        }
    }

    ViewHolder vh;
    private FirebaseAuth mAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        vh = new ViewHolder();

        vh.btnSignIn.setOnClickListener(this::onSignIn);
        vh.btnCreateAccount.setOnClickListener(this::goCreateAccount);

    }

    public void getUserId() {
        Log.d(TAG, "Email " + vh.enterEmail.getText().toString());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("userName", vh.enterUsername.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                userId = document.getId();
                            }
                            // The above loop will not run if no users are found matching the results
                            if(task.getResult().size() == 0){
                                vh.textLoginError.setText("Email not in database");
                                vh.enterEmail.setText("");
                                vh.enterPassword.setText("");
                                Log.d(TAG, "Email not found " + vh.enterEmail.getText().toString());
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void onSignIn(View v) {
        String email = vh.enterEmail.getText().toString();
        String password = vh.enterPassword.getText().toString();

        if (vh.enterUsername.getText().toString().equals("")){
            vh.textLoginError.setText("Please enter username");
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            getUserId();
                            Log.d(TAG, "user id: " + userId);
                            Intent mainIntent = new Intent(getBaseContext(), MainActivity.class);
                            mainIntent.putExtra("User", userId);
                            startActivity(mainIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            vh.textLoginError.setText("Email or password is incorrect");
                            vh.enterEmail.setText("");
                            vh.enterPassword.setText("");
                            Toast.makeText(LogInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    public void goCreateAccount(View v) {
        Intent createIntent = new Intent(getBaseContext(), CreateAccountActivity.class);
        startActivity(createIntent);
    }
}
