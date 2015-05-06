package mustwakeup.galaxyvs.com.mustwakeup.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import mustwakeup.galaxyvs.com.mustwakeup.Activities.GridPuzzelActivity;
import mustwakeup.galaxyvs.com.mustwakeup.Activities.NormalAlarmActivity;
import mustwakeup.galaxyvs.com.mustwakeup.Activities.PuzzelActivity;
import mustwakeup.galaxyvs.com.mustwakeup.Entities.AlarmEntity;
import mustwakeup.galaxyvs.com.mustwakeup.Helper.AlarmHelper;

public class AlarmReveiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmEntity alarm = (AlarmEntity) intent.getSerializableExtra("alarm");
        alarm=AlarmHelper.getAlarm(context,alarm.getId());
        if (alarm != null) {

            updateAlarm(context,alarm);

            Intent inte = null;
            if (alarm.getActivityType() == 1)
                inte = new Intent(context, PuzzelActivity.class);
            else if(alarm.getActivityType() == 2)
                inte = new Intent(context, GridPuzzelActivity.class);
            else if(alarm.getActivityType() == 3)
                inte = new Intent(context, NormalAlarmActivity.class);

            inte.putExtra("alarm", alarm);
            inte.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(inte);
        }
    }

    private void updateAlarm(Context context,AlarmEntity alarm) {
        String[] days = alarm.getWeekDays().split(",");
        StringBuilder weekDays = new StringBuilder();
        for (String day : days) {
            if (day.equals(getDay()))
                continue;
            if (weekDays.length() > 0)
                weekDays.append(",");
            weekDays.append(day);
        }
        alarm.setWeekDays(weekDays.toString());
        AlarmHelper.updateAlarm(context,alarm);
    }

    private String getDay() {
        String day = "su";
        Calendar cal = Calendar.getInstance();
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                day = "mo";
                break;
            case Calendar.TUESDAY:
                day = "tu";
                break;
            case Calendar.WEDNESDAY:
                day = "we";
                break;
            case Calendar.THURSDAY:
                day = "th";
                break;
            case Calendar.FRIDAY:
                day = "fr";
                break;
            case Calendar.SATURDAY:
                day = "sa";
                break;
            default:
                day = "su";
                break;
        }
        return day;
    }
}
