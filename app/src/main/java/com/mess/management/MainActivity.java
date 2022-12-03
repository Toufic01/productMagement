package com.mess.management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        logo = findViewById(R.id.logo);

        YoYo.with(Techniques.ZoomOut)
                .duration(10000)
                .playOn(logo);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                // startActivity(new Intent(getApplicationContext(),LotteryActivity.class));

                if(firebaseAuth.getCurrentUser()!=null)
                {
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                }
                else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            }
        },1000);

    }
}