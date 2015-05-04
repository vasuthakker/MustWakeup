package mustwakeup.galaxyvs.com.mustwakeup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReveiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent inte = new Intent(context, PuzzelActivity.class);
        inte.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(inte);
    }
}
