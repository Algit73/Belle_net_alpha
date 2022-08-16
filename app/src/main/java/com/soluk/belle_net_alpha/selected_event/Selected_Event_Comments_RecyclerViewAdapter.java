package com.soluk.belle_net_alpha.selected_event;

import static android.os.Looper.getMainLooper;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.soluk.belle_net_alpha.HTTP_Provider;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.model.Events_DB_VM;
import com.soluk.belle_net_alpha.ui.login.User_Credentials;
import com.soluk.belle_net_alpha.utils.Post_Request_Creator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.COMMENT_ID;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.COMMENT_TAIL_ID;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.COMMENT_TAIL_ID_R0;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_ID;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_ACTION;
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
    private fragment_adapter_interface adapter_interface;
    private final int REMOVE_USER_COMMENT = 1;
    private final int LIKE_COMMENT = 2;
    private boolean is_liked = false;

    public interface fragment_adapter_interface {void refresh_comments_list(int command);}

    public Selected_Event_Comments_RecyclerViewAdapter(List<Selected_Event_Comments_Object> items, fragment_adapter_interface adapter_interface)
    {
        comments_info = items;
        this.adapter_interface = adapter_interface;
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
        /// Initializing the card
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

            /// inflating menu from xml resource
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
            } catch (JSONException e){e.printStackTrace();}

            /// adding click listener
            popup_menu.setOnMenuItemClickListener(item ->
            {
                int id = item.getItemId();
                if (id == R.id.remove_user_event)
                {
                    remove_comment(comment_info, holder.event_menu_ib.getContext(), holder);
//                    adapter_interface.refresh_comments_list();
                }
                else
                {
                    report_comment(comment_info);
                }
                return true;
            });
            /// displaying the popup
            popup_menu.show();
        });

        /// Loading the user's profile image
        String path = Events_DB_VM.profile_pic_url + comment_info.getUser_picture();
        Target target = new Target()
        {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
            {holder.user_profile_image_civ.setImageBitmap(bitmap);}

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable){}

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable){}
        };

        Picasso.get().load(path).into(target);

        /// Requesting all comment's replies
        holder.event_comment_layout_ll.setOnClickListener(v ->
        {
            Intent intent = new Intent(holder.event_comment_layout_ll.getContext(), Selected_Event_Comment_Replies_Activity.class);
            intent.putExtra(COMMENT_ID, comment_info.comment_id);
            intent.putExtra(EVENT_ID, comment_info.event_id);
            holder.event_comment_layout_ll.getContext().startActivity(intent);
        });

        /// Change the color of thumbs up when user liked the comment
        set_thumbs_up_color(comment_info.getIs_liked(),holder);



        /// Like the comment
        holder.like_holder_cl.setOnClickListener(v ->
        {
//            Toast.makeText(holder.is_liked_iv.getContext(),"Clicked",Toast.LENGTH_SHORT).show();
            try
            {
//                if(comment_info.getIs_liked().equals("1")) is_liked = true;
//                else is_liked = false;
                Log.d(TAG,"Selected_Event_Comments_Fragment is_liked check: \n"+ is_liked);

//                Toast.makeText(holder.is_liked_iv.getContext(),"is_liked: " + is_liked,Toast.LENGTH_SHORT).show();
//                Toast.makeText(holder.is_liked_iv.getContext(),"Num likes: " + comment_info.comment_likes,
//                        Toast.LENGTH_SHORT).show();
//                Toast.makeText(holder.is_liked_iv.getContext(),"com likes: " + holder.comment_likes_tv.getText().toString(),
//                        Toast.LENGTH_SHORT).show();
                Post_Request_Creator request = new Post_Request_Creator();
                request.add(EVENT_ID, comment_info.event_id);
                request.add(COMMENT_ID, comment_info.comment_id);
                request.add(COMMENT_TAIL_ID, COMMENT_TAIL_ID_R0);
                request.add(USER_REQUEST,"like_comment");
                request.add(USER_ACTION,String.valueOf(is_liked));
                send_post_request (request, holder.is_liked_iv.getContext(), LIKE_COMMENT, holder);
            }
            catch (Exception e) {Log.d(TAG,"like_comment Exception: \n"+ e);}
        });
    }

    private void set_thumbs_up_color(String state, ViewHolder holder)
    {
        if(state.equals("1"))
        {
            holder.is_liked_iv.setImageTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(getApplicationContext(), R.color.palette_teal_light)));
            is_liked = true;
