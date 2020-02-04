package com.example.login;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

public class FileContainer extends LinearLayout {

    private ImageView fileIcon;
    private TextView fileName;
    private TextView fileSize;

    public FileContainer(AppCompatActivity context, File file) {
        super(context);
        this.setPadding(0, 120, 0, 0);
        this.fileIcon = new ImageView(context);
        this.fileIcon.setImageResource(R.drawable.txticon);
        this.fileIcon.setMaxWidth(30);
        this.fileIcon.setMaxHeight(30);
        this.fileIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        this.fileName = new TextView(context);
        this.fileName.setText(file.getName());
        this.fileName.setTextSize(24);
        this.fileName.setPadding(50 ,70, 50 , 0);
        this.fileName.setTextColor(Color.WHITE);
        this.fileSize = new TextView(context);
        this.fileSize.setText(Long.toString(file.length()));
        this.fileSize.setTextSize(24);
        this.fileSize.setPadding(0 ,70, 0 , 0);
        this.fileSize.setTextColor(Color.WHITE);
        this.addView(this.fileIcon);
        this.addView(this.fileName);
        this.addView(this.fileSize);
    }

    private void setImage() {

    }
}
