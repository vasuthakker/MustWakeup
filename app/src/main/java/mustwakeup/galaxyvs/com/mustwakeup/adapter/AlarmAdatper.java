package mustwakeup.galaxyvs.com.mustwakeup.adapter;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import mustwakeup.galaxyvs.com.mustwakeup.Activities.AlaramSetActivity;
import mustwakeup.galaxyvs.com.mustwakeup.Entities.AlarmEntity;
import mustwakeup.galaxyvs.com.mustwakeup.Helper.AlarmHelper;
import mustwakeup.galaxyvs.com.mustwakeup.R;
import mustwakeup.galaxyvs.com.mustwakeup.Receivers.AlarmReveiver;

/**
 * Created by viral.thakkar on 04-05-2015.
 */
public class AlarmAdatper extends BaseAdapter {

    private Activity activity;
    private List<AlarmEntity> alarms;
    private LayoutInflater inflater;

    public AlarmAdatper(Activity activity, List<AlarmEntity> alarms) {
        this.activity = activity;
        this.alarms = alarms;
        this.inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return alarms.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null)
            convertView = inflater.inflate(R.layout.alarm_item, null);

        final AlarmEntity alarm = alarms.get(position);
        LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.alarmitem_layout);
        TextView txtName = (TextView) convertView.findViewById(R.id.alarmitem_txtname);
        TextView txtTime = (TextView) convertView.findViewById(R.id.alarmitem_txttime);
        final ImageView imgActive = (ImageView) convertView.findViewById(R.id.alarmitem_imgactive);

        if (alarm != null) {
            txtName.setText(alarm.getName());
            txtTime.setText(new SimpleDateFormat("dd-MMM-yyy KK:mm a").format(alarm.getTime()));
            if (alarm.getIsActive() == 0)
                imgActive.setColorFilter(Color.BLACK);
            else
                imgActive.setColorFilter(Color.WHITE);

            imgActive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (alarm.getIsActive() == 0) {
                        alarm.setIsActive(1);
                        Calendar nowCal = Calendar.getInstance();

                        Calendar alarmCal = Calendar.getInstance();
                        alarmCal.setTimeInMillis(alarm.getTime());
                        alarmCal.set(Calendar.DAY_OF_YEAR, nowCal.get(Calendar.DAY_OF_YEAR));
                        alarmCal.set(Calendar.YEAR, nowCal.get(Calendar.YEAR));

                        long timeToSet = alarmCal.getTimeInMillis();
                        if (nowCal.getTimeInMillis() > alarmCal.getTimeInMillis()) {
                            alarmCal.add(Calendar.DAY_OF_YEAR,1);
                            timeToSet=alarmCal.getTimeInMillis();
                        }
                        alarm.setTime(timeToSet);
                        updateAlarm(alarm,timeToSet);

                    } else {
                        alarm.setIsActive(0);
                    }
                    notifyDataSetChanged();
                }
            });

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, AlaramSetActivity.class);
                    intent.putExtra("alarm", alarm);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                }
            });

            layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(activity)
                            .setTitle("Delete Alarm")
                            .setMessage("Are you sure you want to delete this alarm?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    AlarmHelper.deleteAlarm(activity, alarm);
                                    alarms.remove(position);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })

                            .show();

                    return true;


                }
            });
        }
        return convertView;
    }
    private void updateAlarm(AlarmEntity alarm,long timeToSet)
    {
        AlarmManager manager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);

        Intent inte = new Intent(activity, AlarmReveiver.class);
        inte.putExtra("alarm", alarm);
        inte.setAction("MustWakeup.Alarm");

        PendingIntent pi = PendingIntent.getBroadcast(activity, alarm.getId(), inte, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP, timeToSet, pi);

        Toast toast = Toast.makeText(activity.getApplicationContext(), "Alarm set for " + new SimpleDateFormat("dd-MMM-yyyy KK:mm a").format(timeToSet), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
}
