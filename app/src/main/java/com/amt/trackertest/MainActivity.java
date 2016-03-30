package com.amt.trackertest;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amt.trackertest.httpTask.HttpHandler;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {



    Button btnLogin;
    Button btnCreateSession;
    EditText etUserMail;

    String userMail = "";
    String user_id = "";
    String session_id = "";

    public static final String PREFS_NAME = "GPS_PREFS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnCreateSession = (Button) findViewById(R.id.btnCreateSession);
        etUserMail = (EditText)findViewById(R.id.etUserMail);

        final LogWriter lw = new LogWriter();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userMail = etUserMail.getText().toString();

                if (!userMail.equals("")) {
                    new HttpHandler() {
                        @Override
                        public void onResponse(String result) {
                            user_id = result;
                            String temp = "";
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                            String formattedDate = df.format(c.getTime());
                            if (!result.equals("")) {
                                temp = "Login OK!";
                                lw.writeToFile("[" + formattedDate + "] " + "Login OK. Session ID: " + session_id + "\\r\\n");
                            } else {
                                temp = "Login Error!";
                                lw.writeToFile("[" + formattedDate + "] " + "Login Error.");
                            }
                            DialogFragment back_dialog = new GeneralDialogFragment();
                            Bundle args = new Bundle();
                            args.putString("msg", temp);
                            back_dialog.setArguments(args);
                            back_dialog.show(getFragmentManager(), "Info msg");

                        }
                    }.login(userMail,"test");
                }else{
                    DialogFragment back_dialog = new GeneralDialogFragment();
                    Bundle args = new Bundle();
                    args.putString("msg", getResources().getString(R.string.userMissingMsg));
                    back_dialog.setArguments(args);
                    back_dialog.show(getFragmentManager(), "Info msg");
                }
            }
        });

        btnCreateSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpHandler() {
                    @Override
                    public void onResponse(String result) {
                        session_id = result;
                        String temp = "";
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        String formattedDate = df.format(c.getTime());
                        if (!result.equals("")){
                            temp = "CreateSession OK!";
                            lw.writeToFile("[" + formattedDate + "] " + "CreateSession OK. Session ID: " + session_id);
                        }else{
                            temp = "CreateSession Error!";
                            lw.writeToFile("[" + formattedDate + "] " + "CreateSession Error.");
                        }
                        DialogFragment back_dialog = new GeneralDialogFragment();
                        Bundle args = new Bundle();
                        args.putString("msg", temp);
                        back_dialog.setArguments(args);
                        back_dialog.show(getFragmentManager(), "Info msg");

                    }
                }.createSession(user_id);
            }
        });

    }
}