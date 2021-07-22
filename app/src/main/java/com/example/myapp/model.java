package com.example.myapp;

import com.google.firebase.Timestamp;


public class model {

    private String title;

    private String detail;
    private Timestamp timestamp;
    private String userId;



    private boolean isComplete;

    public model(){}
    public model(String title, String detail, Timestamp timestamp,boolean isComplete,String userId) {
        this.title = title;
        this.detail = detail;
        this.timestamp = timestamp;
        this.isComplete = isComplete;
        this.userId = userId;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }



    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
