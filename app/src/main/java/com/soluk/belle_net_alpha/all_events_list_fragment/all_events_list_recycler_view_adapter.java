package com.soluk.belle_net_alpha.all_events_list_fragment;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mapbox.geojson.Feature;
import com.soluk.belle_net_alpha.HTTP_Provider;
import com.soluk.belle_net_alpha.Main_Activity;
import com.soluk.belle_net_alpha.ui.login.User_Credentials;
import com.soluk.belle_net_alpha.utils.Date_Time_Provider;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.model.Events_DB_VM;
import com.soluk.belle_net_alpha.selected_event.Selected_Event_Activity;
import com.soluk.belle_net_alpha.selected_user_profile_page.User_Profile_Page_Activity;
import com.soluk.belle_net_alpha.utils.Image_Provider;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.soluk.belle_net_alpha.all_events_list_fragment.all_events_list_fragment.DISCOVER_MODE;
import static com.soluk.belle_net_alpha.all_events_list_fragment.all_events_list_fragment.JOINED_MODE;
import static com.soluk.belle_net_alpha.all_events_list_fragment.all_events_list_fragment.OWN_MODE;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_ID;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_EMAIL;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_ID;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_PASSWORD;


public class all_events_list_recycler_view_adapter extends RecyclerView.Adapter<all_events_list_recycler_view_adapter.ViewHolder>
{

    private final List<Feature> feature_list;
    private int event_mode;
    boolean is_user_owned_event = false;
    private final String TAG = all_events_list_recycler_view_adapter.class.getSimpleName();
    String user_id;


    public all_events_list_recycler_view_adapter(List<Feature> feature_list, int event_mode)
    {
        this.feature_list = feature_list;
        this.event_mode = event_mode;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_all_events_list_fragment, parent, false);



        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {

        holder.loading_wheel_pb.setVisibility(View.GONE);
        holder.loading_screen_iv.setVisibility(View.GONE);
        /// Find out if user owns the event

        try {user_id = User_Credentials.get_item(USER_ID);}
        catch (JSONException e){e.printStackTrace();}


        /// Showing event cards according to the
        switch (event_mode)
        {
            case DISCOVER_MODE:
                holder.join_event_btn.setBackgroundColor(ResourcesCompat.getColor(holder.join_event_btn.getResources()
                        , R.color.teal_palette_light, holder.join_event_btn.getContext().getTheme()));
                holder.join_event_btn.setTextColor(ResourcesCompat.getColor(holder.join_event_btn.getResources()
                        , R.color.gray_100, holder.join_event_btn.getContext().getTheme()));
                holder.join_event_btn.setText("join"); break;
            case JOINED_MODE:
                holder.join_event_btn.setBackgroundColor(ResourcesCompat.getColor(holder.join_event_btn.getResources()
                        , R.color.gray_200, holder.join_event_btn.getContext().getTheme()));
                holder.join_event_btn.setTextColor(ResourcesCompat.getColor(holder.join_event_btn.getResources()
                        , R.color.gray_500, holder.join_event_btn.getContext().getTheme()));
                holder.join_event_btn.setText("joined"); break;
            case OWN_MODE:
                holder.join_event_btn.setBackgroundTintList(ResourcesCompat.getColorStateList(holder.join_event_btn.getResources()
                        ,R.color.red_500, holder.join_event_btn.getContext().getTheme()));
                holder.join_event_btn.setText("Remove"); break;
        }


        holder.mItem = feature_list.get(position);
        holder.name_tv.setText(feature_list.get(position).getStringProperty(Events_DB_VM.USER_NAME));
        holder.family_tv.setText(feature_list.get(position).getStringProperty(Events_DB_VM.USER_FAMILY));
        holder.event_date_tv.setText(Date_Time_Provider
                .date_to_MDY(feature_list.get(position).getStringProperty(Events_DB_VM.EVENT_DATE)));
        holder.event_date_end_tv.setText(Date_Time_Provider
                .date_to_MDY(feature_list.get(position).getStringProperty(Events_DB_VM.EVENT_DATE_END)));
        holder.event_time_tv.setText(Date_Time_Provider
                .time_reformat(feature_list.get(position).getStringProperty(Events_DB_VM.EVENT_TIME)));
        holder.event_time_end_tv.setText(Date_Time_Provider
                .time_reformat(feature_list.get(position).getStringProperty(Events_DB_VM.EVENT_TIME_END)));
        holder.event_joinees_tv.setText(feature_list.get(position).getStringProperty(Events_DB_VM.NUM_OF_JOINED));

        /// Setting button color according to the user's past choices


        String profile_pic_name = feature_list.get(position).getStringProperty(Events_DB_VM.USER_PIC);

        Bitmap profile_pic = Image_Provider.get_profile_bmp(profile_pic_name);

        if(profile_pic!=null)
            holder.profile_pic_civ.setImageBitmap(profile_pic);
        /// Check if user owns the event or joined it before

            

        holder.profile_pic_civ.setOnClickListener(v ->
        {
            try
            {
                if(feature_list.get(position).getStringProperty(USER_ID).equals(User_Credentials.get_item(USER_ID)))
                    ((Main_Activity)holder.profile_pic_civ.getContext()).show_user_profile();
                else
                {
                Intent intent = new Intent(holder.profile_pic_civ.getContext(), User_Profile_Page_Activity.class);
                intent.putExtra("feature", feature_list.get(position).toJson());
                holder.profile_pic_civ.getContext().startActivity(intent);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        });

        /// Send user join/opt_out/remove command
        holder.join_event_btn.setOnClickListener(v ->
        {
            /// Showing progress
            holder.loading_wheel_pb.setVisibility(View.VISIBLE);
            holder.loading_screen_iv.setVisibility(View.VISIBLE);

            /// Initializing request: date, user_id, event_id
            JSONObject json = new JSONObject();
            Context context = holder.join_event_btn.getContext();

            try
            {   json.put(USER_EMAIL, User_Credentials.get_item(USER_EMAIL));
                json.put(USER_PASSWORD,User_Credentials.get_item(USER_PASSWORD));
                json.put(USER_ID,user_id);
                json.put(EVENT_ID,feature_list.get(position)
                                    .getStringProperty(Events_DB_VM.EVENT_ID));
            }
            catch (Exception e) {Log.d(TAG, "event_unique_id catch " + e);}
            switch (event_mode)
            {
                case DISCOVER_MODE:
                    try
                    {json.put("request","join");}
                    catch (Exception ignored) {}

                    send_join_command(json,context); break;

                case JOINED_MODE:
                    try {json.put("request","out");}
                    catch (Exception ignored) {}

                    send_join_command(json,context); break;

                case OWN_MODE:
                    try {json.put("request","remove");}
                    catch (Exception ignored) {}
                    AlertDialog.Builder alert_dialog_bl = new AlertDialog.Builder(context);
                    alert_dialog_bl.setMessage("Do you really want to remove this event?")
                            .setCancelable(true)
                            .setPositiveButton("YES", (dialog, id) -> send_join_command(json,context))
                            .setNegativeButton("CANCEL", (dialog, id) -> dialog.cancel());

                    AlertDialog alertDialog = alert_dialog_bl.create();

                    alertDialog.show();
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ResourcesCompat.getColor(context.getResources()
                            ,R.color.gray_800,context.getTheme()));
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ResourcesCompat.getColor(context.getResources()
                            ,R.color.gray_800,context.getTheme())); break;

            }



        });

        holder.card_layout.setOnClickListener(v ->
        {

            Intent intent = new Intent(holder.card_layout.getContext(), Selected_Event_Activity.class);
            intent.putExtra("feature", feature_list.get(position).toJson());
            holder.join_event_btn.getContext().startActivity(intent);
        });

    }

