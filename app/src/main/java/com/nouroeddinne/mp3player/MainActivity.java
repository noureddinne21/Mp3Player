package com.nouroeddinne.mp3player;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_READ_STORAGE = 1;
//    private static final String PATH = Environment.getExternalStorageDirectory().getPath()+"/";
    private static final String PATH = "storage/emulated/0/Music/";
    public static List<String> list = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_READ_STORAGE);
        }else {

            // Check if the Android version is Android 10 or higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10 (API level 29) or higher
                getAllMp3FilesUsingMediaStore();
            } else {
                // Lower than Android 10
                //getAllAudio();
                getAllMp3FilesUsingMediaStore();

            }

            adapter = new AudioAdapter(list,MainActivity.this);
            recyclerView.setAdapter(adapter);

        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AudioAdapter(list, this);






















    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode ==REQUEST_READ_STORAGE && permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getAllAudio();
        }else {

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getAllAudio() {

        if(PATH != null){
            File file = new File(PATH);
            File [] files = file.listFiles();
            for(File f : files){
                Log.d("TAGA", "getAllAudio: "+f);
                if (f.isDirectory()){
                    scanDirectory(f);
                }else{
                    String path = file.getAbsolutePath();
                    if (path.endsWith(".mp3")){
                        list.add(path);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(this, "add", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    public void scanDirectory(File directory){

        if (directory != null){
            File [] files2 = directory.listFiles();
            if (files2 != null){
                for(File f2 : files2){
                    Log.d("TAGA", "2 getAllAudio: "+f2);
                    if (f2.isDirectory()){
                        scanDirectory(f2);
                    }else{
                        String path = directory.getAbsolutePath();
                        if (path.endsWith(".mp3")){
                            list.add(path);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(this, "add", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }

    }


    public void getAllMp3FilesUsingMediaStore() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME };

        Cursor cursor = contentResolver.query(uri, projection, MediaStore.Audio.Media.IS_MUSIC + " != 0", null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                list.add(filePath);
                Log.d("MP3File", "Path: " + filePath + ", Name: " + displayName);
            }
            cursor.close();
        } else {
            Log.d("MP3File", "No MP3 files found.");
        }
    }


















}