package com.example.login;

import android.os.AsyncTask;

import java.io.IOException;

public class SMSDecrypt extends AsyncTask<Void, Void, String> {

    private SMSActivity parent;
    private SMSTab target;

    public SMSDecrypt(SMSActivity parent, SMSTab target) {
        this.parent = parent;
        this.target = target;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String encryptedMessage = MainActivity.net.in.readObject().toString();
            String unencryptedMessage = MainActivity.encrypt.decrypt(encryptedMessage);
            return unencryptedMessage;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String message) {
        super.onPostExecute(message);
        this.target.txtMessage.setText(message);
    }
}
