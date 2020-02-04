package com.example.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class setIPActivity extends AppCompatActivity {
    public String newIp = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_ip2);


    }

    public void setIp(View v){
        EditText ipInput = findViewById(R.id.ipText);
        MainActivity.net = new Network(ipInput.getText().toString(), 8192);
    }

}
