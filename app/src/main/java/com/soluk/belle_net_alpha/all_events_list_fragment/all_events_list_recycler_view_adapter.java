package com.soluk.belle_net_alpha.all_events_list_fragment;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.geojson.Feature;
import com.soluk.belle_net_alpha.Main_Activity;
import com.soluk.belle_net_alpha.ui.login.User_Credentials;
import com.soluk.belle_net_alpha.utils.Date_Time_Provider;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.model.Events_DB_VM;
import com.soluk.belle_net_alpha.selected_event.Selected_Event_Activity;
import com.soluk.belle_net_alpha.selected_user_profile_page.User_Profile_Page_Activity;
import com.soluk.belle_net_alpha.utils.Image_Provider;

import org.json.JSONException;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_ID;


public class all_events_list_recycler_view_adapter extends RecyclerView.Adapter<all_events_list_recycler_view_adapter.ViewHolder>
{

    private final List<Feature> feature_list;


    public all_events_list_recycler_view_adapter(List<Feature> feature_list)
    {
        this.feature_list = feature_list;
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
        holder.mItem = feature_list.get(position);
        holder.name_tv.setText(feature_list.get(position).getStringProperty(Events_DB_VM.USER_NAME));
        holder.family_tv.setText(feature_list.get(position).getStringProperty(Events_DB_VM.USER_FAMILY));
        holder.event_date_tv.setText(Date_Time_Provider.date_to_MDY(feature_list.get(position).getStringProperty(Events_DB_VM.EVENT_DATE)));
        holder.event_joinees_tv.setText(feature_list.get(position).getStringProperty(Events_DB_VM.NUM_OF_JOINED));

        String profile_pic_name = feature_list.get(position).getStringProperty(Events_DB_VM.USER_PIC);

        Bitmap profile_pic = Image_Provider.get_profile_bmp(profile_pic_name);
        if(profile_pic!=null)
            holder.profile_pic_civ.setImageBitmap(profile_pic);

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

        holder.join_event.setOnClickListener(v ->
        {
            Toast.makeText(holder.join_event.getContext(),feature_list
                    .get(position).getStringProperty(Events_DB_VM.USER_NAME),Toast.LENGTH_SHORT).show();

        });

        holder.card_layout.setOnClickListener(v ->
        {
            Toast.makeText(holder.card_layout.getContext(),feature_list
                    .get(position).getStringProperty(Events_DB_VM.USER_FAMILY),Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(holder.card_layout.getContext(), Selected_Event_Activity.class);
            intent.putExtra("feature", feature_list.get(position).toJson());
            holder.join_event.getContext().startActivity(intent);
        });

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
        public final TextView event_time_tv;
        public final TextView event_distance_tv;
        public final TextView event_type_tv;
        public final TextView event_explanation_tv;
        public final TextView event_joinees_tv;
        public final CircleImageView profile_pic_civ;
        public final Button join_event;
        public final ConstraintLayout card_layout;
        public Feature mItem;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            name_tv = view.findViewById(R.id.user_name);
            family_tv = view.findViewById(R.id.user_family);
            event_date_tv = view.findViewById(R.id.event_date);
            event_time_tv = view.findViewById(R.id.event_start_time_tv);
            event_distance_tv = view.findViewById(R.id.event_distance);
            event_type_tv = view.findViewById(R.id.challenge_type);
            event_explanation_tv = view.findViewById(R.id.event_explanation);
            event_joinees_tv = view.findViewById(R.id.event_joinees);
            profile_pic_civ = view.findViewById(R.id.profile_pic);
            join_event = view.findViewById(R.id.join_event);
            card_layout = view.findViewById(R.id.card_layout);
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