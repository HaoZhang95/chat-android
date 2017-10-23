package com.example.ahao9.chatclient.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahao9.chatclient.components.ClientSocket;
import com.example.ahao9.chatclient.components.R;
import com.example.ahao9.chatclient.components.Utils;

import java.io.*;
import java.net.Socket;

/**
 * login activity,attempt to connect server by typed ip and port number
 */
public class LoginActivity extends Activity {

    private int port;
    private String ip = null;
    private Button loginButton;
    private Button exitButton;
    private EditText ipEdit;
    private EditText portText;
    private TextView bar_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.login);
        exitButton= (Button) findViewById(R.id.exit);
        ipEdit= (EditText) findViewById(R.id.username);
        portText = (EditText) findViewById(R.id.password);
        bar_name = (TextView) findViewById(R.id.bar_name);
        bar_name.setText("MyChatClient");

        //get ip and port number, attempt to connect server
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ip = ipEdit.getText().toString();
                port = Integer.parseInt(portText.getText().toString());

                connect();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });
    }

    /**
     * avoid blocking UI thread, using AsyncTask to login
     * login successed ---> jump to SetNameActivity
     *       failed    ---> stay and show Toast
     */
    public void connect(){

        AsyncTask<Void,String,Void> login = new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    ClientSocket.setIpAddress(ip);
                    ClientSocket.setPortNum(port);
                    ClientSocket.getClientSocket();
                    publishProgress("@success");
                } catch (Exception e){
                    Utils.showShortToast(LoginActivity.this,"Login failed, Please check your ip and port number.");
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                try{
                    if (values[0] == "@success"){
                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this,SetNameActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    super.onProgressUpdate(values);
                } catch (Exception e){
                    Utils.showShortToast(LoginActivity.this,"Login failed, Please check your ip and port number.");
                }

            }
        };
        login.execute();
    };
}

