package mustwakeup.galaxyvs.com.mustwakeup;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class AlaramSetActivity extends FragmentActivity {

    private TextView alarmTime;
    private Handler handler = new Handler();
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("KK:mm a");
    private LinearLayout ringPicker;
    private TextView txtRingName;
    private TextView txtActivityName;
    private LinearLayout layoutActivityPick;
    private EditText txtAlarmName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alaram_set);

        new TestAsync().execute();
        alarmTime = (TextView) findViewById(R.id.alarmTime);
        txtRingName = (TextView) findViewById(R.id.txtRingName);
        txtAlarmName = (EditText) findViewById(R.id.txtAlaramName);
        layoutActivityPick = (LinearLayout) findViewById(R.id.layoutActivityPick);
        txtActivityName=(TextView)findViewById(R.id.txtActivityName);

        alarmTime.setText(getTime(System.currentTimeMillis()));
        handler.postDelayed(getTime, 1000);

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
                                txtActivityName.setText(getString(R.string.activity_math));
                                break;
                            case R.id.activity_pic:
                                txtActivityName.setText(getString(R.string.activity_pic));
                                break;
                        }
                    }
                }).show();
            }
        });



        txtRingName.setText(RingtoneManager.getRingtone(this,RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)).getTitle(this));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri ringUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
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
            handler.postDelayed(this, 1000);
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
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));

        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

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

            // setting alarm
            AlarmManager manager = (AlarmManager) AlaramSetActivity.this.getSystemService(Context.ALARM_SERVICE);
            Intent inte = new Intent(AlaramSetActivity.this, AlarmReveiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(AlaramSetActivity.this, 10, inte, PendingIntent.FLAG_UPDATE_CURRENT);
            manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);

            Toast toast = Toast.makeText(AlaramSetActivity.this.getApplicationContext(), "Alarm set for " + getTime(cal.getTimeInMillis()), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        }
    }

    private class TestAsync extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPostExecute(String aVoid) {
            Toast.makeText(AlaramSetActivity.this, "" + aVoid, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(Void... params) {
            return Utils.sendRequest("http://192.168.110.49:8080/WSDemo/MyApp/Demo/HelloWorld", null);
        }


    }
}
