package com.amt.trackertest.httpTask;


public abstract class HttpHandler {

    public abstract void onResponse(String result);

    public void login(String user, String token){
        new AsyncHttpTask(this).execute("Login",user,token);
    }

    public void createSession(String user_id){
        new AsyncHttpTask(this).execute("CreateSession", user_id);
    }

    public void sendLocation(String latitude, String longitude, String session_id, String battery){
        new AsyncHttpTask(this).execute("SendLocation", latitude, longitude, session_id, battery);
    }

}