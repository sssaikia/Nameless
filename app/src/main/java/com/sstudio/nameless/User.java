package com.sstudio.nameless;

/**
 * Created by Alan on 9/22/2017.
 */

public class User {
    public User() {
    }
    private String text,id;
    public User(String text,String id){
        this.text=text;
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
