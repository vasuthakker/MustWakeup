package mustwakeup.galaxyvs.com.mustwakeup.Activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;

import mustwakeup.galaxyvs.com.mustwakeup.Entities.AlarmEntity;
import mustwakeup.galaxyvs.com.mustwakeup.Helper.AlarmHelper;
import mustwakeup.galaxyvs.com.mustwakeup.R;
import mustwakeup.galaxyvs.com.mustwakeup.adapter.AlarmAdatper;

public class AlarmListActivity extends ActionBarActivity {

    private ListView alarmListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alarm_list);

        alarmListView = (ListView) findViewById(R.id.alarmlistview);

        ActionBar aBar = this.getSupportActionBar();

        aBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_green)));


    }

    @Override
    protected void onResume() {
        super.onResume();
        final List<AlarmEntity> alarms = AlarmHelper.getAlarams(this, null, null, null, null, null);
        if (alarms != null && !alarms.isEmpty())
        {
            alarmListView.setAdapter(new AlarmAdatper(this, alarms));

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_alarm) {
            Intent intent = new Intent(AlarmListActivity.this, AlaramSetActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
