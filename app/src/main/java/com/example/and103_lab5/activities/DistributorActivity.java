package com.example.and103_lab5.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.and103_lab5.R;
import com.example.and103_lab5.adapter.DistributorsAdapter;
import com.example.and103_lab5.models.Distributor;
import com.example.and103_lab5.models.Response;
import com.example.and103_lab5.services.HttpRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;


public class DistributorActivity extends AppCompatActivity {
    List<Distributor> list;
    HttpRequest httpRequest;
    String TAG = "//===DistributorActivity";
    private DistributorsAdapter adapter;
    private RecyclerView recyclerView;
    Dialog dialog;
    EditText edNameDistributor, edSearch;
    Button btnSaveDialog, btnCancelDialog;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        httpRequest = new HttpRequest();
        setContentView(R.layout.activity_distributor);
        edSearch = (EditText) findViewById(R.id.edSearch);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatActionButton);


        recyclerView = (RecyclerView) findViewById(R.id.rcvDistributor);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new DistributorsAdapter();
        adapter.setOnItemClickListener(new DistributorsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String id) {
                showDialogDelete(id);
            }

            @Override
            public void updateItem(String id, String name) {
                openDialog(id, name);
            }
        });
        onResume();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("", "");
            }
        });
        edSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String key = edSearch.getText().toString().trim();
                    if (!key.isEmpty()) { // Đảm bảo rằng có từ khóa tìm kiếm
                        httpRequest.callAPI().searchDistributor(key).enqueue(searchDistributor);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please enter search term", Toast.LENGTH_SHORT).show();
                    }
                    return true; // Trả về true để chặn bàn phím tiếp tục hoạt động
                }
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        httpRequest.callAPI().getListDistributor().enqueue(getListDistributor);
    }
    Callback<com.example.and103_lab5.models.Response<ArrayList<Distributor>>> getListDistributor = new Callback<Response<ArrayList<Distributor>>>() {
        @Override
        public void onResponse(Call<Response<ArrayList<Distributor>>> call, retrofit2.Response<Response<ArrayList<Distributor>>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    list = new ArrayList<>();
                    list = response.body().getData();
                    adapter.setData(list);
                    recyclerView.setAdapter(adapter);
                    for (Distributor item: list) {
                        Log.d(TAG, "//===" + item.toString());
                    }
                }
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Distributor>>> call, Throwable throwable) {
            Log.i(TAG, "//==Error=" + throwable.getMessage());
        }
    };

    Callback<Response<Distributor>> addDistributor = new Callback<Response<Distributor>>() {
        @Override
        public void onResponse(Call<Response<Distributor>> call, retrofit2.Response<Response<Distributor>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    Toast.makeText(getApplicationContext(), "Add Distributor Successfull !", Toast.LENGTH_SHORT).show();
                    onResume();
                    dialog.dismiss();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<Distributor>> call, Throwable throwable) {
            Log.i(TAG, "//==Error=" + throwable.getMessage());
        }
    };

    Callback<Response<Distributor>> updateDistributor = new Callback<Response<Distributor>>() {
        @Override
        public void onResponse(Call<Response<Distributor>> call, retrofit2.Response<Response<Distributor>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    Toast.makeText(getApplicationContext(), "Update Successfull !", Toast.LENGTH_SHORT).show();
                    onResume();
                    dialog.dismiss();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<Distributor>> call, Throwable throwable) {
            Log.i(TAG, "//==Error=" + throwable.getMessage());
        }
    };

    public void openDialog(String id, String name) {
        dialog = new Dialog(DistributorActivity.this);
        dialog.setContentView(R.layout.dialog_distributor);
        edNameDistributor = dialog.findViewById(R.id.edName);
        edNameDistributor.setText(name);
        btnSaveDialog = dialog.findViewById(R.id.btnSave);
        btnCancelDialog = dialog.findViewById(R.id.btnCancel);
        btnCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnSaveDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strName = edNameDistributor.getText().toString().trim();
                if (strName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please input Distributor", Toast.LENGTH_SHORT).show();
                } else {
                    Distributor distributor = new Distributor();
                    distributor.setName(strName);
                    if (id.isEmpty()) {
                        // Gọi API thêm mới distributor
                        httpRequest.callAPI().addDistributor(distributor).enqueue(addDistributor);
                    } else {
                        // Gọi API cập nhật distributor
                        httpRequest.callAPI().updateDistributor(id, distributor).enqueue(updateDistributor); // Thay đổi ở đây
                    }
                }
            }
        });

        dialog.show();

    }

    Callback<Response<Distributor>> deleteDistributor = new Callback<Response<Distributor>>() {
        @Override
        public void onResponse(Call<Response<Distributor>> call, retrofit2.Response<Response<Distributor>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    Toast.makeText(getApplicationContext(), "Delete Successfull !", Toast.LENGTH_SHORT).show();
                    onResume();
                    dialog.dismiss();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<Distributor>> call, Throwable throwable) {
            Log.i(TAG, "//==Error=" + throwable.getMessage());
        }
    };

    public void showDialogDelete(String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DistributorActivity.this);
        builder.setTitle("Delete Distributor");
        builder.setMessage("Are you sure you want to delete this Distributor?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                httpRequest.callAPI().deleteDistributor(id).enqueue(deleteDistributor);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    Callback<Response<ArrayList<Distributor>>> searchDistributor = new Callback<Response<ArrayList<Distributor>>>() {
        @Override
        public void onResponse(Call<Response<ArrayList<Distributor>>> call, retrofit2.Response<Response<ArrayList<Distributor>>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    list = response.body().getData();
                    adapter.setData(list);
                    adapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                    for (Distributor item: list) {
                        Log.d(TAG, "//===" + item.toString());
                    }
                }
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Distributor>>> call, Throwable throwable) {
            Log.i(TAG, "//==Error=" + throwable.getMessage());
        }
    };



}