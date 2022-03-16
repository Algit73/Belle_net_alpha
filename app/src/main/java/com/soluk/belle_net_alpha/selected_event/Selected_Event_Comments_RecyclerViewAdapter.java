package com.soluk.belle_net_alpha.selected_event;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_ID;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_ID;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_REQUEST;

/**
 * {@link RecyclerView.Adapter} that can display a {@link }.
 * TODO: Replace the implementation with code for your data type.
 */
public class Selected_Event_Comments_RecyclerViewAdapter extends RecyclerView.Adapter<Selected_Event_Comments_RecyclerViewAdapter.ViewHolder>
{

    private final List<Selected_Event_Comments_Object> comments_info;
    private final String TAG = Selected_Event_Comments_Fragment.class.getSimpleName();

    public Selected_Event_Comments_RecyclerViewAdapter(List<Selected_Event_Comments_Object> items)
    {
        comments_info = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_selected_event_comment_list_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        Selected_Event_Comments_Object comment_info = comments_info.get(position);
        String user_name_family = comment_info.getUser_name()+" "+comment_info.getUser_family();
        holder.user_name_family_tv.setText(user_name_family);
        holder.comment_likes_tv.setText(comment_info.comment_likes);
        int comment_replies = Integer.parseInt(comment_info.comment_replies);
        if (comment_replies>1)
            holder.comment_replies_tv.setText(String.valueOf(comment_replies-1));
        holder.user_comment_tv.setText(comment_info.user_comment);

        /// Binding the menu
        holder.event_menu_ib.setOnClickListener(v ->
        {
            PopupMenu popup_menu = new PopupMenu(holder.event_menu_ib.getContext(), holder.event_menu_ib);
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
                    remove_comment(comment_info, holder.event_menu_ib.getContext());
                else
                    report_comment(comment_info);
                return true;
//                switch (item.getItemId())
//                {
//                    case R.id.remove_user_event:
//                        //handle menu1 click
//                        return true;
//                    case R.id.report_user_comment:
//                        //handle menu2 click
//                        return true;
//
//                    default:
//                        return false;
//                }
            });
            //displaying the popup
            popup_menu.show();
        });

        /// Loading the user's profile image
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

        holder.event_comment_layout_ll.setOnClickListener(v ->
        {
            Intent intent = new Intent(holder.event_comment_layout_ll.getContext(), Selected_Event_Comment_Replies_Activity.class);
            intent.putExtra("comment_id", comment_info.comment_id);
            holder.event_comment_layout_ll.getContext().startActivity(intent);
        });
    }

    private void report_comment(Selected_Event_Comments_Object comment_info)
    {
    }

    private void remove_comment(Selected_Event_Comments_Object comment_info, Context context)
    {
        try
        {
            Post_Request_Creator request = new Post_Request_Creator();
            request.add(COMMENT_ID,comment_info.comment_id);
            request.add(USER_REQUEST,"event_remove_comment");
            send_post_request (request, context);
        }
        catch (Exception e) {Log.d(TAG,"remove_comment Exception: \n"+ e);}
    }

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
        public final TextView comment_likes_tv;
        public final TextView comment_replies_tv;
        public final ImageButton event_menu_ib;
        public final CircleImageView user_profile_image_civ;
        public final LinearLayout event_comment_layout_ll;
        //public Selected_Event_Comments_Object comment_info;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            user_name_family_tv = view.findViewById(R.id.user_name_family_tv);
            user_profile_image_civ = view.findViewById(R.id.user_profile_image_civ);
            user_comment_tv = view.findViewById(R.id.user_comment_tv);
            comment_likes_tv = view.findViewById(R.id.comment_likes_tv);
            comment_replies_tv = view.findViewById(R.id.comment_replies_tv);
            comment_date_time_tv = view.findViewById(R.id.comment_date_time_tv);
            event_menu_ib = view.findViewById(R.id.event_menu_ib);
            event_comment_layout_ll = view.findViewById(R.id.event_comment_layout_ll);

        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + user_name_family_tv.getText() + "'";
        }
    }
}