    void send_join_command(JSONObject json, Context context)
    {
        //Log.d(TAG,"Send join: "+params);
        Log.d(TAG,"Send join JSON: "+json);
        Callback callback = new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {

                Log.d(TAG, "Post Body: "+response.body().string());

            }
        };

        HTTP_Provider.post_json(context.getString(R.string.bellenet_join_event_url),json,callback);
        ((Main_Activity)context).refresh_db();

    }


    @Override
    public int getItemCount()
    {
        return feature_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final TextView name_tv;
        public final TextView family_tv;
        public final TextView event_date_tv;
        public final TextView event_date_end_tv;
        public final TextView event_time_tv;
        public final TextView event_time_end_tv;
        public final TextView event_distance_tv;
        public final TextView event_type_tv;
        public final TextView event_explanation_tv;
        public final TextView event_joinees_tv;
        public final CircleImageView profile_pic_civ;
        public final Button join_event_btn;
        public final ConstraintLayout card_layout;
        public final CardView even_card_cv;
        public Feature mItem;
        public ProgressBar loading_wheel_pb;
        public ImageView loading_screen_iv;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            name_tv = view.findViewById(R.id.user_name_tv);
            family_tv = view.findViewById(R.id.user_family_tv);
            event_date_tv = view.findViewById(R.id.event_date_tv);
            event_date_end_tv = view.findViewById(R.id.event_date_end_tv);
            event_time_tv = view.findViewById(R.id.event_start_time_tv);
            event_time_end_tv = view.findViewById(R.id.event_start_time_end_tv);
            event_distance_tv = view.findViewById(R.id.event_distance);
            event_type_tv = view.findViewById(R.id.challenge_type_tv);
            event_explanation_tv = view.findViewById(R.id.event_explanation);
            event_joinees_tv = view.findViewById(R.id.event_joinees);
            profile_pic_civ = view.findViewById(R.id.profile_pic_civ);
            join_event_btn = view.findViewById(R.id.join_event_btn);
            card_layout = view.findViewById(R.id.card_layout);
            even_card_cv = view.findViewById(R.id.event_card_cv);
            loading_wheel_pb = view.findViewById(R.id.loading_wheel_pb);
            loading_screen_iv = view.findViewById(R.id.loading_screen_iv);
        }



        /*
        @Override
        public String toString()
        {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

         */
    }
}