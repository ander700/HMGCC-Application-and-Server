package com.example.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Signup extends AppCompatActivity {

    private Network net;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //Intent i = getIntent();
        this.net = MainActivity.net;
    }
    public void signUpValidate(View v) {
        EditText email = (EditText) findViewById(R.id.emailInput);
        EditText pass = (EditText) findViewById(R.id.passwordInput);
        EditText username = (EditText) findViewById(R.id.userNameInput);
        EditText confPass = (EditText) findViewById(R.id.confirmPasswordInput);
        EditText phone = (EditText) findViewById(R.id.phoneInput);
        String pattern = "[0-9]";
        if((email.getText().toString().trim().length() <= 0) || (username.getText().toString().trim().length() <= 0) ||(phone.getText().toString().trim().length() <= 0)||(confPass.getText().toString().trim().length() <= 0) || (pass.getText().toString().trim().length() <=0))
        {
            System.out.println("oi oi");
            showToast("length");
        }
        else if(!pass.getText().toString().equals(confPass.getText().toString())){
            showToast("notSame");
        }
        else {
            sendSignUpMessage(v);
        }
    }
    public void sendSignUpMessage(View v){

        EditText email = (EditText) findViewById(R.id.emailInput);
        EditText pass = (EditText) findViewById(R.id.passwordInput);
        EditText username = (EditText) findViewById(R.id.userNameInput);
        EditText confPass = (EditText) findViewById(R.id.confirmPasswordInput);
        EditText phone = (EditText) findViewById(R.id.phoneInput);

        this.net.sendMessage("signup " + username.getText().toString() + " " + email.getText().toString() + " " +
                pass.getText().toString() + " " + phone.getText().toString());
        recieveAsync signupTask = new recieveAsync(this);
        signupTask.execute(new String[] {"Sign Up", "Successfully Signed Up", "Unable to Sign Up"});
    }
    public void showToast(String error){
        if (error.equals("length")){
            Toast toast = Toast.makeText(Signup.this,"Fields cannot be blank",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL,0,-200);
            toast.show();
        }
        if(error.equals("notSame")){
            Toast toast = Toast.makeText(Signup.this,"Passwords not the same",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL,0,-200);
            toast.show();
        }
    }

}
