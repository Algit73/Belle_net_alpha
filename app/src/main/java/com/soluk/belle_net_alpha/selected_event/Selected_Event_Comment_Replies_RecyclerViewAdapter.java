package com.soluk.belle_net_alpha.selected_event;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.model.Events_DB_VM;
import com.soluk.belle_net_alpha.selected_event.dummy.DummyContent.DummyItem;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class Selected_Event_Comment_Replies_RecyclerViewAdapter extends RecyclerView.Adapter<Selected_Event_Comment_Replies_RecyclerViewAdapter.ViewHolder>
{

    private final List<Selected_Event_Comment_Replies_Object> comments_info;

    public Selected_Event_Comment_Replies_RecyclerViewAdapter(List<Selected_Event_Comment_Replies_Object> items)
    {
        comments_info = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_comment_replies_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        Selected_Event_Comment_Replies_Object comment_info = comments_info.get(position);
        String user_name_family = comment_info.getUser_name()+" "+comment_info.getUser_family();
        holder.user_name_family_tv.setText(user_name_family);

        holder.user_comment_tv.setText(comment_info.user_comment);
        String path = Events_DB_VM.profile_pic_url + comment_info.getUser_picture() + ".jpg";
        Target target = new Target()
        {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
            {
                holder.user_profile_image_civ.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable)
            {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable)
            {

            }
        };

        Picasso.get().load(path).into(target);
    }

    @Override
    public int getItemCount()
    {
        return comments_info.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final TextView user_name_family_tv;
        public final TextView user_comment_tv;
        public final TextView comment_date_time_tv;
        public final CircleImageView user_profile_image_civ;


        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            user_name_family_tv = view.findViewById(R.id.user_name_family_tv);
            user_profile_image_civ = view.findViewById(R.id.user_profile_image_civ);
            user_comment_tv = view.findViewById(R.id.user_comment_tv);
            comment_date_time_tv = view.findViewById(R.id.comment_date_time_tv);

        }

        @Override
        public String toString()
        {return super.toString() + " '" + user_name_family_tv.getText() + "'";}
    }
}