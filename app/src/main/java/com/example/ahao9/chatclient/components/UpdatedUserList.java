package com.example.ahao9.chatclient.components;

/**
 * a string which caontains updated userlist from server
 */
public class UpdatedUserList {

    private String updatedList;

    public UpdatedUserList(String updatedList){
        setUpdatedList(updatedList);
    }

    public String getUpdatedList() {
        return updatedList;
    }

    public void setUpdatedList(String updatedList) {
        this.updatedList = updatedList;
    }
}
