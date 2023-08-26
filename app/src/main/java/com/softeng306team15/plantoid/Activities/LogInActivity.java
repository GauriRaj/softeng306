package com.softeng306team15.plantoid.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.TextView;

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
        EditText enterEmail, enterPassword;
        Button btnSignIn, btnCreateAccount;

        TextView textLoginError;

        public ViewHolder() {

            enterEmail = findViewById(R.id.editTextEmail);
            enterPassword = findViewById(R.id.editTextPassword);

            btnSignIn = findViewById(R.id.btnSignIn);
            btnCreateAccount = findViewById(R.id.btnNewAccount);

            textLoginError = findViewById(R.id.textError);
        }
    }

    ViewHolder vh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        vh = new ViewHolder();

        vh.btnSignIn.setOnClickListener(this::onSignIn);
        vh.btnCreateAccount.setOnClickListener(this::goCreateAccount);

    }

    public void onSignIn(View v) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("userName", vh.enterEmail.getText().toString())
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
                            // The above loop will not run if no users are found matching the results
                            if(task.getResult().size() == 0){
                                vh.textLoginError.setText("Username or password is incorrect");
                                vh.enterEmail.setText("");
                                vh.enterPassword.setText("");
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void goCreateAccount(View v) {
        Intent createIntent = new Intent(getBaseContext(), CreateAccountActivity.class);
        startActivity(createIntent);
    }
}
