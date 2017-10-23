package com.example.ahao9.chatclient.adpaters;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ahao9.chatclient.activities.ChatList;
import com.example.ahao9.chatclient.components.ChatHistory;
import com.example.ahao9.chatclient.components.ChatMessage;
import com.example.ahao9.chatclient.components.R;
import com.example.ahao9.chatclient.components.Utils;

import java.util.List;

/**
 * ChatListAdapter for chatActivity
 */
public class ChatListAdapter extends BaseAdapter {

    public List<ChatHistory> lists;
    private Context context;
    private RelativeLayout layout;
    private MediaPlayer mediaPlayer;

    public ChatListAdapter(List<ChatHistory> lists, Context context){
        this.lists = lists;
        this.context = context;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int i) {
        return lists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * According to receive message to place it on optimal position with corresponding layout resource
     */
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = LayoutInflater.from(context);
        // distinguish the message is text or voice and comes from yourself or other people
        // and place the message with corresponding layout XML
        switch (lists.get(i).getChatMessage().getFlag()){
            case ChatMessage.TEXT_RECEIVE:
                layout = (RelativeLayout) inflater.inflate(R.layout.text_left_item,null);
                TextView tv_rece = (TextView) layout.findViewById(R.id.tv);
                TextView time1 = (TextView) layout.findViewById(R.id.time_tv);

                time1.setText(lists.get(i).getChatMessage().getTime());
                tv_rece.setText(lists.get(i).getChatMessage().getContent_text());
                layout.setOnClickListener(new textOnClickListener());
                break;
            case ChatMessage.TEXT_SEND:
                layout = (RelativeLayout) inflater.inflate(R.layout.text_right_item,null);
                TextView tv_send = (TextView) layout.findViewById(R.id.tv);
                TextView time2 = (TextView) layout.findViewById(R.id.time_tv);

                time2.setText(lists.get(i).getChatMessage().getTime());
                tv_send.setText(lists.get(i).getChatMessage().getContent_text());
                layout.setOnClickListener(new textOnClickListener());
                break;
            case ChatMessage.VOICE_SEND:
                layout = (RelativeLayout) inflater.inflate(R.layout.voice_right_item,null);
                TextView time3 = (TextView) layout.findViewById(R.id.time_tv);

                time3.setText(lists.get(i).getChatMessage().getTime());
                playVoiceFile(i);
                break;
            case ChatMessage.VOICE_RECEIVE:
                layout = (RelativeLayout) inflater.inflate(R.layout.voice_left_item,null);
                TextView time4 = (TextView) layout.findViewById(R.id.time_tv);

                time4.setText(lists.get(i).getChatMessage().getTime());
                playVoiceFile(i);
                break;
        }

        return layout;
    }

    /**
     * play voice file of item
     */
    private void playVoiceFile(final int i) {
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer = new MediaPlayer();
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();
                }
                try {
                    mediaPlayer.setDataSource(lists.get(i).getChatMessage().getContent_voice().getAbsolutePath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception e) {
                    // TODO: handle exception
                    Log.d("cuowu", "onClick: " + e.getMessage());
                }
            }
        });
    }


    class textOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Utils.showShortToast(context,"this is text");
        }
    }
}
