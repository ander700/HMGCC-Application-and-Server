package com.example.login;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainMenu extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


    }
    public void fileChooser(View v){
        Intent intent = new Intent(this,FileChooserActivity.class);
        startActivity(intent);
    }
    public void smsStart(View v){
        Intent smsIntent = new Intent(MainMenu.this,SMSActivity.class);
        startActivity(smsIntent);
    }

    public void encryptStart(View v){
        Intent intent = new Intent(MainMenu.this,TextFileEditor.class);
        startActivity(intent);
    }

    public void imageStart(View v){
        Intent intent = new Intent(MainMenu.this,ImageViewActivity.class);
        startActivity(intent);
    }

}



