package com.softeng306team15.plantoid.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.softeng306team15.plantoid.R;

public class CreateAccountActivity  extends AppCompatActivity {

    private class ViewHolder {

        EditText usernameText, passText, confirmPassText, emailText, phoneText;

        Button btnCreate, btnLogin;

        public ViewHolder() {
            usernameText = findViewById(R.id.editUsername);
            passText = findViewById(R.id.editPassword);
            confirmPassText = findViewById(R.id.editPasswordConfirm);
            emailText = findViewById(R.id.editEmail);
            phoneText = findViewById(R.id.editPhone);
        }
    }

    ViewHolder vh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        vh = new ViewHolder();
    }

    public void goLogin(View v) {
        //Intent loginIntent = new Intent(getBaseContext(), LoginActivity.class);
        //startActivity(loginIntent);
    }
}