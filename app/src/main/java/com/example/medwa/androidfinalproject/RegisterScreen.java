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

    TextInputEditText username, email, pass, cpass;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        username = (TextInputEditText) findViewById(R.id.ET_CRE_User);
        email = (TextInputEditText) findViewById(R.id.ET_CRE_Email);
        pass = (TextInputEditText) findViewById(R.id.ET_CRE_Pass);
        cpass = (TextInputEditText) findViewById(R.id.ET_CRE_Confirm_Pass);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference();
    }

    public void registerScreenClick(View view) {

        final String name = username.getText().toString();
        final String e = email.getText().toString();
        String p = pass.getText().toString();
        String cp = cpass.getText().toString();

        if(TextUtils.isEmpty(e) || TextUtils.isEmpty(p)){

            Toast.makeText(this,"A field is empty!",Toast.LENGTH_SHORT).show();
        }else {

            if(p.equals(cp)) {
                mAuth.createUserWithEmailAndPassword(e, cp).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterScreen.this, "Registration Successful!", Toast.LENGTH_SHORT).show();

                            FirebaseUser user = mAuth.getCurrentUser();
                            myRef.child(user.getUid()).child("name").setValue(name);
                            myRef.child(user.getUid()).child("email").setValue(e);
                            myRef.child(user.getUid()).child("avatar").setValue("");
                            myRef.child(user.getUid()).child("lat").setValue("");
                            myRef.child(user.getUid()).child("long").setValue("");
                            myRef.child(user.getUid()).child("bus").setValue("");


                            Intent intent = new Intent(RegisterScreen.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterScreen.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else {
                Toast.makeText(RegisterScreen.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            }
        }



    }
}

