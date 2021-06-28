package com.soluk.belle_net_alpha.search_users;

public class Returned_User_Info_Object
{
    String user_name;
    String user_family;
    String user_picture;
    String user_id;

    public String getUser_name()
    {return user_name;}

    public void setUser_name(String user_name)
    {this.user_name = user_name;}

    public String getUser_family()
    {return user_family;}

    public void setUser_family(String user_family)
    {this.user_family = user_family;}

    public String getUser_picture()
    {return user_picture;}

    public void setUser_picture(String user_picture)
    {this.user_picture = user_picture;}

    public String getUser_id()
    {return user_id;}

    public void setUser_id(String user_id)
    {this.user_id = user_id;}


    @Override
    public String toString()
    {
        return "{" +
                "user_name='" + user_name + '\'' +
                ", user_family='" + user_family + '\'' +
                ", user_picture='" + user_picture + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }
}
