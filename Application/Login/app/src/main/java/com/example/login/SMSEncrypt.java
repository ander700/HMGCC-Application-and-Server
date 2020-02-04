package com.example.login;

import android.os.AsyncTask;

import java.io.IOException;

public class SMSEncrypt extends AsyncTask<Void, Void, String> {

    private SMSActivity parent;

    public SMSEncrypt(SMSActivity parent) {
        this.parent = parent;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String message = MainActivity.net.in.readObject().toString();
            System.out.println(message);
            return message;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return " ";
    }

    @Override
    protected void onPostExecute(String message) {
        super.onPostExecute(message);
        //parent.sendSMSMessage(message);
    }
}
