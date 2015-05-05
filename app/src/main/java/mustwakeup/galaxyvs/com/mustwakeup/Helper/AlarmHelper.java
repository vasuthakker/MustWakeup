package mustwakeup.galaxyvs.com.mustwakeup.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import mustwakeup.galaxyvs.com.mustwakeup.Common.DBHandler;
import mustwakeup.galaxyvs.com.mustwakeup.Entities.AlarmEntity;

/**
 * Created by viral.thakkar on 04-05-2015.
 */
public class AlarmHelper {

    public static final String TABLE_NAME = "ALARM";

    public static final String KEY_ID = "ID";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_TONE = "TONE";
    public static final String KEY_ACTIVITY = "ACTIVITY";
    public static final String KEY_TIME = "TIME";
    public static final String KEY_ISACTIVE = "ISACTIVE";

    public static final String KEY_WEEKDAY="WEEKDAY";

    public static void insertAlarm(Context context, AlarmEntity alarmEntity) {
        DBHandler dbHandler = new DBHandler(context);
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        try {
            alarmEntity.setId((int) db.insert(TABLE_NAME, null, getCVForAlarm(alarmEntity)));
        } finally {
            db.close();
        }
    }

    private static ContentValues getCVForAlarm(AlarmEntity alarmEntity) {
        ContentValues cv = new ContentValues();

        cv.put(KEY_NAME, alarmEntity.getName());
        cv.put(KEY_TONE, alarmEntity.getTone());
        cv.put(KEY_ACTIVITY, alarmEntity.getActivityType());
        cv.put(KEY_TIME, alarmEntity.getTime());
        cv.put(KEY_ISACTIVE, alarmEntity.getIsActive());
        cv.put(KEY_WEEKDAY,alarmEntity.getWeekDays());
        return cv;
    }

    public static void updateAlarm(Context context, AlarmEntity alarmEntity) {
        DBHandler dbHandler = new DBHandler(context);
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        try {
            db.update(TABLE_NAME, getCVForAlarm(alarmEntity), KEY_ID + "=?", new String[]{String.valueOf(alarmEntity.getId())});
        } finally {
            db.close();
        }
    }

    public static void deleteAlarm(Context context, AlarmEntity alarm) {
        DBHandler dbHandler = new DBHandler(context);
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        try {
            db.delete(TABLE_NAME, KEY_ID + "=?", new String[]{String.valueOf(alarm.getId())});
        } finally {
            db.close();
        }
    }

    public static List<AlarmEntity> getAlarams(Context context, String selection, String[] args, String groupBy, String having, String orderBy) {
        List<AlarmEntity> alarms = null;
        DBHandler dbHandler = new DBHandler(context);
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        try {
            Cursor cursor = db.query(TABLE_NAME, null, selection, args, groupBy, having, orderBy);

            while (cursor.moveToNext()) {
                if (alarms == null)
                    alarms = new ArrayList<AlarmEntity>();

                alarms.add(getEntityFromCursor(cursor));
            }
            cursor.close();

        } finally {
            db.close();
        }
        return alarms;
    }

    private static AlarmEntity getEntityFromCursor(Cursor cursor) {
        AlarmEntity alarm = new AlarmEntity();

        alarm.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
        alarm.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        alarm.setActivityType(cursor.getInt(cursor.getColumnIndex(KEY_ACTIVITY)));
        alarm.setTone(cursor.getString(cursor.getColumnIndex(KEY_TONE)));
        alarm.setIsActive(cursor.getInt(cursor.getColumnIndex(KEY_ISACTIVE)));
        alarm.setTime(cursor.getLong(cursor.getColumnIndex(KEY_TIME)));
        alarm.setWeekDays(cursor.getString(cursor.getColumnIndex(KEY_WEEKDAY)));

        return alarm;
    }


}
