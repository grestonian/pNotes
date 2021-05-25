package com.gurpreet.pnotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivitySplash extends AppCompatActivity {

    private static final String TAG = ActivitySplash.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        findViewById(R.id.temp).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });


    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null) {
            Log.d(TAG, currentUser.getDisplayName());
            startActivity(new Intent(getApplicationContext(), ActivityDashboard.class));
            Toast.makeText(this, "Signing in", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            Toast.makeText(this, "Signing in", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}