package com.soluk.belle_net_alpha.user_followx;

public class FollowX_Object
{
    String user_name;
    String user_family;
    String user_picture;

    public String getUser_family()
    {
        return user_family;
    }

    public String getUser_picture()
    {
        return user_picture;
    }

    public String getUser_name()
    {
        return user_name;
    }

    public void setUser_family(String user_family)
    {
        this.user_family = user_family;
    }

    public void setUser_name(String user_name)
    {
        this.user_name = user_name;
    }

    public void setUser_picture(String user_picture)
    {
        this.user_picture = user_picture;
    }

    @Override
    public String toString()
    {
        return "{" +
                "user_name='" + user_name + '\'' +
                ", user_family='" + user_family + '\'' +
                ", user_picture='" + user_picture + '\'' +
                '}';
    }
}
