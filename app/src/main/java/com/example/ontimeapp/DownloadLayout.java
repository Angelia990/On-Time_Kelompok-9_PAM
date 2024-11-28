package com.example.ontimeapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.io.OutputStream;

public class DownloadLayout extends AppCompatActivity {

    ImageView closeDownload, downloadImage, downloadImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Panggil setContentView terlebih dahulu untuk mengatur layout
        setContentView(R.layout.activity_download_layout);

        // Inisialisasi elemen UI
        closeDownload = findViewById(R.id.close_download);
        downloadImage = findViewById(R.id.download_img);
        downloadImageView = findViewById(R.id.download_image_view);

        // Set Edge-to-Edge
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ambil URL gambar dari intent
        String imageUrl = getIntent().getStringExtra("imageUrl");

        // Load gambar ke ImageView
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(downloadImageView);
        } else {
            downloadImageView.setImageResource(R.drawable.tabler_download); // Placeholder default
        }

        // Tambahkan listener untuk tombol close
        closeDownload.setOnClickListener(v -> finish());
        downloadImage.setOnClickListener(v -> saveImageToFile());
    }
    private void saveImageToFile() {
        // Ambil drawable dari downloadImageView
        BitmapDrawable drawable = (BitmapDrawable) downloadImageView.getDrawable();
        if (drawable == null) {
            Toast.makeText(this, "No image to save", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap = drawable.getBitmap();

        // Siapkan konten untuk MediaStore
        ContentResolver contentResolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "downloaded_image_" + System.currentTimeMillis() + ".png");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/DownloadedImages"); // Disimpan di folder Pictures/DownloadedImages

        // Simpan gambar menggunakan MediaStore
        try {
            // Dapatkan URI tempat file akan disimpan
            Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (uri == null) {
                throw new Exception("Failed to create new MediaStore record.");
            }

            // Tulis bitmap ke file
            OutputStream outputStream = contentResolver.openOutputStream(uri);
            if (outputStream == null) {
                throw new Exception("Failed to get output stream.");
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();

            Toast.makeText(this, "Image saved successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
