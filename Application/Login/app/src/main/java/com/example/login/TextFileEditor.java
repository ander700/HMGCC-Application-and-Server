package com.example.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TextFileEditor extends AppCompatActivity {

    private EditText txtData;
    private EditText txtFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_file_editor);
        this.txtData = findViewById(R.id.txtFileData);
        this.txtFilePath = findViewById(R.id.txtFilePath);
    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.textfile,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch (id) {
            case R.id.encryptButton:
                this.encryptText(); break;
            case R.id.decryptButton:
                this.decryptText(); break;
            case R.id.saveButton:
                this.saveFile(); break;
            case R.id.openFile:
                this.openFile(); break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void encryptText() {
        this.txtData.setText(MainActivity.encrypt.encrypt(this.txtData.getText().toString()));
    }

    public void decryptText() {
        this.txtData.setText(MainActivity.encrypt.decrypt(this.txtData.getText().toString()));
    }

    public void saveFile() {
        if (this.txtFilePath.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please Enter a Name for the File", Toast.LENGTH_LONG).show();
            return;
        } else this.writeFile(this.txtFilePath.getText().toString(),
                            this.txtData.getText().toString());
    }

    private void writeFile(String fileName, String fileContents){
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(getFilesDir().getAbsolutePath() + "/" + fileName + ".txt");
            System.err.println(getFilesDir().getAbsolutePath() + "/" + fileName + ".txt");
            System.err.println(fileContents);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openFile() {
        String fileContents = this.readFile(getFilesDir().getAbsolutePath() + "/" + this.txtFilePath.getText().toString() + ".txt");
        this.txtData.setText(fileContents);
    }

    private String readFile(String file){
        try {
            FileInputStream in = new FileInputStream(new File(file));
            int val;
            StringBuffer stringBuffer = new StringBuffer();
            while ((val = in.read()) > 0) {
                stringBuffer.append((char) val);
            }
            in.close();
            return stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }


}
