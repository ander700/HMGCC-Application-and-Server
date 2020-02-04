package com.example.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.IOException;

public class recieveAsync extends AsyncTask<String, Void, Boolean> {

    private AppCompatActivity parent;
    private String sucMes, errMes, opType, return_message;

    public recieveAsync(AppCompatActivity parent) {
        this.parent = parent;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        return_message = null;
        this.sucMes = strings[1];
        this.errMes = strings[2];

        try {
            return_message = MainActivity.net.in.readObject().toString();
            System.err.println("Message Received : " + return_message);
            if (parse_message(return_message)) return true;
            else return false;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean status) {
        super.onPostExecute(status);
        if (status) Toast.makeText(this.parent, this.sucMes, Toast.LENGTH_LONG).show();
        else Toast.makeText(this.parent, this.errMes, Toast.LENGTH_LONG).show();
        this.executeIntent(status);
    }

    private void executeIntent(boolean status) {
        Intent intent = null;
        switch (this.opType.toUpperCase()) {
            case "LOGIN":
                intent = status ? new Intent(this.parent.getApplicationContext(), MainMenu.class) : new Intent(this.parent.getApplicationContext(), MainActivity.class);
                break;
            case "SIGN UP":
                intent = status ? new Intent(this.parent.getApplicationContext(), VerifyActivity.class) : new Intent(this.parent.getApplicationContext(), Signup.class);
                break;
            case "VERIFY":
                intent = status ? new Intent(this.parent.getApplicationContext(), MainActivity.class) : new Intent(this.parent.getApplicationContext(), VerifyActivity.class);
                break;
        }
        if (intent != null) this.parent.startActivity(intent);
    }

    private boolean parse_message(String message) {
        String finalMessage = "";
        if (!message.toUpperCase().startsWith("LOGIN")) finalMessage = MainActivity.encrypt.decrypt(message);
        else finalMessage = message;
        System.out.println("Server - " + finalMessage);
        String reqType = finalMessage.split(" ")[0].trim().toUpperCase();
        switch(reqType) {
            case "LOGIN":
                if (finalMessage.split(" ")[1].trim().toUpperCase().equals("SUCCESS")) {
                    MainActivity.encrypt = new aesEncrypt(finalMessage.split(" ")[2].trim());
                    this.opType = "Login";
                    return true;
                }
                else return false;
            case "SIGNUP":
                if (finalMessage.split(" ")[1].trim().toUpperCase().equals("SUCCESS")) {
                    this.opType = "Sign Up";
                    return true;
                }
                else return false;
            case "VERIFY":
                System.err.println(finalMessage);
                if (finalMessage.split(" ")[1].trim().toUpperCase().equals("SUCCESS")) {
                    this.opType = "Verify";
                    return true;
                }
                else return false;
        }
        return false;
    }
}

