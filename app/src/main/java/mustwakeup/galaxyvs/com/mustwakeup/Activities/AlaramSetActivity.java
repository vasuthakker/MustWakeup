package mustwakeup.galaxyvs.com.mustwakeup.Activities;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import mustwakeup.galaxyvs.com.mustwakeup.Entities.AlarmEntity;
import mustwakeup.galaxyvs.com.mustwakeup.Helper.AlarmHelper;
import mustwakeup.galaxyvs.com.mustwakeup.R;
import mustwakeup.galaxyvs.com.mustwakeup.Receivers.AlarmReveiver;
import mustwakeup.galaxyvs.com.mustwakeup.Utils.Utils;


public class AlaramSetActivity extends FragmentActivity {

    private TextView alarmTime;
    private Handler handler = new Handler();
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("KK:mm a");
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
    private LinearLayout ringPicker;
    private TextView txtRingName;
    private TextView txtActivityName;
    private LinearLayout layoutActivityPick;
    private EditText txtAlarmName;
    private ImageView imgSave;
    private long timeToSet = System.currentTimeMillis();
    private Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    private TextView txtTitle;
    private int typeActivity = 1;
    private Button btnCancel;
    private Button btnSave;
    private TextView txtDate;

    private TextView txtMon;
    private TextView txtTue;
    private TextView txtWed;
    private TextView txtThu;
    private TextView txtFri;
    private TextView txtSat;
    private TextView txtSun;

    private LinearLayout weekLayout;

