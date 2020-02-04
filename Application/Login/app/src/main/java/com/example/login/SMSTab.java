package com.example.login;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.view.Display;
import android.view.PointerIcon;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SMSTab extends LinearLayout {

    private boolean sender;
    public TextView txtMessage;
    private final SMSActivity parent;
    private SMSTab self;
    private String message;

    public SMSTab(Context context, final boolean sender, final String mesasge, final SMSActivity parent) {
        super(context);
        this.sender = sender;
        this.parent = parent;
        this.message = mesasge;
        this.txtMessage= new TextView(this.getContext());
        this.setupWidgets();

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Future Upgrades to encryption.
                MainActivity.net.sendMessage("SMSD " + parent.phoneNumberText.getText().toString()+ " " + txtMessage.getText().toString());
                SMSDecrypt smsDecrypt = new SMSDecrypt(parent, self);
                smsDecrypt.execute();
                */
                if (sender) {
                    MainActivity.net.sendMessage("SMSD " + parent.phoneNumberText.getText() + " " + txtMessage.getText().toString());
                    DecryptMessageByContact decryptMessageByContact = new DecryptMessageByContact(parent, self);
                    decryptMessageByContact.execute();
                }
                else {
                    String message = (MainActivity.encrypt.decrypt(txtMessage.getText().toString()) == null)
                            ? txtMessage.getText().toString() : MainActivity.encrypt.decrypt(txtMessage.getText().toString());
                    txtMessage.setText(message);
                }
            }
        });
    }

    private void setupWidgets() {
        this.setPadding(10, 10, 10, 10);
        if (this.sender) {
            this.setBackgroundColor(Color.parseColor("#FFFFFF"));
            this.txtMessage.setTextSize(20);

        } else {
            this.setBackgroundColor(Color.parseColor("#96a3b7"));
            this.txtMessage.setTextSize(20);
        }
        this.txtMessage.setText(this.message);
        this.addView(this.txtMessage);
        this.self = this;
    }

}
