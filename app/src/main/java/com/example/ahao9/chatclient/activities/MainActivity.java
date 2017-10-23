package com.example.ahao9.chatclient.activities;

import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.ahao9.chatclient.components.ChatHistory;
import com.example.ahao9.chatclient.components.ChatMessage;
import com.example.ahao9.chatclient.components.ClientSocket;
import com.example.ahao9.chatclient.components.Person;
import com.example.ahao9.chatclient.components.R;
import com.example.ahao9.chatclient.components.UpdatedUserList;
import com.example.ahao9.chatclient.components.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * MainActivity holds:
 * (a) Fragment1 ---> group chat  (b) Fragment2 ---> PersonList (3) Fragment3 ---> SettingList
 */
public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener{

    private static final String TAG = "cuowu";
    private static AsyncTask<Void,String,Void> receive_text;

    private static RadioGroup group;
    private static RadioButton r2;
    private ChatList fragment1;
    private PersonList fragment2;
    private SettingList fragment3;
    private TextView bar_name;
    private ImageView iv;
    private long voice_size;
    private File voice_rece ;
    //private static String voiceFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bar_name = (TextView) findViewById(R.id.bar_name);
        iv = (ImageView) findViewById(R.id.iv);
        group = (RadioGroup) findViewById(R.id.group);
        r2 = (RadioButton) findViewById(R.id.contacts);

        r2.setChecked(true);
        showDefaultPage();
        group.setOnCheckedChangeListener(this);

        ReceiveFromServer();
        Log.d(TAG, "onCreate: " + receive_text);
    }

    /**
     * start using ClientSocket.getDis().readUTF() to receive data from server in this activity
     * because this activity contains 3 fragments,
     * after receiving, transfer message to corresponding fragment to update UI
     */
    public void ReceiveFromServer(){
         receive_text = new AsyncTask<Void, String, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    String line = null;
                    while((line = ClientSocket.getDis().readUTF()) != null){

                        if (line.startsWith("text_download")){    //message is text,and post it to its subscriber
                            String data = line.substring(13);
                            ChatMessage message = new ChatMessage(data,ChatMessage.TEXT_RECEIVE,Utils.getTime());
                            EventBus.getDefault().post(message);
                        } else if (line.startsWith("voice_download")){  //message is voice file
                            String len = line.substring(14);
                            voice_size = Long.parseLong(len);
                            try {
                                int len1,length = 0;
                                byte voice_storage[] = new byte[1024];

                                String filename = new Date().getTime() + "_Received" + ".amr";
                                voice_rece = new File("/sdcard/ChatVoices",filename);

                                FileOutputStream fout = new FileOutputStream(voice_rece);
                                if (!voice_rece.exists()) {
                                    voice_rece.createNewFile();
                                }
                                while((len1 = ClientSocket.getDis().read(voice_storage)) != -1) {
                                    fout.write(voice_storage, 0, len1);
                                    length += len1;
                                    if (length >= voice_size) {
                                        break;
                                    }
                                }
                                Log.d("cuowu", "doInBackground: store voice file to sdcard successfully,size ---> " + length);
                                fout.close();

                                ChatMessage message = new ChatMessage(voice_rece,ChatMessage.VOICE_RECEIVE,Utils.getTime());
                                EventBus.getDefault().post(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }else if (line.startsWith("list_update")) {     //message is a updated userlist
                            String u_list = line.substring(11);
                            UpdatedUserList uplist = new UpdatedUserList(u_list);
                            EventBus.getDefault().post(uplist);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            };
        };
    };

    /**
     * add default fragment2 into fragment_container
     * set Fragment2 ---> PersonList as default fragment
     */
    private void showDefaultPage() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fragment2 = new PersonList();
        bar_name.setText("Chat List");
        transaction.add(R.id.fragment_container,fragment2,"f2");
        iv.setImageResource(R.drawable.tab_tab3_normal);
        transaction.commit();
    }

    /**
     * @param count ---> To prevent touch the back key on the phone mistakenly,User have one change for false touch
     *   count == 0 ---> show warning
     *   count == 1 ---> exit directly
     */
    int count = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (count == 1) System.exit(0);
        if (keyCode == KeyEvent.KEYCODE_BACK && count < 1){
            Utils.showShortToast(MainActivity.this,"Press again to log out");
            count ++;
        }
        return true;
    }

    /**
     * push radiobutton on the bottom to transform current fragment to corresponding fragment
     * press radiobutton1,
     * fragments of radiobutton2 and radiobutton3 will be hiding. only fragment of radiobutton1 shows up
     */
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Fragment fragment1 = manager.findFragmentByTag("f1");
        Fragment fragment2 = manager.findFragmentByTag("f2");
        Fragment fragment3 = manager.findFragmentByTag("f3");

        if (fragment1!=null){
            transaction.hide(fragment1);
        }
        if(fragment2!=null) {
            transaction.hide(fragment2);
        }
        if(fragment3!=null) {
            transaction.hide(fragment3);
        }
        switch (i){
            case R.id.chat:
                if (fragment1 == null){
                    fragment1 = new ChatList();
                    transaction.add(R.id.fragment_container,fragment1,"f1");
                }else {
                    transaction.show(fragment1);
                }
                bar_name.setText("Group Chat");
                iv.setImageResource(R.drawable.tab_tab2_normal);
                break;
            case R.id.contacts:
                if (fragment2 == null){
                    fragment2 = new PersonList();
                    transaction.add(R.id.fragment_container,fragment2,"f2");
                }else {
                    transaction.show(fragment2);
                }
                bar_name.setText("Chat List");
                iv.setImageResource(R.drawable.tab_tab3_normal);
                break;
            case R.id.info:
                if (fragment3 == null){
                    fragment3 = new SettingList();
                    transaction.add(R.id.fragment_container,fragment3,"f3");
                }else {
                    transaction.show(fragment3);
                }
                bar_name.setText("Settings");
                iv.setImageResource(R.drawable.tab_tab1_normal);
                break;
        }
        transaction.commit();
    }

    public static AsyncTask<Void, String, Void> getReceive_text() {
        return receive_text;
    }
}
