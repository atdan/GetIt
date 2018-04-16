package com.example.eets_nostredame.getit.model;

/**
 * Created by Eets_Nostredame on 18/03/2018.
 */

public class User {
    private String user_id;
    private String name;
    public User(String user_id, String name){
        this.name = name;
        this.user_id = user_id;
    }
    public User(){

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
