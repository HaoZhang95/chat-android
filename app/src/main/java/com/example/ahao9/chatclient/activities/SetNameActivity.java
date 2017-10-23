package com.example.ahao9.chatclient.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahao9.chatclient.components.ClientSocket;
import com.example.ahao9.chatclient.components.R;
import com.example.ahao9.chatclient.components.Utils;

import java.io.IOException;

/**
 * SetNameActivity will be displaying after login.
 * you have to be required to have a username before chatting, that why this activity exists
 */
public class SetNameActivity extends Activity{

    private static String username;
    private Button verifyName;
    private EditText editName;
    private TextView bar_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setname);

        verifyName = (Button)findViewById(R.id.btn_start);
        editName = (EditText)findViewById(R.id.edit_name);
        bar_name = (TextView) findViewById(R.id.bar_name);
        bar_name.setText("Set Name");

        verifyName.setOnClickListener(new veriftyNameListener());
    }

    /**
     * after click verify button, the typed username will be transmitted to server
     */
    class veriftyNameListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            username = editName.getText().toString();
            if (! username.equals("")){
                try{
                    ClientSocket.getDos().writeUTF("client_name" +"," +  username);
                }catch (IOException e){
                    Log.d("cuowu",e.getMessage());
                }
                verifyNameInServer();   //verify if the name alreadlt exist in server

            }else{
                Utils.showShortToast(SetNameActivity.this,"Username can not be empty!");
            }
        }
    }

    /**
     * @param react ---> the read value from server
     * react == "ok" ---> no duplicate name in server ---> jump to MainActivity
     * react == "error" ---> sorry, you got a big trouble. ---> show Toast "Username alreadly exists!"
     */
    String react;
    public void verifyNameInServer(){

        AsyncTask<Void,String,String> verify = new AsyncTask<Void, String, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                try {
                   react = ClientSocket.getDis().readUTF();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return react;
            }

            @Override
            protected void onPostExecute(String s) {
                if (s.equals("ok")){
                    Intent intent = new Intent();
                    intent.setClass(SetNameActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    Utils.showShortToast(SetNameActivity.this,"Welcome " + username);
                }else {
                    Utils.showShortToast(SetNameActivity.this,"Username alreadly exists!");
                }
            }
        };
        verify.execute();
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        SetNameActivity.username = username;
    }

}
