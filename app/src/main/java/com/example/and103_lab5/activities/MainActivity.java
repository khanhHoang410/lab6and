package com.example.and103_lab5.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.and103_lab5.R;

public class MainActivity extends AppCompatActivity {
    Button btnDistributor, btnRegister;
    private SharedPreferences sharedPreferences;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("CHECK_LOGIN", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("USERNAME", "");
        if (username.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        }
        setContentView(R.layout.activity_main);
        btnDistributor = (Button) findViewById(R.id.btnDistributor);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        btnDistributor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DistributorActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

//        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//        startActivity(intent);


    }
}