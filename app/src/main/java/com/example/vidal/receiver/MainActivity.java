package com.example.vidal.receiver;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper updDB;
    private SQLiteDatabase mDb;
    Context pcontext;
    EditText number;
    TextView fio;
   /* TextView device;
    TextView snImei;
    TextView dateInv;
    TextView description;
    TextView dateExp;
    TextView status;
    TextView garantee;
    */
    Spinner spinner;

    public String value = "";
    static List<String> list = new ArrayList<>();

    String ext = ".db";
    String dir = "storage/sdcard0/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findFiles(dir, ext);

        pcontext = getApplicationContext();
        updDB = new DatabaseHelper(getApplicationContext());
        number = (EditText)findViewById(R.id.number);
        fio = (TextView) findViewById(R.id.tvFIO1);
       /* device = (TextView) findViewById(R.id.tvDevice1);
        snImei = (TextView) findViewById(R.id.tvSNimei1);
        dateInv = (TextView) findViewById(R.id.tvDateInv1);
        description = (TextView) findViewById(R.id.tvDescription1);
        dateExp = (TextView) findViewById(R.id.tvDateExp1);
        status = (TextView) findViewById(R.id.tvStatus1);
        garantee = (TextView) findViewById(R.id.tvGarantee1);
        */


        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

       spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

       /* Timer myTimer = new Timer(); // Создаем таймер
        final Handler uiHandler = new Handler();
        myTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onClick1();
                    }
                });
            }
        }, 20L, 60L * 1000);
       */
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.find:{
               value = spinner.getSelectedItem().toString();

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Name",value);
                editor.apply();

                 try {
                    updDB.updateDataBase();
                } catch (IOException mIOException) {
                    throw new Error("UnableToUpdateDatabase");
                }

                try {
                    mDb = updDB.getWritableDatabase();
                } catch (SQLException mSQLException) {
                    throw mSQLException;
                }


                  if(number != null) {
                    String stringNumber = number.getText().toString();
                    mDb = updDB.openToFind();

                    String query = "SELECT * FROM application WHERE clientphone = ?";
                    Cursor cursor = mDb.rawQuery(query, new String[] {"+38" + stringNumber});

                    String info = "";

                    String nameBrand = "";
                    String model = "";
                    String clientName = "";
                    String strSnImei = "";
                    String strDateInv = "";
                    String strDateExp = "";
                    String strDateExp = "";
                    String strDescritption = "";
                    String strStatus = "";
                    String strStatusName = "";
                    String strGaranty = "";
                    String strGarantyID = "";
                    String strNameProizv = "";
                    String strNameProizvID = "";
                    int intClient = cursor.getColumnIndex("clientname");
                    int intBrandModel = cursor.getColumnIndex("id_brand");
                    int intModel = cursor.getColumnIndex("model");
                    int intStatus = cursor.getColumnIndex("id_status");
                    int intSnImei = cursor.getColumnIndex("SN");
                    int intDateInv = cursor.getColumnIndex("date");
                    int intDateExp = cursor.getColumnIndex("date_out");
                    int intDescription = cursor.getColumnIndex("clientmessage");
                    int intGaranty = cursor.getColumnIndex("id_Garanty");
                    int intIdProizv = cursor.getColumnIndex("id_proizv");
                    cursor.moveToFirst();

                    while (!cursor.isAfterLast()) {

                        strNameProizvID = cursor.getString(intIdProizv);
                        String queryProizv = "SELECT * FROM proizv WHERE id = ?";
                        Cursor cursorProizv = mDb.rawQuery(queryProizv, new String[] {strNameProizvID});
                        int intProizv = cursorProizv.getColumnIndex("name");
                        cursorProizv.moveToFirst();

                        model = cursor.getString(intBrandModel);
                        strStatus = cursor.getString(intStatus);
                        strGaranty = cursor.getString(intGaranty);
                        clientName = cursor.getString(intClient);
                        strSnImei = cursor.getString(intSnImei) + "\n";
                        strDateInv = cursor.getString(intDateInv) + "\n";
                        strDateExp = cursor.getString(intDateExp) + "\n";
                        strDescritption += cursor.getString(intDescription) + "\n";

                        String query2 = "SELECT * FROM brand WHERE id = ?";
                        Cursor cursor2 = mDb.rawQuery(query2, new String[] {model});
                        int intBrand = cursor2.getColumnIndex("brandname");
                        cursor2.moveToFirst();

                        String queryStatus = "SELECT * FROM status WHERE id = ?";
                        Cursor cursorStatus = mDb.rawQuery(queryStatus, new String[] {strStatus});
                        int intStatusName = cursorStatus.getColumnIndex("statusname");
                        cursorStatus.moveToFirst();

                        String queryGaranty = "SELECT * FROM Garanty WHERE id = ?";
                        Cursor cursorGaranty = mDb.rawQuery(queryGaranty, new String[] {strGaranty});
                        int intGarantyId = cursorGaranty.getColumnIndex("garanty");
                        cursorGaranty.moveToFirst();


                        while (!cursorProizv.isAfterLast()) {
                            strNameProizv = cursorProizv.getString(intProizv) + " ";
                            cursorProizv.moveToNext();

                            nameBrand = cursor2.getString(intBrand)  + " " + strNameProizv +  " " + cursor.getString(intModel) + "\n";
                            cursor2.moveToNext();

                            strStatusName = cursorStatus.getString(intStatusName) + "\n";
                            cursorStatus.moveToNext();

                            strGarantyID = cursorGaranty.getString(intGarantyId) + "\n";
                            cursorGaranty.moveToNext();
                            info += "ФИО: " + clientName + "\n" + "Устройство: " + nameBrand + "\n" + "SN IMEI: " + strSnImei + "\n"
                                    + "Дата приема: " + strDateInv + "\n" + "Описание неисправности устройства: " + strDescritption + "\n"
                                    + "Дата выдачи: " + strDateExp + "\n" + "Статус: " + strStatusName + "\n" + "Гарантия: " + strGarantyID + "\n" + "\n";


                        }
                        cursorProizv.close();
                        cursor2.close();
                        cursorStatus.close();
                        cursorGaranty.close();

                        cursor.moveToNext();
                    }
                    cursor.close();

                    fio.setText(info);
                    /* fio.setText(clientName);
                    device.setText(nameBrand);
                    snImei.setText(strSnImei);
                    dateInv.setText(strDateInv);
                    description.setText(strDescritption);
                    dateExp.setText(strDateExp);
                    status.setText(strStatusName);
                    garantee.setText(strGarantyID); */


                }

            }

                break;
        }
    }

    private static void findFiles(String dir, String ext) {

        File file = new File(dir);
        if(!file.exists()) System.out.println(dir + " папка не существует");
        File[] listFiles = file.listFiles(new MyFileNameFilter(ext));
        if(listFiles.length == 0){
            System.out.println(dir + " не содержит файлов с расширением " + ext);
        }else{
            int i=0;
            for(File f : listFiles){
                System.out.println("Файл: " + dir + f.getName());

                list.add(dir + f.getName());
            }
        }
    }


    public static class MyFileNameFilter implements FilenameFilter {

        private String ext;

        public MyFileNameFilter(String ext){
            this.ext = ext.toLowerCase();
        }
        @Override
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(ext);
        }
    }

}
