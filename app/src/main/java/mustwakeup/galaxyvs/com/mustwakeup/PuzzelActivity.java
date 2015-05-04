package mustwakeup.galaxyvs.com.mustwakeup;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;


public class PuzzelActivity extends FragmentActivity {

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

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        if (value == (first / second)) {
                            msg = "Division success";
                            isAnswerSuccess = true;
                        } else
                            counter--;
                        break;
                    case 1:
                        if (value == (first * second)) {
                            msg = "Multiplication success";
                            isAnswerSuccess = true;
                        } else
                            counter--;
                        break;
                    case 2:
                        if (value == (first + second)) {
                            msg = "Addition success";
                            isAnswerSuccess = true;
                        } else
                            counter--;
                        break;
                    case 3:
                        if (value == (first - second)) {
                            msg = "Subtraction success";
                            isAnswerSuccess = true;
                        } else
                            counter--;
                        break;
                }
                if (counter < 5) {
                    Toast.makeText(PuzzelActivity.this, msg + "  " + NumberProcessor.getWordRepresentation(value), Toast.LENGTH_SHORT).show();
                    if (isAnswerSuccess) {
                        generateNewNunbres();
                        txtCounter.setText(5 - counter + " puzzle remaining to stop the alarm");
                    }
                } else {
                    if (ring != null)
                        ring.stop();
                    DialogFragment dialog=new WokeUpDialog();
                    dialog.show(getSupportFragmentManager(),"wokeup");
                }
            }
        });

        generateNewNunbres();

        String ringUri = Utils.getValue(this, "ring");
        if (ringUri != null && !ringUri.isEmpty()) {
            ring = RingtoneManager.getRingtone(this, Uri.parse(ringUri));
            ring.play();
        }

    }

    private void generateNewNunbres() {
        first = random.nextInt(100);
        second = random.nextInt(100);
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

    private class WokeUpDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Dialog dialog = super.onCreateDialog(savedInstanceState);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_wakup);

            Button btnWokeUp=(Button)dialog.findViewById(R.id.dialog_btnWokeUp);
            Button btnMoreQuestion=(Button)dialog.findViewById(R.id.dialog_btnMoreQuestion);

            btnMoreQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    counter=0;
                    generateNewNunbres();
                    txtCounter.setText(5 - counter + " puzzle remaining to stop the alarm");
                    dismiss();
                }
            });

            btnWokeUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            return dialog;


        }
    }
}
