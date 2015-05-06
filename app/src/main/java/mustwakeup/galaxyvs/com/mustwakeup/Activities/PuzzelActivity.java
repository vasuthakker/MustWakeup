package mustwakeup.galaxyvs.com.mustwakeup.Activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;

import mustwakeup.galaxyvs.com.mustwakeup.Entities.AlarmEntity;
import mustwakeup.galaxyvs.com.mustwakeup.Helper.AlarmHelper;
import mustwakeup.galaxyvs.com.mustwakeup.R;


public class PuzzelActivity extends ActionBarActivity {

    private TextView lblFirst;
    private TextView lblSecond;
    private TextView lblSign;
    private EditText txtAnswer;
    private Button btnCheck;
    private TextView txtCounter;

    private Random random = new Random();

    int first, second;

    int signPos = 0;

    private String[] signs = {"÷", "X", "+", "-"};

    private Ringtone ring;

    private MediaPlayer mediaPlayer;

    private int maxLimit = 10;

    private AlarmEntity alarmEntity;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzel);


        lblFirst = (TextView) findViewById(R.id.first);
        lblSecond = (TextView) findViewById(R.id.Second);
        lblSign = (TextView) findViewById(R.id.SignTextView);
        btnCheck = (Button) findViewById(R.id.chkAnswer);
        txtAnswer = (EditText) findViewById(R.id.answerEditText);
        txtCounter = (TextView) findViewById(R.id.remainingCounter);

        txtCounter.setText(5 - counter + " puzzle remaining to stop the alarm");

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });

        txtAnswer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    checkAnswer();
                }
                txtAnswer.requestFocus();
                return false;
            }
        });

        generateNewNunbres();

        alarmEntity = (AlarmEntity) getIntent().getSerializableExtra("alarm");
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, Uri.parse(alarmEntity.getTone()));
            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (IOException e) {
            Log.e("Error", "IOException", e);
        }
    }

    private void checkAnswer() {
        String val = txtAnswer.getText().toString();
        boolean isAnswerSuccess = false;
        long value = 0;
        if (val == null || val.isEmpty()) {
            txtAnswer.setError("Please enter value to check");
            return;
        } else {
            value = Integer.parseInt(val);
        }
        txtAnswer.setText("");
        String msg = "Wrong answer";
        counter++;
        switch (signPos) {
            case 0:
                try {
                    if (value == (first / second)) {
                        //  msg = "Division success";
                        isAnswerSuccess = true;
                    } else
                        counter--;
                } catch (ArithmeticException e) {
                    isAnswerSuccess = true;
                }
                break;
            case 1:
                if (value == (first * second)) {
                    //  msg = "Multiplication success";
                    isAnswerSuccess = true;
                } else
                    counter--;
                break;
            case 2:
                if (value == (first + second)) {
                    //  msg = "Addition success";
                    isAnswerSuccess = true;
                } else
                    counter--;
                break;
            case 3:
                if (value == (first - second)) {
                    // msg = "Subtraction success";
                    isAnswerSuccess = true;
                } else
                    counter--;
                break;
        }
        if (counter < 5) {
            //   Toast.makeText(PuzzelActivity.this, msg + "  " + NumberProcessor.getWordRepresentation(value), Toast.LENGTH_SHORT).show();
            if (isAnswerSuccess) {
                generateNewNunbres();
                txtCounter.setText(5 - counter + " puzzle remaining to stop the alarm");
                displayToast(true);
            } else
                displayToast(false);
        } else {
            DialogFragment dialog = new WokeUpDialog();
            dialog.show(getSupportFragmentManager(), "awake");
        }


    }

    private void displayToast(boolean isRight) {
        Toast toast = new Toast(PuzzelActivity.this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 50);
        ImageView responseImageView = new ImageView(PuzzelActivity.this);
        if (isRight)
            responseImageView.setImageResource(R.drawable.right);
        else
            responseImageView.setImageResource(R.drawable.wrong);
        toast.setView(responseImageView);
        toast.show();
    }


    private void generateNewNunbres() {
        first = random.nextInt(maxLimit);
        second = random.nextInt(maxLimit);
        lblFirst.setText(String.valueOf(first));
        lblSecond.setText(String.valueOf(second));

        signPos = random.nextInt(3);
        lblSign.setText(signs[signPos]);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_puzzel, menu);
        return true;
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

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onBackPressed() {

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
                    counter = 0;
                    generateNewNunbres();
                    txtCounter.setText(5 - counter + " puzzle remaining to stop the alarm");
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
                    AlarmHelper.updateAlarm(PuzzelActivity.this, alarmEntity);
                    dismiss();
                    PuzzelActivity.this.finish();
                }
            });
            return dialog;
        }
    }
}
