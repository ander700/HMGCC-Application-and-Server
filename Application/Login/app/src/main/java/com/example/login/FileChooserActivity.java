package com.example.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

public class FileChooserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);
        File dir = getFilesDir();
        File[] files = dir.listFiles();
        LinearLayout scroll = findViewById(R.id.scroll_layout);
        for (int i=0; i<files.length; i++) {
            scroll.addView(new FileContainer(this, files[i]));
        }
    }
    


}
