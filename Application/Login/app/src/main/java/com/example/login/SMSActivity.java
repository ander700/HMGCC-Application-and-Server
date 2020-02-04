package com.example.login;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static android.provider.CalendarContract.CalendarCache.URI;

public class SMSActivity extends AppCompatActivity {
    EditText phoneNumberText,messageText;
    public static final int MY_PERMISSIONS_REQUEST_SEND_SMS =1;
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private static final String TAG = MainMenu.class.getSimpleName();
    private Uri uriContact;
    private String contactID;
    public String contactNumber;
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPend,deliveredPend;
    BroadcastReceiver smsSentReceiver,smsDeliveredReceiver;
    ArrayList<String> smsMessagesList = new ArrayList<String>();
    ArrayList<String> smsSentList = new ArrayList<String>();

    private LinearLayout scroll_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        this.scroll_layout = findViewById(R.id.scroll_layout);
        phoneNumberText = findViewById(R.id.phoneNumberText);
        messageText = findViewById(R.id.messageText);
        sentPend = PendingIntent.getBroadcast(this,0,new Intent(SENT),0);
        deliveredPend = PendingIntent.getBroadcast(this,0,new Intent(DELIVERED),0);
    }

    public void getContact(View v){
        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
    }


    public void showSms(String number){
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"),null,null,null,null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        int sentId = smsInboxCursor.getColumnIndex("_id");

        String checkNumber = number;


        if(indexBody<0||!smsInboxCursor.moveToFirst())return;
        System.out.println(number + " the number of contact");

        do{
           if(number.equals(smsInboxCursor.getString(indexAddress))){
                String str = smsInboxCursor.getString(indexBody);
                smsMessagesList.add(str);
            }
        }while(smsInboxCursor.moveToNext());

        smsInboxCursor.close();


        Cursor smsSentCursor = contentResolver.query(Uri.parse("content://sms/sent"),null,null,null,null);


        int sIndexBody = smsSentCursor.getColumnIndex("body");
        int sIndexAddress = smsSentCursor.getColumnIndex("address");
        if(sIndexBody<0||!smsSentCursor.moveToFirst())return;
        do{
            if(number.equals(smsSentCursor.getString(sIndexAddress))){
                String sStr = smsSentCursor.getString(sIndexBody);
                smsSentList.add(sStr);
            }
        }while(smsSentCursor.moveToNext());
        smsSentCursor.close();
        System.out.println(smsSentList);

        Collections.reverse(smsMessagesList);
        Collections.reverse(smsSentList);
        ArrayList<SMSTab> messages = new ArrayList<SMSTab>();

        this.scroll_layout.removeAllViews();

        int max = (smsMessagesList.size() >= smsSentList.size()) ? smsMessagesList.size() : smsSentList.size();
        for (int i=0; i<max; i++) {
            try {
                this.scroll_layout.addView(new SMSTab(getApplicationContext(), true, smsMessagesList.get(i), this));
            } catch (IndexOutOfBoundsException e) {
                // Do Nothing
            }
            try {
                this.scroll_layout.addView(new SMSTab(getApplicationContext(), false, smsSentList.get(i), this));
            } catch (IndexOutOfBoundsException e) {
                // Do Nothing
            }
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            Log.d(TAG, "Response: " + data.toString());
            uriContact = data.getData();
            retrieveContactName();
            retrieveContactNumber();
            System.out.println(contactNumber);
            phoneNumberText = findViewById(R.id.phoneNumberText);
            phoneNumberText.setText(contactNumber);
            if(ContextCompat.checkSelfPermission(getBaseContext(),"android.permission.READ_SMS")==PackageManager.PERMISSION_GRANTED){
                showSms(contactNumber);
            }
            else{
                final int REQUEST_CODE_ASK_PERMISSIONS = 123;
                ActivityCompat.requestPermissions(SMSActivity.this,new String[]{"android.permission.READ_SMS"},REQUEST_CODE_ASK_PERMISSIONS);
            }
        }
    }
    private void retrieveContactName() {
        String contactName = null;

        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();

        Log.d(TAG, "Contact Name: " + contactName);
    }

    private void retrieveContactNumber() {

        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        Log.d(TAG, "Contact ID: " + contactID);

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        cursorPhone.close();
        Log.d(TAG, "Contact Phone Number: " + contactNumber);

    }
    @Override
    public void onResume(){
        super.onResume();
        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch(getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(SMSActivity.this,"Message sent!",Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(SMSActivity.this,"Generic failure", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(SMSActivity.this,"Error no service", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(SMSActivity.this,"Null PDU", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(SMSActivity.this,"Radio off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch(getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(SMSActivity.this,"SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(SMSActivity.this,"SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        registerReceiver(smsSentReceiver,new IntentFilter(SENT));
        registerReceiver(smsDeliveredReceiver,new IntentFilter(DELIVERED));
    }
    public void onPause(){
        super.onPause();
        unregisterReceiver(smsDeliveredReceiver);
        unregisterReceiver(smsSentReceiver);
    }

    public void encryptSMS(View v) {
        String message = messageText.getText().toString();
        MainActivity.net.sendMessage("SMSE " + phoneNumberText.getText().toString() + " " + message);
        SMSEncrypt smsEncrypt = new SMSEncrypt(this);
        smsEncrypt.execute();
    }

    public void sendSMSMessage(View v){
        String message = MainActivity.encrypt.encrypt(messageText.getText().toString());
        String phoneNumber = phoneNumberText.getText().toString();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
        else{
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber,null,message,sentPend,deliveredPend);
        }


    }

}
