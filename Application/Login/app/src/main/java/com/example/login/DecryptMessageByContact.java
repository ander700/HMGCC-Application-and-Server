package com.example.login;

import android.os.AsyncTask;

import java.io.IOException;

public class DecryptMessageByContact extends AsyncTask<Void, Void, String> {

    private SMSActivity parent;
    private SMSTab target;

    public DecryptMessageByContact(SMSActivity parent, SMSTab target) {
        this.parent = parent;
        this.target = target;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String encryptedMessage = MainActivity.net.in.readObject().toString();
            String unecryptedMessage = MainActivity.encrypt.decrypt(encryptedMessage);
            System.out.println(encryptedMessage + " : " + unecryptedMessage);
            return unecryptedMessage;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
