package com.example.and103_lab5.activities;

import static com.example.and103_lab5.R.id.edPassword;
import static com.example.and103_lab5.R.id.edUserName;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.and103_lab5.R;
import com.example.and103_lab5.models.Response;
import com.example.and103_lab5.models.User;
import com.example.and103_lab5.services.HttpRequest;

import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends AppCompatActivity {

    String TAG = "//===LoginActivity===";
    Button btnLogin, btnSignup;
    HttpRequest httpRequest;
    EditText edUserName, edPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        edUserName = (EditText) findViewById(R.id.edUserName);
        edPassword = (EditText) findViewById(R.id.edPassword);

        httpRequest = new HttpRequest();
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnSignup = (Button) findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strUserName = edUserName.getText().toString().trim();
                String strPassword = edPassword.getText().toString().trim();
                if (strUserName.isEmpty() && strPassword.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Input username and password !", Toast.LENGTH_SHORT).show();
                } else {
                    User user = new User();
                    user.setUsername(strUserName);
                    user.setPassword(strPassword);
                    httpRequest.callAPI().checkLogin(user).enqueue(responseCheckLogin);
//                    finish();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }
        });
    }
    Callback<Response<User>> responseCheckLogin = new Callback<Response<User>>() {
        @Override
        public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
            if (response.isSuccessful()) {
                Log.i(TAG, "//=responseCheckLogin" + response.body().toString());
                if (response.body().getStatus() == 200) {
                    Toast.makeText(getApplicationContext(), "Login successfull !", Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPreferences = getSharedPreferences("CHECK_LOGIN", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Token", response.body().getToken());
                    editor.putString("Refresh_Token", response.body().getRefreshToken());
                    editor.putString("USERNAME", response.body().getData().getUsername());
                    editor.putString("ID", response.body().getData().get_id());
                    editor.commit();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Login failed !", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<User>> call, Throwable throwable) {
            Toast.makeText(getApplicationContext(), "Login fail !", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "//=responseCheckLogin ERROR: " + throwable.getMessage());
        }
    };
}