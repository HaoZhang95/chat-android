package com.example.ahao9.chatclient.activities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ahao9.chatclient.adpaters.ChatListAdapter;
import com.example.ahao9.chatclient.components.ChatHistory;
import com.example.ahao9.chatclient.components.ChatMessage;
import com.example.ahao9.chatclient.components.ClientSocket;
import com.example.ahao9.chatclient.components.R;
import com.example.ahao9.chatclient.components.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Every chat will jump into this Activity
 */
public class ChatList extends Fragment{
    private ChatListAdapter chatAdapter;
    private List<ChatHistory> lists;
    private ImageButton voiceBtnInText;
    private ImageButton voiceBtnInVoice;
    private TextView title_name_txt;
    private ListView lv;
    private EditText typeText;
    private Button sendBtn;
    private MediaRecorder recorder = null;

    private File voiceFile;
    private static String voiceFilePath;
    private RelativeLayout text_bottom;
    private RelativeLayout voice_bottom;
    private Button voice_press;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_chat,null);

        text_bottom = (RelativeLayout) view.findViewById(R.id.text_bottom);
        voice_bottom = (RelativeLayout) view.findViewById(R.id.voice_bottom);
        title_name_txt = (TextView) view.findViewById(R.id.bar_name);
        lv = (ListView) view.findViewById(R.id.chat_list);
        typeText = (EditText) view.findViewById(R.id.typeText);
        sendBtn = (Button) view.findViewById(R.id.send);
        voice_press = (Button) view.findViewById(R.id.voice_press);

        //set chatAdapter on ListView
        lists = new ArrayList<>();
        chatAdapter = new ChatListAdapter(lists,getContext());
        lv.setAdapter(chatAdapter);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg();
            }
        });

        //=================voice button setONclickListener=================================

        voiceBtnInText = (ImageButton) view.findViewById(R.id.vocie_btn_in_text);
        voiceBtnInVoice = (ImageButton) view.findViewById(R.id.vocie_btn_in_voice);

        voiceBtnInText.setOnClickListener(new VoiceInTextClickListener());
        voiceBtnInVoice.setOnClickListener(new VoiceInVoiceClickListener());
        voice_press.setOnTouchListener(new voicePressOnTouchListener());

        //=============this fragment as a subscriber to receive alterd data=================
        EventBus.getDefault().register(this);

        return view;
    }

    /**
     * when client receives message, update it on ui thread
     * @param message
     */
    public void updateUI(final ChatMessage message){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Utils.showShortToast(getActivity(),message.getContent_text());
                ChatHistory chatHistory = new ChatHistory(message);
                lists.add(chatHistory);
                chatAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * when messages from data source has been changed, this onEvent method will be invoked automatically
     */
    @Subscribe
    public void onEvent(ChatMessage message) {
        updateUI(message);
    }

    class VoiceInTextClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            verifyStoragePermissions(getActivity());
            text_bottom.setVisibility(View.GONE);
            voice_bottom.setVisibility(View.VISIBLE);
        }
    }

    class VoiceInVoiceClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            text_bottom.setVisibility(View.VISIBLE);
            voice_bottom.setVisibility(View.GONE);

            typeText.setFocusable(true);
            typeText.setFocusableInTouchMode(true);
            typeText.requestFocus();
        }
    }

    class voicePressOnTouchListener implements View.OnTouchListener{

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == 0){    //press the button but not release
                startRecord();
            }else if (event.getAction() == 1){ //release
                stopRecord();
                ChatMessage message = new ChatMessage(voiceFile,ChatMessage.VOICE_SEND,Utils.getTime());
                ChatHistory chatHistory = new ChatHistory(message);
                lists.add(chatHistory);
                chatAdapter.notifyDataSetChanged();
            }
            return false;
        }
    }

    /**
     * start recording and store the voice file in the StorageDirectory/ChatVoices folder
     */
    public void startRecord(){
        if (recorder == null){
            File dir = new File(Environment.getExternalStorageDirectory(),"ChatVoices");
            if (! dir.exists()){
                dir.mkdirs();
            }
            //name the voice file according the current time
            String filename = new Date().getTime() + ".amr";
            voiceFile = new File(dir,filename);

            if (! voiceFile.exists()){
                try {
                    voiceFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //start recorder
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);

            voiceFilePath = voiceFile.getAbsolutePath();
            recorder.setOutputFile(voiceFile.getAbsolutePath());
            try {
                recorder.prepare();
                recorder.start();
                Utils.showShortToast(getContext(),"recording...");
            }catch (IllegalStateException e){
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * stop recording and transmit voice file to server automatically
     */
    public void stopRecord(){
        if (recorder != null){
            recorder.stop();
            recorder.release();
            recorder = null;

            Utils.showShortToast(getContext(),"stop recording...");
            sendVoice();
        }
    }

    /**
     * After android 6.0 we have to checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     * @param activity
     * Storage Permissions
     */
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    /**
     * get the typed text from EditText, send the text to the server and insert it into ChatHistory
     */
    public void sendMsg(){
        try {
            String str = typeText.getText().toString();
            ClientSocket.getDos().writeUTF(str);
            ClientSocket.getDos().flush();

            ChatMessage message = new ChatMessage(str,ChatMessage.TEXT_SEND,Utils.getTime());
            ChatHistory chatHistory = new ChatHistory(message);
            lists.add(chatHistory);
            chatAdapter.notifyDataSetChanged();

            typeText.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            Log.e("cuowu",e.getMessage());
        }
    }

    /**
     * send voice file byte by bete to the server
     */
    public void sendVoice(){
        try {
            ClientSocket.getDos()
                    .writeUTF(("upload_voice" + ","
                            + new File(voiceFile.getAbsolutePath()).length()
                            + "," + SetNameActivity.getUsername()));
            ClientSocket.getDos().flush();

            FileInputStream fin = new FileInputStream(voiceFile);
            int len = 0;
            byte[] b1 = new byte[1024];
            while ((len = fin.read(b1)) != -1) {
                ClientSocket.getDos().write(b1, 0, len);
                ClientSocket.getDos().flush();
            }
            fin.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static String getVoiceFilePath() {
        return voiceFilePath;
    }
}
