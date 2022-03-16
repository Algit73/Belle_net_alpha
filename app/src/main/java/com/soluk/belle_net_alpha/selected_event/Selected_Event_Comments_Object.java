package com.soluk.belle_net_alpha.selected_event;



public class Selected_Event_Comments_Object
{
    String user_id;//
    String user_name;//
    String user_family;//
    String user_picture;//
    String user_comment;//
    String comment_id;//
    String comment_date;//
    String comment_time;//
    String comment_replies;//
    String comment_likes;//


    public String getUser_id() { return user_id; }

    public String getUser_name() { return user_name; }

    public String getUser_family() { return user_family; }

    public String getUser_picture() { return user_picture; }

    public String getUser_comment() { return user_comment; }

    public String getComment_id() { return comment_id; }

    public String getComment_date() { return comment_date; }

    public String getComment_time() { return comment_time; }

    public String getComment_likes() { return comment_likes; }

    public String getComment_replies() { return comment_replies; }


    public void setUser_id(String user_id) { this.user_id = user_id; }

    public void setUser_name(String user_name) { this.user_name = user_name; }

    public void setUser_family(String user_family) { this.user_family = user_family; }

    public void setUser_picture(String user_picture) { this.user_picture = user_picture; }

    public void setUser_comment(String user_comment) { this.user_comment = user_comment; }

    public void setComment_id(String comment_id) { this.comment_id = comment_id; }

    public void setComment_date(String comment_date) { this.comment_date = comment_date; }

    public void setComment_time(String comment_time) { this.comment_time = comment_time; }

    public void setComment_likes(String comment_likes) { this.comment_likes = comment_likes; }

    public void setComment_replies(String comment_replies) { this.comment_replies = comment_replies; }



    @Override
    public String toString()
    {
        return "{" +
                "user_id='" + user_id + '\'' +
                "user_name='" + user_name + '\'' +
                ", user_family='" + user_family + '\'' +
                ", user_picture='" + user_picture + '\'' +
                ", user_comment='" + user_comment + '\'' +
                ", comment_date='" + comment_date + '\'' +
                ", comment_time='" + comment_time + '\'' +
                ", comment_id='" + comment_id + '\'' +
                ", comment_likes='" + comment_likes + '\'' +
                ", comment_replies='" + comment_replies + '\'' +
                '}';
    }
}
