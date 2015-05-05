package mustwakeup.galaxyvs.com.mustwakeup.Common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by viral.thakkar on 04-05-2015.
 */
public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "MustWake";

    private static final int DB_VERSION = 1;

    private static final String CREATE="CREATE TABLE ALARM(ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,TONE TEXT,ACTIVITY INTEGER,TIME INTEGER,ISACTIVE INTEGER,WEEKDAY TEXT)";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
