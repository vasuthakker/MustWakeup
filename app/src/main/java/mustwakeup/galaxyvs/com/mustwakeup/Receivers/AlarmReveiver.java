package mustwakeup.galaxyvs.com.mustwakeup.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import mustwakeup.galaxyvs.com.mustwakeup.Activities.GridPuzzelActivity;
import mustwakeup.galaxyvs.com.mustwakeup.Activities.PuzzelActivity;
import mustwakeup.galaxyvs.com.mustwakeup.Entities.AlarmEntity;

public class AlarmReveiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmEntity alarm = (AlarmEntity) intent.getSerializableExtra("alarm");

        if (alarm != null) {

            Intent inte=null;
            if (alarm.getActivityType() == 1)
                inte = new Intent(context, PuzzelActivity.class);
            else
                inte = new Intent(context, GridPuzzelActivity.class);
            inte.putExtra("alarm", alarm);
            inte.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(inte);
        }
    }
}
