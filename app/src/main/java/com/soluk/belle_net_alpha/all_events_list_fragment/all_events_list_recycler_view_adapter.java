package com.soluk.belle_net_alpha.all_events_list_fragment;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.geojson.Feature;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.dummy.DummyContent.DummyItem;
import com.soluk.belle_net_alpha.model.Events_DB_VM;
import com.soluk.belle_net_alpha.selected_event.Selected_Event_Activity;
import com.soluk.belle_net_alpha.user_followx.User_FollowX_List_Activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
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
        holder.name_tv.setText(feature_list.get(position).getStringProperty("name"));
        holder.family_tv.setText(feature_list.get(position).getStringProperty("family"));
        holder.event_date_tv.setText(date_reformat(feature_list.get(position).getStringProperty("event_date")));
        holder.event_joinees_tv.setText(feature_list.get(position).getStringProperty("count"));

        String profile_pic_name = feature_list.get(position).getStringProperty("profile_pic");

        Bitmap profile_pic = get_profile_bmp(profile_pic_name);
        if(profile_pic!=null)
            holder.profile_pic_civ.setImageBitmap(profile_pic);

        holder.join_event.setOnClickListener(v ->
        {
            Toast.makeText(holder.join_event.getContext(),feature_list.get(position).getStringProperty("name"),Toast.LENGTH_SHORT).show();

        });

        holder.card_layout.setOnClickListener(v ->
        {
            Toast.makeText(holder.card_layout.getContext(),feature_list.get(position).getStringProperty("family"),Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(holder.card_layout.getContext(), Selected_Event_Activity.class);
            holder.join_event.getContext().startActivity(intent);
        });

    }

    private Bitmap get_profile_bmp(String name)
    {
        //ContextWrapper cw = new ContextWrapper(context);
        //File directory = cw.getDir("Profile_Pictures", Context.MODE_PRIVATE);

        try
        {
            File file =new File(Events_DB_VM.get_image_file(), name);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            return bitmap;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private String date_reformat(String time)
    {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MMMM, dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
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
            event_time_tv = view.findViewById(R.id.event_time);
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