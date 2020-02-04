package com.example.login;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.media.Image;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

import static android.provider.SyncStateContract.Columns.DATA;

public class ImageViewActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 42;
    private ImageView displayImage;
    private EditText imagePath;
    private static final int EDIT_REQUEST_CODE = 44;

    private String currentURI = null;
    private String dataToWrite = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        this.displayImage = findViewById(R.id.showImage);
        this.imagePath = findViewById(R.id.pathText);
    }


    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.imagefile,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch (id) {
            case R.id.encryptButton:
                this.encryptFile(); break;
            case R.id.decryptButton:
                this.decryptFile(); break;
            case R.id.openFile:
                this.showImage();
        }

        return super.onOptionsItemSelected(item);
    }

    public void showImage(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    private void encryptFile() {
        if (this.currentURI == null) return;

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent,EDIT_REQUEST_CODE);
    }

    private void decryptFile() {
        if (this.currentURI == null) return;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            Uri uri = null;
            String displayName = "";
            if (resultData != null) {
                uri = resultData.getData();
            }
            Cursor cursor = getContentResolver()
                    .query(uri, null, null, null, null, null);

            try {
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    System.out.println(displayName);
                }
                cursor.close();


                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor imgCursor = getContentResolver().query(uri,filePathColumn,null,null,null);
                imgCursor.moveToFirst();
                int columnIndex = imgCursor.getColumnIndex(filePathColumn[0]);
                String filePath = imgCursor.getString(columnIndex);
                imgCursor.close();

                copyFile(new File(filePath), new File(getFilesDir() + "/" + displayName));





                this.displayImage.setImageURI(Uri.fromFile(new File(getFilesDir() + "/" + displayName)));
                this.imagePath.setText(uri.getPath());
                this.currentURI = uri.getPath();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void copyFile(File src,File dst) throws IOException{

        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try{
            inChannel.transferTo(0,inChannel.size(),outChannel);
        }
        finally{
            if(inChannel!=null)
                inChannel.close();
            if (outChannel!=null)
                outChannel.close();
        }
    }

    private String getRealPath(Uri uri, String fileName) {
        String[] sections = uri.getPath().split("/");
        String finalPath = "";
        for (int i=0; i<sections.length-1; i++) {
            finalPath += sections[i] + "/";
        }
        return finalPath + fileName;
    }


}
