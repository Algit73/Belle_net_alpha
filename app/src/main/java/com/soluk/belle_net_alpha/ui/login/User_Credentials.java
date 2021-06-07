package com.soluk.belle_net_alpha.ui.login;


import com.soluk.belle_net_alpha.event_data_maker.file_maker;

import org.json.JSONException;
import org.json.JSONObject;

public class User_Credentials
{


    private static file_maker credentials_file;
    private static JSONObject user_credentials;

    public User_Credentials(){}

    public static void init(String file_directory, String key)
    {
        credentials_file = new file_maker(file_directory,key);
        user_credentials = new JSONObject();
    }

    public static file_maker get_credentials_file()
    {return credentials_file;}

    public static String get_item(String key) throws JSONException
    {return credentials_file.read_json().get(key).toString();}

    public static JSONObject get_items()
    {return credentials_file.read_json();}

    public static void put_user_item(String key, String value) throws JSONException
    {user_credentials.put(key,value);}

    public static void write_user_items()
    {credentials_file.write_json(user_credentials);}


}
