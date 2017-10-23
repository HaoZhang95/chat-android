package com.example.ahao9.chatclient.components;

/**
 * this class is used for add(new person) to the contact list
 */
public class Person {
    private String name;
    private String ip;

    public  Person(String name,String ip){
        this.name=name;
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    @Override
    public String toString() {
        return name + " ======> " + ip;
    }
}



