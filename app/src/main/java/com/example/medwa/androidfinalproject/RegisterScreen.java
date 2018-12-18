package com.example.medwa.androidfinalproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class RegisterScreen extends AppCompatActivity {

    // TextInput Declarations
    TextInputEditText username, email, pass, cpass;
    // FireBase Variable Declarations
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    // ProgressBar Declaration
    private ProgressBar progressBar;

    // OnCreate called
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        // Assign various TextInputEditTexts with their corresponding XML IDs
        username = (TextInputEditText) findViewById(R.id.ET_CRE_User);
        email = (TextInputEditText) findViewById(R.id.ET_CRE_Email);
        pass = (TextInputEditText) findViewById(R.id.ET_CRE_Pass);
        cpass = (TextInputEditText) findViewById(R.id.ET_CRE_Confirm_Pass);

        // FireBase assignments
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference();

        // ProgressBar assigned with corresponding XML ID
        progressBar = findViewById(R.id.PB_CRE);
    }

    // OnClick Registration Button
    public void registerScreenClick(View view) {

        // Get final Strings for TextInputEditText fields
        final String name = username.getText().toString();
        final String e = email.getText().toString();
        String p = pass.getText().toString();
        String cp = cpass.getText().toString();

        // Check to see if the email and/or password is empty
        if(TextUtils.isEmpty(e) || TextUtils.isEmpty(p)){

            Toast.makeText(this,"A field is empty!",Toast.LENGTH_SHORT).show();
        }else {

            // Check to see if the password matches the confirm password
            if(p.equals(cp)) {
                // Set the Progressbar to visible that way the user knows they are trying to
                // register their account
                progressBar.setVisibility(View.VISIBLE);
                // Attempting to register account with FireBase Authentication Server
                mAuth.createUserWithEmailAndPassword(e, cp).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // When the task is successful push the information for the account to the
                        // FireBase database
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterScreen.this, "Registration Successful!", Toast.LENGTH_SHORT).show();

                            FirebaseUser user = mAuth.getCurrentUser();
                            myRef.child(user.getUid()).child("name").setValue(name);
                            myRef.child(user.getUid()).child("email").setValue(e);
                            myRef.child(user.getUid()).child("avatar").setValue("");
                            myRef.child(user.getUid()).child("lat").setValue("");
                            myRef.child(user.getUid()).child("long").setValue("");
                            myRef.child(user.getUid()).child("bus").setValue("");

                            // Move to new Intent
                            Intent intent = new Intent(RegisterScreen.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterScreen.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
            else {
                Toast.makeText(RegisterScreen.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }



    }
}

