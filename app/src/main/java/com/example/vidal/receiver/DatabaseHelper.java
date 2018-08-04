package com.example.vidal.receiver;

/**
 * Created by Vidal on 27.01.2018.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    public String value1;
    private static String DB_NAME = "sqlite.db";
    private static String DB_NAME1 = "sqlite1.db";
    private static String DB_PATH = "";
    private static final int DB_VERSION = 2;

    private SQLiteDatabase mDataBase;
    private final Context mContext;
    private boolean mNeedUpdate = false;


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);



        if (android.os.Build.VERSION.SDK_INT >= 17)
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        else
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        this.mContext = context;

        MainActivity a = new MainActivity();
        value1 = a.value;

        copyDataBase();

        this.getReadableDatabase();
    }

    public void updateDataBase() throws IOException {
        if (mNeedUpdate) {
            File dbFile = new File(DB_PATH + DB_NAME);
            if (dbFile.exists())
                dbFile.delete();

            copyDataBase();

            mNeedUpdate = false;
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() {
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    private void copyDBFile() throws IOException {

        InputStream mInput = mContext.getResources().openRawResource(R.raw.sqlite);
        OutputStream mOutput = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0)
            mOutput.write(mBuffer, 0, mLength);
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public SQLiteDatabase open(String nameBase)throws SQLException {
        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        //String name = preferences.getString("Name", "");

        return SQLiteDatabase.openDatabase(nameBase, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public SQLiteDatabase openToFind()throws SQLException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String name = preferences.getString("Name", "");

        return SQLiteDatabase.openDatabase(name, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            mNeedUpdate = true;
    }
}