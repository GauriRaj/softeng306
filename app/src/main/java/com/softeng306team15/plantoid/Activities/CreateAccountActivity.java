package com.softeng306team15.plantoid.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.softeng306team15.plantoid.UserModels.IUser;
import com.softeng306team15.plantoid.UserModels.User;
import com.softeng306team15.plantoid.MyCallback;
import com.softeng306team15.plantoid.R;

public class CreateAccountActivity  extends AppCompatActivity {

    private class ViewHolder {

        TextView createErrorText;

        EditText usernameText, passText, confirmPassText, emailText;

        Button btnCreate, btnLogin;

        public ViewHolder() {
            createErrorText = findViewById(R.id.textErrorCreate);

            usernameText = findViewById(R.id.editUsername);
            passText = findViewById(R.id.editPassword);
            confirmPassText = findViewById(R.id.editPasswordConfirm);
            emailText = findViewById(R.id.editEmail);

            btnCreate = findViewById(R.id.btnCreateAccount);
            btnLogin = findViewById(R.id.btnLogIn);
        }
    }

    ViewHolder vh;
    private FirebaseAuth mAuth;
    public Boolean createFail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        vh = new ViewHolder();

        vh.btnCreate.setOnClickListener(this::verifyInputs);

        vh.btnLogin.setOnClickListener(this::goLogin);
    }

    public void createFirebaseAccount(String email, String password, MyCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            callback.onCallback();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            vh.createErrorText.setText("Invalid email address");
                            createFail = true;
                            callback.onCallback();
                        }
                    }
                });
    }

    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            vh.createErrorText.setText("Invalid email address");
                        }
                    }
                });
    }

    public void createAccount(String username, String email, String password) {
        mAuth = FirebaseAuth.getInstance();

        MyCallback callback = new MyCallback() {
            @Override
            public void onCallback() {
                if (!createFail) {
                    signIn(email, password);
                    IUser userInfo = new User(username, email);
                    userInfo.createNewUserDocument(new MyCallback(){
                        @Override
                        public void onCallback() {
                            Intent mainIntent = new Intent(getBaseContext(), MainActivity.class);
                            mainIntent.putExtra("User", userInfo.getId());
                            startActivity(mainIntent);
                        }
                    });
                } else {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    Log.d(TAG, "currentUser " + currentUser);
                    if(currentUser == null){
                        vh.createErrorText.setText("Invalid email address");
                    }
                }
                createFail = false;
            }
        };

        createFirebaseAccount(email, password, callback);
    }

    public void verifyInputs(View v) {
        // Check if passwords match
        String password = vh.passText.getText().toString();
        String confPassword = vh.confirmPassText.getText().toString();
        String username = vh.usernameText.getText().toString();

        if (!password.equals(confPassword)) {
            vh.createErrorText.setText("Passwords must match");
            vh.passText.setText("");
            vh.confirmPassText.setText("");
            return;
        }

        String email = vh.emailText.getText().toString();

        if (username.equals("") || password.equals("") || email.equals("")) {
            vh.createErrorText.setText("All fields must be filled");
            return;
        }

        if (password.length() < 6) {
            vh.createErrorText.setText("Password must be at least 6 characters");
            vh.passText.setText("");
            vh.confirmPassText.setText("");
            return;
        }

        createAccount(username, email, password);

    }

    public void goLogin(View v) {
        Intent loginIntent = new Intent(getBaseContext(), LogInActivity.class);
        startActivity(loginIntent);
    }
}