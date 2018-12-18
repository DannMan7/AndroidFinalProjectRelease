package com.example.medwa.androidfinalproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// Settings Activity
public class Settings extends AppCompatActivity {
    // F for Feet || T for Meter
    private Switch mSwitchFeetMeter;
    // F for Mile || T for Kilometer
    private Switch mSwitchMileKilo;
    // F for Fahrenheit || T for Celsius
    private Switch mSwitchFahCel;
    // Sign Out Button
    private Button mBTN_SignOut;
    private boolean feet, meter, mile, kilometer, fahrenheit, celsisus;

    // FireBase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    // OnCreate called
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Assigning Switches to corresponding XML IDs
        mSwitchFeetMeter = findViewById(R.id.SwitchFeetMeter);
        mSwitchMileKilo = findViewById(R.id.SwitchMileKilometer);
        mSwitchFahCel = findViewById(R.id.SwitchFahrenheitCelsius);
        // Assign Button to corresponding XML ID
        mBTN_SignOut = findViewById(R.id.BTN_SignOut);
        // Assigning FireBase variables
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference();
        // Sign Out Button OnClickListener
        mBTN_SignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Signs the User out of current FireBase instance
                mAuth.signOut();
                finish();
            }
        });

    }
    // OnStop Called
    @Override
    protected void onStop()
    {
        super.onStop();
        // Will get User Settings
        getSettings();
        // If Authentication is Successful the Settings will be saved to FireBase Database
        if(mAuth.getCurrentUser() != null) {
            String mUID = mAuth.getCurrentUser().getUid();
            myRef.child(mUID).child("Settings").child("Feet").setValue(feet);
            myRef.child(mUID).child("Settings").child("Meter").setValue(meter);
            myRef.child(mUID).child("Settings").child("Mile").setValue(mile);
            myRef.child(mUID).child("Settings").child("Kilometer").setValue(kilometer);
            myRef.child(mUID).child("Settings").child("fahrenheit").setValue(fahrenheit);
            myRef.child(mUID).child("Settings").child("Celsius").setValue(celsisus);
            // User will receive OnScreen message to let them know Settings were saved Successfully
            Toast.makeText(Settings.this, "Settings Saved!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(Settings.this, "Signed Out!", Toast.LENGTH_SHORT).show();
        }

    }
    // Get Settings Method
    private void getSettings()
    {
        // Checks to see if FeetMeter switch is checked and assigns appropriate value
        if(!mSwitchFeetMeter.isChecked())
        {
            feet = true;
            meter = false;
        }
        else {
            feet = false;
            meter = true;
        }
        // Checks to see if MileKilo switch is checked and assigns appropriate value
        if(!mSwitchMileKilo.isChecked())
        {
            mile = true;
            kilometer = false;
        }
        else {
            mile = false;
            kilometer = true;
        }
        // Checks to see if FahCel switch is checked and assigns appropriate value
        if(!mSwitchFahCel.isChecked())
        {
            fahrenheit = true;
            celsisus = false;
        }
        else {
            fahrenheit = false;
            celsisus = true;
        }
    }
}
