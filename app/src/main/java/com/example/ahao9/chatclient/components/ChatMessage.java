package com.example.ahao9.chatclient.components;

import java.io.File;

public class ChatMessage {

    public static final int TEXT_SEND = 1;
    public static final int TEXT_RECEIVE = 2;
    public static final int VOICE_SEND = 3;
    public static final int VOICE_RECEIVE = 4;

    private String content_text;
    private File content_voice;
    private int flag;
    private String time;

    //constructor for text message
    public ChatMessage(String content, int flag,String time){
        setContent_text(content);
        this.flag = flag;
        this.time = time;
    }
    //constructor for voice file message
    public ChatMessage(File content, int flag,String time){
        setContent_voice(content);
        this.flag = flag;
        this.time = time;
    }

    public int getFlag() {
        return flag;
    }

    public String getContent_text() {
        return content_text;
    }

    public void setContent_text(String content_text) {
        this.content_text = content_text;
    }

    public File getContent_voice() {
        return content_voice;
    }

    public void setContent_voice(File content_voice) {
        this.content_voice = content_voice;
    }

    public String getTime() {
        return time;
    }

}
