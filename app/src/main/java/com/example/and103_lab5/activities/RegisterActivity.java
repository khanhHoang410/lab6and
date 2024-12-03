package com.example.and103_lab5.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.and103_lab5.R;
import com.example.and103_lab5.models.Response;
import com.example.and103_lab5.models.User;
import com.example.and103_lab5.services.HttpRequest;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText edUserName, edPassWord, edEmail, edName, edAge;
    Button btnRegister;
    ImageView imageView;
    File file;
    private HttpRequest httpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edUserName = findViewById(R.id.edUserName);
        edPassWord = findViewById(R.id.edPassWord);
        edEmail = findViewById(R.id.edEmail);
        edName = findViewById(R.id.edName);
        edAge = findViewById(R.id.edAge);
        btnRegister = findViewById(R.id.btnRegister);
        imageView = findViewById(R.id.imvAvatar);
        httpRequest = new HttpRequest();

        btnRegister.setOnClickListener(v -> {
            if (validateInputs()) {
                performRegistration();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
    }

    private boolean validateInputs() {
        if (edUserName.getText().toString().trim().isEmpty()) {
            edUserName.setError("Username is required.");
            return false;
        }
        if (edPassWord.getText().toString().trim().isEmpty()) {
            edPassWord.setError("Password is required.");
            return false;
        }
        if (edEmail.getText().toString().trim().isEmpty()) {
            edEmail.setError("Email is required.");
            return false;
        }
        if (edName.getText().toString().trim().isEmpty()) {
            edName.setError("Name is required.");
            return false;
        }
        if (edAge.getText().toString().trim().isEmpty()) {
            edAge.setError("Age is required.");
            return false;
        }
        return true;
    }

    private void performRegistration() {
        RequestBody _username = RequestBody.create(MediaType.parse("multipart/form-data"), edUserName.getText().toString().trim());
        RequestBody _password = RequestBody.create(MediaType.parse("multipart/form-data"), edPassWord.getText().toString().trim());
        RequestBody _email = RequestBody.create(MediaType.parse("multipart/form-data"), edEmail.getText().toString().trim());
        RequestBody _name = RequestBody.create(MediaType.parse("multipart/form-data"), edName.getText().toString().trim());
        RequestBody _age = RequestBody.create(MediaType.parse("multipart/form-data"), edAge.getText().toString().trim());
        RequestBody _available = RequestBody.create(MediaType.parse("multipart/form-data"), "true");

        MultipartBody.Part muPart = null;
        if (file != null) {
            Log.d("File Info", "File name: " + file.getName() + ", File path: " + file.getAbsolutePath());
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
            muPart = MultipartBody.Part.createFormData("avatar", file.getName(), requestBody);
        } else {
            Log.e("File Error", "File is null");
        }


        httpRequest.callAPI().register(_username, _password, _email, _name, _age, _available, muPart).enqueue(new Callback<Response<User>>() {
            @Override
            public void onResponse(@NonNull Call<Response<User>> call, @NonNull retrofit2.Response<Response<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 200) {
                        Toast.makeText(getApplicationContext(), "Register Successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Registration failed: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Thêm đoạn này để in ra lỗi chi tiết từ server khi gặp lỗi 400
                    try {
                        String errorResponse = response.errorBody().string();
                        Log.e("Registration Error", errorResponse);
                        Toast.makeText(getApplicationContext(), "Error: " + errorResponse, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Response<User>> call, @NonNull Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Registration failed. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
        Log.d("RegisterData", "Username: " + edUserName.getText().toString().trim());
        Log.d("RegisterData", "Password: " + edPassWord.getText().toString().trim());
        Log.d("RegisterData", "Email: " + edEmail.getText().toString().trim());
        Log.d("RegisterData", "Name: " + edName.getText().toString().trim());
        Log.d("RegisterData", "Age: " + edAge.getText().toString().trim());
        Log.d("RegisterData", "File: " + (file != null ? file.getName() : "No file"));

    }

    ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri imagePath = data.getData();
                        file = createFileFromUri(imagePath, "avatar");
                        Glide.with(RegisterActivity.this)
                                .load(file)
                                .thumbnail(Glide.with(RegisterActivity.this).load(R.drawable.ic_avatar))
                                .centerCrop()
                                .circleCrop()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(imageView);
                    }
                }
            });

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        getImage.launch(intent);
    }

    private File createFileFromUri(Uri path, String name) {
        File _file = new File(RegisterActivity.this.getCacheDir(), name + ".png");
        try (InputStream in = RegisterActivity.this.getContentResolver().openInputStream(path);
             OutputStream out = new FileOutputStream(_file)) {

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            return _file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    // Method to check if app is in foreground
    private boolean isAppInForeground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses != null) {
            String packageName = getApplicationContext().getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        && appProcess.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
