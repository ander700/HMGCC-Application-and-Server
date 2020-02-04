package com.example.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class VerifyActivity extends AppCompatActivity {

    private Network net;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        this.net = MainActivity.net;
    }

    public void verify(View v) {
        EditText verificationCode = (EditText) findViewById(R.id.verifyInput);
        this.net.sendMessage("verify " + verificationCode.getText().toString().trim());
        recieveAsync verifyTask = new recieveAsync(this);
        verifyTask.execute(new String[] {"Verify", "Successfully Verified Account", "Unable to Verify Account"});
    }

}
