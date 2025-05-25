package com.example.gmh_app.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.gmh_app.Adapter.CertificateAdapter;
import com.example.gmh_app.R;
import com.example.gmh_app.databinding.ActivityProfileBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private final List<Certificate> certificateList = new ArrayList<>();
    private CertificateAdapter adapter;

    private final ActivityResultLauncher<String> filePicker = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        String title = getFileName(uri);
                        Certificate cert = new Certificate(
                                UUID.randomUUID(),
                                title,
                                "Self Added",
                                new Date().toString(),
                                uri
                        );
                        certificateList.add(cert);
                        adapter.notifyItemInserted(certificateList.size() - 1);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Certificates");
        }

        adapter = new CertificateAdapter(certificateList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        // Load certificate from shared preferences if any
        SharedPreferences prefs = getSharedPreferences("VideoCompletionPrefs", MODE_PRIVATE);
        String uriString = prefs.getString("certificate_uri", null);
        if (uriString != null) {
            Uri uri = Uri.parse(uriString);
            String title = prefs.getString("certificate_title", "Certificate");
            String issuer = prefs.getString("certificate_issuer", "App");
            String date = prefs.getString("certificate_date", new Date().toString());

            Certificate cert = new Certificate(UUID.randomUUID(), title, issuer, date, uri);
            certificateList.add(cert);
            adapter.notifyItemInserted(certificateList.size() - 1);

            prefs.edit().remove("certificate_uri")
                    .remove("certificate_title")
                    .remove("certificate_issuer")
                    .remove("certificate_date")
                    .apply();
        }

//        binding.fab.setOnClickListener(v -> filePicker.launch("application/pdf"));

    }

    private String getFileName(@NonNull Uri uri) {
        String result = "Unknown";
        try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) {
                    result = cursor.getString(index);
                }
            }
        }
        return result;
    }

    public static class Certificate {
        public final UUID id;
        public final String title;
        public final String issuer;
        public final String date;
        public final Uri fileUri;

        public Certificate(UUID id, String title, String issuer, String date, Uri fileUri) {
            this.id = id;
            this.title = title;
            this.issuer = issuer;
            this.date = date;
            this.fileUri = fileUri;
        }
    }
}
