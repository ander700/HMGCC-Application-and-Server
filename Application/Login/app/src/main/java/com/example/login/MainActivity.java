package com.example.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private Button testValidButton;
    Button mainMenu;
    static Network net;
    static aesEncrypt encrypt;
    public String length = "length";
    public String setIp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File textImageDir = getDir("users", Context.MODE_PRIVATE);
        textImageDir.mkdirs();
        //mainMenu = (Button) findViewById(R.id.button2);
        testValidButton = (Button) findViewById(R.id.signupButton);
        this.net = new Network("10.0.2.2", 8192);
        this.net.connect();

//        mainMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mainMenu(v);
//            }
//        });
    }
    public void showToast(String error){
        if (error.equals("length")){
            Toast toast = Toast.makeText(MainActivity.this,"Fields cannot be blank",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL,0,-200);
            toast.show();
        }
    }
public void verify(View v){
        Intent intent = new Intent(this,VerifyActivity.class);
        startActivity(intent);
}

    public void ipActivity(View v){
        Intent intent = new Intent(this,setIPActivity.class);
        startActivity(intent);

    }

    public void fileChooser(View v){
        Intent intent = new Intent(this,FileChooserActivity.class);
        startActivity(intent);

    }
    public void mainMenu(View v){
        Intent intent = new Intent(this,MainMenu.class);
        startActivity(intent);
    }

    public void openSignupActivity(View v){
        Intent intent = new Intent(this,Signup.class);
        //intent.putExtra("net", net);
        startActivity(intent);
    }
    public void deleteText(){
        EditText emailEdit = (EditText) findViewById(R.id.emailTextBox);

        emailEdit.setText("");
    }

    public void login(View v) {
        //String error = "error";
        EditText checkEditEmail = (EditText) findViewById(R.id.emailTextBox);
        EditText checkEditPass = (EditText) findViewById(R.id.passwordTextBox);
        Intent suc = new Intent(this, MainMenu.class);
        Intent err = new Intent(this, MainActivity.class);
        //err.putExtra("error",error);

        Button login = (Button) findViewById(R.id.loginButton);
        if((checkEditEmail.getText().toString().trim().length() <= 0) || (checkEditPass.getText().toString().trim().length() <= 0)) {
            showToast(length);
        }
        else{

            this.net.sendMessage("login " + checkEditEmail.getText().toString() + " " + checkEditPass.getText().toString());
            recieveAsync loginTask = new recieveAsync(this);
            loginTask.execute(new String[] {"Login", "Successfully Logged In", "Unable to Log In"});
        }
    }



}
