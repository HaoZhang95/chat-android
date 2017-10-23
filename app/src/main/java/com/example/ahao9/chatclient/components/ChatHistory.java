package com.example.ahao9.chatclient.components;

import java.io.File;

public class ChatHistory {

    private ChatMessage chatMessage;

    public ChatHistory(ChatMessage chatMessage){
        setChatMessage(chatMessage);
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }


}
