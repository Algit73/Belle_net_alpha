package com.soluk.belle_net_alpha.utils;

import com.soluk.belle_net_alpha.ui.login.User_Credentials;

import org.json.JSONException;
import org.json.JSONObject;

import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_ID;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_EMAIL;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_ID;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_PASSWORD;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_REQUEST;

public class Post_Request_Creator
{
    JSONObject request;

    public Post_Request_Creator() throws JSONException
    {
        request = new JSONObject();
        request.put(USER_ID, User_Credentials.get_item(USER_ID));
        request.put(USER_EMAIL,User_Credentials.get_item(USER_EMAIL));
        request.put(USER_PASSWORD,User_Credentials.get_item(USER_PASSWORD));
    }

    public void add(String key, String Value) throws JSONException {request.put(key, Value);}

    public JSONObject get_request() {return request;}
}
