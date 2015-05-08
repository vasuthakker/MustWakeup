package mustwakeup.galaxyvs.com.mustwakeup.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mustwakeup.galaxyvs.com.mustwakeup.Entities.AlarmEntity;
import mustwakeup.galaxyvs.com.mustwakeup.Helper.AlarmHelper;
import mustwakeup.galaxyvs.com.mustwakeup.R;

public class GridPuzzelActivity extends ActionBarActivity {

    private GridView gridPuzzel;

    private GridAdapter gridAdapter;
    private List<Integer> images;

    private int counter = 0;

    private AlarmEntity alarmEntity;
    private MediaPlayer mediaPlayer;
    private TextView txtCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        setContentView(R.layout.activity_grid_puzzel);

        gridPuzzel = (GridView) findViewById(R.id.gridpuzzel_gridview);
        txtCounter = (TextView) findViewById(R.id.grid_txtcounter);


        images = new ArrayList<Integer>();

        images.add(R.drawable.smily_1);
        images.add(R.drawable.smily_2);
        images.add(R.drawable.smily_3);
        images.add(R.drawable.smily_4);
        images.add(R.drawable.smily_5);
        images.add(R.drawable.smily_6);
        images.add(R.drawable.smily_7);
        images.add(R.drawable.smily_8);
        images.add(R.drawable.smily_9);


        txtCounter.setText(5 - counter + " puzzle remaining to stop the alarm");

        gridAdapter = new GridAdapter();

        gridPuzzel.setAdapter(gridAdapter);

        gridPuzzel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (counter < 5) {
                    if (images.get(position) == R.drawable.smily_6) {
                        txtCounter.setText(5 - counter + " puzzle remaining to stop the alarm");
                        counter++;
                        displayToast(true);
                    } else {
                        displayToast(false);
                    }
                    shuffle();
                } else {
                    DialogFragment dialog = new WokeUpDialog();
                    dialog.show(getSupportFragmentManager(), "awake");
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        alarmEntity = (AlarmEntity) getIntent().getSerializableExtra("alarm");
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, Uri.parse(alarmEntity.getTone()));
            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != 0) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (IOException e) {
            Log.e("Error", "IOException", e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    private void displayToast(boolean isRight) {
        Toast toast = new Toast(GridPuzzelActivity.this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 50);
        ImageView responseImageView = new ImageView(GridPuzzelActivity.this);
        if (isRight)
            responseImageView.setImageResource(R.drawable.right);
        else
            responseImageView.setImageResource(R.drawable.wrong);
        toast.setView(responseImageView);
        toast.show();
    }

    private void shuffle() {
        Collections.shuffle(images);
        gridAdapter.notifyDataSetChanged();
    }


    private int dpToPx(int dp) {
        Resources r = getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }


    private class WokeUpDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Dialog dialog = super.onCreateDialog(savedInstanceState);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_wakup);

            Button btnWokeUp = (Button) dialog.findViewById(R.id.dialog_btnWokeUp);
            Button btnMoreQuestion = (Button) dialog.findViewById(R.id.dialog_btnMoreQuestion);

            btnMoreQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shuffle();
                    counter = 0;
                    dismiss();
                }
            });

            btnWokeUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    if (alarmEntity.getWeekDays() == null || alarmEntity.getWeekDays().isEmpty())
                        alarmEntity.setIsActive(0);
                    AlarmHelper.updateAlarm(GridPuzzelActivity.this, alarmEntity);
                    dismiss();
                    GridPuzzelActivity.this.finish();
                }
            });

            return dialog;


        }
    }

    private class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return images.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView img;
            if (convertView == null)
                img = new ImageView(GridPuzzelActivity.this);
            else
                img = (ImageView) convertView;

            img.setImageResource(images.get(position));
            img.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(110), dpToPx(110)));

            return img;
        }
    }


}
