package com.example.ahao9.chatclient.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.ahao9.chatclient.components.ClientSocket;
import com.example.ahao9.chatclient.components.R;
import com.example.ahao9.chatclient.components.Utils;

import java.io.IOException;

/**
 * rating activity
 */
public class RatingActivity extends Activity {
    private static final String RATE_COMM = "rating";

    private Button ratingBtn;
    private RatingBar ratingBar;
    private TextView title_name_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        ratingBtn = (Button) findViewById(R.id.rating_btn);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        title_name_txt = (TextView) findViewById(R.id.bar_name);
        title_name_txt.setText("Rating");

        //send rating score to server
        ratingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showShortToast(RatingActivity.this,"Stars: " + ratingBar.getRating() + " Thank you for your support!");
                try {
                    ClientSocket.getDos().writeUTF(RATE_COMM + "," + ratingBar.getRating() + "," + SetNameActivity.getUsername());
                    ClientSocket.getDos().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });

    }
}

