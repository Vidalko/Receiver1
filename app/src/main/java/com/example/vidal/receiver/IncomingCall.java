package com.example.vidal.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.CountDownTimer;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import java.io.IOException;

/**
 * Created by Vidal on 14.12.2017.
 */

public class IncomingCall extends BroadcastReceiver {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    String[] arrayData = new String[] { "sqlite.db", "sqlite1.db" };
    int intNumberData = 2;

    Context pcontext;
    public void onReceive(Context context, Intent intent) {

        pcontext = context;


        try {
            // TELEPHONY MANAGER class object to register one listner
            TelephonyManager tmgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            //Create Listner
            MyPhoneStateListener PhoneListener = new MyPhoneStateListener();

            // Register listener for LISTEN_CALL_STATE
            tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        } catch (Exception e) {
            Log.e("Phone Receive Error", " " + e);
        }

    }
    private class MyPhoneStateListener extends PhoneStateListener {


        public void onCallStateChanged(int state, String incomingNumber) {

            Log.d("MyPhoneListener",state+"   incoming no:"+incomingNumber);

            if (state == 1) {
                String nameBase = "storage/sdcard0/";
                String info = "";

                for (int i = 0; i < intNumberData; i++) {

                    mDBHelper = new DatabaseHelper(pcontext);

                    try {
                        mDBHelper.updateDataBase();
                    } catch (IOException mIOException) {
                        throw new Error("UnableToUpdateDatabase");
                    }

                    try {
                        mDb = mDBHelper.getWritableDatabase();
                    } catch (SQLException mSQLException) {
                        throw mSQLException;
                    }


                    mDb = mDBHelper.open(nameBase + arrayData[i]);

                    String query = "SELECT * FROM application WHERE clientphone = ?";
                    Cursor cursor = mDb.rawQuery(query, new String[]{incomingNumber});
                    String nameBrand = "";
                    String brandID = "";
                    String strNameProizv = "";
                    String strNameProizvID = "";
                    String model = "";

                    int intClient = cursor.getColumnIndex("clientname");
                    int intIdBrand = cursor.getColumnIndex("id_brand");
                    int intIdProizv = cursor.getColumnIndex("id_proizv");
                    int intModelBrand = cursor.getColumnIndex("model");
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        brandID = cursor.getString(intIdBrand);
                        model = cursor.getString(intModelBrand);

                        String query2 = "SELECT * FROM brand WHERE id = ?";
                        Cursor cursor2 = mDb.rawQuery(query2, new String[]{brandID});
                        int intBrand = cursor2.getColumnIndex("brandname");
                        cursor2.moveToFirst();
                        while (!cursor2.isAfterLast()) {
                            nameBrand = cursor2.getString(intBrand) + " ";

                            cursor2.moveToNext();
                        }
                        cursor2.close();

                        strNameProizvID = cursor.getString(intIdProizv);
                        String queryProizv = "SELECT * FROM proizv WHERE id = ?";
                        Cursor cursorProizv = mDb.rawQuery(queryProizv, new String[] {strNameProizvID});
                        int intProizv = cursorProizv.getColumnIndex("name");
                        cursorProizv.moveToFirst();
                        while (!cursorProizv.isAfterLast()) {
                            strNameProizv = cursorProizv.getString(intProizv) + " ";
                            cursorProizv.moveToNext();

                        }
                        cursorProizv.close();

                        info += cursor.getString(intClient) + " " + nameBrand + " " + strNameProizv + " " + model;

                        cursor.moveToNext();
                    }
                    cursor.close();
                }
                   // mDb = null;

                    String msg = "Incomming Number: " + incomingNumber + " " + "Name: " + info;

                    final Toast tag = Toast.makeText(pcontext, msg, Toast.LENGTH_SHORT);
                    tag.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    tag.show();
                    new CountDownTimer(15000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            tag.show();
                        }

                        public void onFinish() {
                            tag.show();
                        }

                    }.start();
                //mDb.close();


            }
        }
    }
}
