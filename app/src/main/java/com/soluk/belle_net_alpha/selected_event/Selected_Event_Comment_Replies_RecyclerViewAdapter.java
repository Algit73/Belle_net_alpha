package com.soluk.belle_net_alpha.selected_event;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.soluk.belle_net_alpha.HTTP_Provider;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.model.Events_DB_VM;
import com.soluk.belle_net_alpha.ui.login.User_Credentials;
import com.soluk.belle_net_alpha.utils.Post_Request_Creator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.soluk.belle_net_alpha.model.Events_DB_VM.COMMENT_ID;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.COMMENT_TAIL_ID;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_ID;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_REQUEST;


public class Selected_Event_Comment_Replies_RecyclerViewAdapter extends RecyclerView.Adapter<Selected_Event_Comment_Replies_RecyclerViewAdapter.ViewHolder>
{

    private final List<Selected_Event_Comments_Object> comments_info;
    private final String TAG = Selected_Event_Comment_Replies_Fragment.class.getSimpleName();
    private fragment_adapter_interface adapter_interface;
    private final int REMOVE_USER_COMMENT = 1;
    public interface fragment_adapter_interface {void refresh_comments_list(int command);}

    public Selected_Event_Comment_Replies_RecyclerViewAdapter(List<Selected_Event_Comments_Object> items, fragment_adapter_interface adapter_interface)
    {
        comments_info = items;
        this.adapter_interface = adapter_interface;
    }

    @NonNull
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
        Selected_Event_Comments_Object comment_info = comments_info.get(position);
        String user_name_family = comment_info.getUser_name()+" "+comment_info.getUser_family();
        holder.user_name_family_tv.setText(user_name_family);
//        Log.d(TAG,"User_Id onResponse: \n"+ comment_info.user_id);

        /// Binding the menu
        holder.comment_menu_ib.setOnClickListener(v ->
        {
            PopupMenu popup_menu = new PopupMenu(holder.comment_menu_ib.getContext(), holder.comment_menu_ib);
            //inflating menu from xml resource
            popup_menu.inflate(R.menu.menu_comment_items);
            try
            {
                if (comment_info.user_id.equals(User_Credentials.get_item(USER_ID)))
                {
                    popup_menu.getMenu().findItem(R.id.remove_user_event).setVisible(true);
                    popup_menu.getMenu().findItem(R.id.report_user_comment).setVisible(false);
                }
                else
                {
                    popup_menu.getMenu().findItem(R.id.remove_user_event).setVisible(false);
                    popup_menu.getMenu().findItem(R.id.report_user_comment).setVisible(true);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            //adding click listener
            popup_menu.setOnMenuItemClickListener(item ->
            {
                int id = item.getItemId();
                if (id == R.id.remove_user_event)
                {
                    remove_comment(comment_info, holder.comment_menu_ib.getContext());
                    Log.d(TAG,"comment_info: \n"+ comment_info.comment_tail_id);
                }
                else
                    report_comment(comment_info);
                return true;

            });
            //displaying the popup
            popup_menu.show();
        });

        holder.user_comment_tv.setText(comment_info.user_comment);
        String path = Events_DB_VM.profile_pic_url + comment_info.getUser_picture();
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

    private void remove_comment(Selected_Event_Comments_Object  comment_info, Context context)
    {
        try
        {
            Post_Request_Creator request = new Post_Request_Creator();
            request.add(COMMENT_ID,comment_info.comment_id);
            request.add(COMMENT_TAIL_ID,comment_info.comment_tail_id);
            request.add(USER_REQUEST,"event_remove_comment");
            Log.d(TAG,"comment info: "+ comment_info + '\n');
            Log.d(TAG,"comment id: "+ comment_info + '\n');
            send_post_request (request, context);
        }
        catch (Exception e) {Log.d(TAG,"remove_comment Exception: \n"+ e);}
    }

    private void report_comment(Selected_Event_Comments_Object comment_info) {}

    private void send_post_request(Post_Request_Creator request, Context context)
    {
        Callback callback = new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {}

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                String body = response.body().string();
                Log.d(TAG,"Selected_Event_Comments_Fragment onResponse: \n"+ body);
                adapter_interface.refresh_comments_list(REMOVE_USER_COMMENT);
//                if(!body.equals(""))
//                {
//
//                }

            }
        };

        HTTP_Provider.post_json(context.getString(R.string.events_comments_followers_handler),
                request.get_request(),callback);
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
        public final ImageButton comment_menu_ib;


        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            user_name_family_tv = view.findViewById(R.id.user_name_family_tv);
            user_profile_image_civ = view.findViewById(R.id.user_profile_image_civ);
            user_comment_tv = view.findViewById(R.id.user_comment_tv);
            comment_date_time_tv = view.findViewById(R.id.comment_date_time_tv);
            comment_menu_ib = view.findViewById(R.id.comment_menu_ib);

        }

        @NonNull
        @Override
        public String toString()
        {return super.toString() + " '" + user_name_family_tv.getText() + "'";}
    }
}