    int green = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alaram_set);

        green = getResources().getColor(R.color.dark_green_2);

        txtTitle = (TextView) findViewById(R.id.txt_alarmname);
        imgSave = (ImageView) findViewById(R.id.img_save);
        alarmTime = (TextView) findViewById(R.id.alarmTime);
        txtRingName = (TextView) findViewById(R.id.txtRingName);
        txtAlarmName = (EditText) findViewById(R.id.txtAlaramName);
        layoutActivityPick = (LinearLayout) findViewById(R.id.layoutActivityPick);
        txtActivityName = (TextView) findViewById(R.id.txtActivityName);
        alarmTime.setText(getTime(System.currentTimeMillis()));
        btnCancel = (Button) findViewById(R.id.alarmset_btncancel);
        btnSave = (Button) findViewById(R.id.alarmset_btnsave);
        txtDate = (TextView) findViewById(R.id.alarmset_txtdate);
        weekLayout = (LinearLayout) findViewById(R.id.weeklayout);

        txtMon = (TextView) findViewById(R.id.weeklayout_txtmon);
        txtTue = (TextView) findViewById(R.id.weeklayout_txttue);
        txtWed = (TextView) findViewById(R.id.weeklayout_txtwed);
        txtThu = (TextView) findViewById(R.id.weeklayout_txtthu);
        txtFri = (TextView) findViewById(R.id.weeklayout_txtfri);
        txtSat = (TextView) findViewById(R.id.weeklayout_txtsat);
        txtSun = (TextView) findViewById(R.id.weeklayout_txtsun);

        txtMon.setOnClickListener(weekDayListener);
        txtTue.setOnClickListener(weekDayListener);
        txtWed.setOnClickListener(weekDayListener);
        txtThu.setOnClickListener(weekDayListener);
        txtFri.setOnClickListener(weekDayListener);
        txtSat.setOnClickListener(weekDayListener);
        txtSun.setOnClickListener(weekDayListener);

        ringPicker = (LinearLayout) findViewById(R.id.btnsoundpick);

        alarmTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "picker");
            }
        });

        ringPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select ringtone for notifications:");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                startActivityForResult(intent, 19);
            }
        });

        layoutActivityPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BottomSheet.Builder(AlaramSetActivity.this).title(getString(R.string.choose_activity)).sheet(R.menu.menu_alaram_set).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.activity_math:
                                typeActivity = 1;
                                txtActivityName.setText(getString(R.string.activity_math));
                                break;
                            case R.id.activity_pic:
                                typeActivity = 2;
                                txtActivityName.setText(getString(R.string.activity_pic));
                                break;
                        }
                    }
                }).show();
            }
        });

        final AlarmEntity alarmEntity = (AlarmEntity) getIntent().getSerializableExtra("alarm");

        if (alarmEntity != null) {
            alarmTime.setText(getTime(alarmEntity.getTime()));
            txtAlarmName.setText(alarmEntity.getName());
            txtTitle.setText(alarmEntity.getName());
            txtRingName.setText(RingtoneManager.getRingtone(this, Uri.parse(alarmEntity.getTone())).getTitle(this));
            String activity = getString(R.string.activity_math);
            if (alarmEntity.getActivityType() == 2)
                activity = getString(R.string.activity_pic);
            txtActivityName.setText(activity);
            txtDate.setText(dateFormatter.format(alarmEntity.getTime()));
            timeToSet = alarmEntity.getTime();
        } else {
            handler.postDelayed(getTime, 60000);
            txtRingName.setText(RingtoneManager.getRingtone(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)).getTitle(this));
            txtDate.setText(dateFormatter.format(System.currentTimeMillis()));
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setting alarm

                AlarmEntity alarm = new AlarmEntity();
                if (alarmEntity == null) {

                    alarm.setIsActive(1);
                    alarm.setTime(timeToSet);
                    alarm.setActivityType(typeActivity);
                    alarm.setTone(ringUri.toString());
                    alarm.setName(txtAlarmName.getText().toString());

                    AlarmHelper.insertAlarm(AlaramSetActivity.this, alarm);
                } else {
                    alarm.setId(alarmEntity.getId());
                    alarm.setIsActive(1);
                    alarm.setTime(timeToSet);
                    alarm.setActivityType(typeActivity);
                    alarm.setTone(ringUri.toString());
                    alarm.setName(txtAlarmName.getText().toString());
                    AlarmHelper.updateAlarm(AlaramSetActivity.this, alarm);
                }

                StringBuilder weekDay = new StringBuilder();
                for (int i = 0; i < weekLayout.getChildCount(); i++) {
                    TextView tv = (TextView) weekLayout.getChildAt(i);
                    if (tv.getCurrentTextColor() == Color.WHITE) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.DAY_OF_WEEK, i + 1);

                        Calendar alarmCal = Calendar.getInstance();
                        alarm.setTime(timeToSet);

                        cal.set(Calendar.HOUR_OF_DAY, alarmCal.get(Calendar.HOUR_OF_DAY));
                        cal.set(Calendar.MINUTE, alarmCal.get(Calendar.MINUTE));

                        if (cal.getTimeInMillis() < System.currentTimeMillis())
                            cal.add(Calendar.WEEK_OF_MONTH, 1);

                        if (weekDay.length() > 0)
                            weekDay.append(",");
                        weekDay.append(tv.getText().toString());


                        setAlarm(alarm, cal.getTimeInMillis());

                    }
                }

                if (weekDay.length() > 0) {
                    alarm.setWeekDays(weekDay.toString());
                    AlarmHelper.updateAlarm(AlaramSetActivity.this, alarm);
                }


            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setAlarm(AlarmEntity alarm, long timeToSet) {
        AlarmManager manager = (AlarmManager) AlaramSetActivity.this.getSystemService(Context.ALARM_SERVICE);

        Intent inte = new Intent(AlaramSetActivity.this, AlarmReveiver.class);
        inte.putExtra("alarm", alarm);
        inte.setAction("MustWakeup.Alarm");

        PendingIntent pi = PendingIntent.getBroadcast(AlaramSetActivity.this, alarm.getId(), inte, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP, timeToSet, pi);

        Toast toast = Toast.makeText(AlaramSetActivity.this.getApplicationContext(), "Alarm set for " + new SimpleDateFormat("dd-MMM-yyyy KK:mm a").format(timeToSet), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();

        finish();
    }

    private View.OnClickListener weekDayListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView weekDay = (TextView) v;

            if (weekDay.getCurrentTextColor() == green)
                weekDay.setTextColor(Color.WHITE);
            else
                weekDay.setTextColor(green);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            ringUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            Utils.putValue(this, "ring", ringUri.toString());
            Toast.makeText(this, "Alarm Ringtone selected", Toast.LENGTH_SHORT).show();
            Ringtone ring = RingtoneManager.getRingtone(this, ringUri);
            txtRingName.setText(ring.getTitle(this.getApplicationContext()));
        }
    }

    private Runnable getTime = new Runnable() {
        @Override
        public void run() {
            alarmTime.setText(getTime(System.currentTimeMillis()));
            handler.postDelayed(this, 60000);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alaram_set, menu);
        return true;
    }

    private String getTime(long miliSeconds) {
        return timeFormatter.format(miliSeconds);
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(getTime);
        super.onPause();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            if (timeToSet > 0) {
                c.setTimeInMillis(timeToSet);
            }
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));

        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            handler.removeCallbacks(getTime);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cal.set(Calendar.MINUTE, minute);

            String ampm = "AM";
            if (hourOfDay > 12) {
                ampm = "PM";
                hourOfDay -= 12;
            }
            handler.removeCallbacks(getTime);
            alarmTime.setText(hourOfDay + ":" + minute + " " + ampm);

            if (cal.getTimeInMillis() < System.currentTimeMillis()) {
                cal.add(Calendar.DAY_OF_YEAR, 1);
            }


            Calendar nowTime = Calendar.getInstance();
            nowTime.add(Calendar.MINUTE, 1);
            if (timeToSet < nowTime.getTimeInMillis()) {
                nowTime.add(Calendar.DAY_OF_YEAR, 1);
                timeToSet = nowTime.getTimeInMillis();
                txtDate.setText(dateFormatter.format(timeToSet));
            } else {
                txtDate.setText(dateFormatter.format(System.currentTimeMillis()));
            }

            timeToSet = cal.getTimeInMillis();
        }
    }


}