//            Toast.makeText(holder.is_liked_iv.getContext(),"is_liked: " + is_liked,Toast.LENGTH_SHORT).show();
        }
        else
        {
            holder.is_liked_iv.setImageTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(getApplicationContext(), R.color.palette_gray_dark)));
            is_liked = false;
//            Toast.makeText(holder.is_liked_iv.getContext(),"is_liked: " + is_liked,Toast.LENGTH_SHORT).show();
        }
    }


    private void report_comment(Selected_Event_Comments_Object comment_info){}

    private void remove_comment(Selected_Event_Comments_Object comment_info, Context context, final ViewHolder holder)
    {
        try
        {
            Post_Request_Creator request = new Post_Request_Creator();
            request.add(COMMENT_ID, comment_info.comment_id);
            request.add(COMMENT_TAIL_ID, COMMENT_TAIL_ID_R0);
            request.add(USER_REQUEST,"event_remove_comment");
            send_post_request (request, context, REMOVE_USER_COMMENT, holder);
        }
        catch (Exception e) {Log.d(TAG,"remove_comment Exception: \n"+ e);}
    }

    private void send_post_request(Post_Request_Creator request, Context context, int command, final ViewHolder holder)
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
                /// Check if the request has been accepted correctly
                if (body.length()>1)
                {
                    String ok_return = body.substring(0, 2);

                    if (command == REMOVE_USER_COMMENT)
                    {
                        if(!ok_return.equals("ok"))
                        {
//                            Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show();
                            Log.d(TAG,"REMOVE_USER_COMMENT ok_return:\n");
                            return;
                        }

                        adapter_interface.refresh_comments_list(REMOVE_USER_COMMENT);
                    }

                    else if (command == LIKE_COMMENT)
                    {
                        if(!ok_return.equals("ok"))
                        {
//                            Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show();
                            Log.d(TAG,"LIKE_COMMENT  !ok_return.equals():\n");
                            return;
                        }
                        try
                        {
                            String numbers = body.substring(2);
                            JSONObject numbers_json = new JSONObject(numbers);
                            String numbers_string = numbers_json.getString("likes_number");
                            String is_liked = numbers_json.getString("is_liked");
                            Handler mainHandler = new Handler(getMainLooper());
                            Runnable myRunnable = () ->
                            {
                                holder.comment_likes_tv.setText(numbers_string);
                                set_thumbs_up_color(is_liked,holder);

                            };
                            mainHandler.post(myRunnable);
                        }
                        catch (Exception e)
                        {
                            Log.d(TAG,"Selected_Event_Comments_Fragment send_post_request Exception:\n"+ e);
                        }
                    }
                }
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

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final TextView user_name_family_tv;
        public final TextView user_comment_tv;
        public final TextView comment_date_time_tv;
        public final TextView comment_likes_tv;
        public final TextView comment_replies_tv;
        public final ImageButton event_menu_ib;
        public final ImageView is_liked_iv;
        public final CircleImageView user_profile_image_civ;
        public final LinearLayout event_comment_layout_ll;
        public final ConstraintLayout like_holder_cl;
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
            is_liked_iv = view.findViewById(R.id.is_liked_iv);
            event_comment_layout_ll = view.findViewById(R.id.event_comment_layout_ll);
            like_holder_cl = view.findViewById(R.id.like_holder_cl);

        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + user_name_family_tv.getText() + "'";
        }
    }
}