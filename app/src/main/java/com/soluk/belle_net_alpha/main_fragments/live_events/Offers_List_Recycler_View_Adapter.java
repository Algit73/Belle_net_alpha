package com.soluk.belle_net_alpha.main_fragments.live_events;

import static com.soluk.belle_net_alpha.model.Events_DB_VM.BUSINESS_PIC;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_DATE;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_DATE_END;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_DESCRIPTION;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_ID;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_NAME;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_TYPE;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.NUM_OF_JOINED;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbox.geojson.Feature;
import com.soluk.belle_net_alpha.Main_Activity;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.dummy.DummyContent.DummyItem;
import com.soluk.belle_net_alpha.selected_event.Selected_Event_Activity;
import com.soluk.belle_net_alpha.utils.Date_Time_Provider;
import com.soluk.belle_net_alpha.utils.Image_Provider;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class Offers_List_Recycler_View_Adapter extends RecyclerView.Adapter<Offers_List_Recycler_View_Adapter.ViewHolder>
{

    private final List<Feature> feature_list;
    private final String TAG = Main_Activity.class.getSimpleName();

    public Offers_List_Recycler_View_Adapter(List<Feature> feature_list)
    {
        this.feature_list = feature_list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_feed_list_fragment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        Feature feature = feature_list.get(position);
        holder.header_title_tv.setText(feature.getStringProperty(EVENT_NAME));
        holder.event_description_tv.setText(feature.getStringProperty(EVENT_DESCRIPTION));
        set_event_type(holder, feature);



        holder.event_joined_tv.setText(feature.getStringProperty(NUM_OF_JOINED));
        holder.event_description_tv.setText(feature.getStringProperty(EVENT_DESCRIPTION));
        Log.d(TAG,"Offers_List_Recycler_View_Adapter: "+feature);

        set_event_wallpaper(holder, feature.getStringProperty(EVENT_ID));
        set_business_logo(holder, feature.getStringProperty(BUSINESS_PIC));
        set_date(holder, feature.getStringProperty(EVENT_DATE), feature.getStringProperty(EVENT_DATE_END));
        explore_button_def(holder, feature);
        card_layout_def(holder, feature);

    }

    private void card_layout_def(ViewHolder holder, Feature feature)
    {
        holder.card_layout.setOnClickListener(v ->
        {
            Intent intent = new Intent(holder.card_layout.getContext(), Selected_Event_Activity.class);
            intent.putExtra("feature", feature.toJson());
            holder.card_layout.getContext().startActivity(intent);

        });
    }

    private void set_event_type(ViewHolder holder, Feature feature)
    {
        switch (feature.getStringProperty(EVENT_TYPE))
        {
            case "0":
                holder.event_type_icon_iv.setImageResource(R.drawable.flag);
                holder.event_type_tv.setText("Challenge"); break;
            case "1":
                holder.event_type_icon_iv.setImageResource(R.drawable.ticket_confirmation);
                holder.event_type_tv.setText("Discount"); break;
            case "2":
                holder.event_type_icon_iv.setImageResource(R.drawable.compass);
                holder.event_type_tv.setText("Explore"); break;
        }
    }

    private void explore_button_def(ViewHolder holder, Feature feature)
    {
        holder.explore_event.setOnClickListener(v ->
        {
            Intent intent = new Intent(holder.explore_event.getContext(), Selected_Event_Activity.class);
            intent.putExtra("feature", feature.toJson());
            holder.explore_event.getContext().startActivity(intent);

        });
    }

    void set_date(ViewHolder holder, String date_start, String date_end)
    {
        String date_start_modified = Date_Time_Provider.date_to_MD(date_start);
        String date_end_modified = Date_Time_Provider.date_to_MD(date_end);
        String date = date_start_modified + " to " + date_end_modified;

        holder.event_date_tv.setText(date);
    }

    void set_event_wallpaper(ViewHolder holder, String postfix)
    {
        Target target = new Target()
        {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
            {holder.event_wallpaper.setImageBitmap(bitmap);}

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable)
            {}

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable)
            {}
        };

        String path = holder.event_wallpaper.getContext().getString(R.string.event_wallpaper_url)
                + postfix+ "_wp.jpg";

        Picasso.get().invalidate(path);
        Picasso.get().load(path).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(target);
    }

    void set_business_logo(ViewHolder holder, String profile_pic_name)
    {
        Bitmap profile_pic = Image_Provider.get_profile_bmp(profile_pic_name);

        if(profile_pic!=null)
            holder.business_logo_civ.setImageBitmap(profile_pic);
    }

    @Override
    public int getItemCount()
    {
        return feature_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final TextView header_title_tv;
        public final TextView event_description_tv;
        public final TextView event_type_tv;
        public final TextView event_date_tv;
        public final TextView event_joined_tv;
        public final ImageView event_type_icon_iv;
        public final ImageView event_wallpaper;
        public final CircleImageView business_logo_civ;
        public final Button explore_event;
        public final CardView card_layout;
        public Feature feature;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            header_title_tv = view.findViewById(R.id.header_title_tv);
            event_description_tv = view.findViewById(R.id.event_description_tv);
            business_logo_civ = view.findViewById(R.id.business_logo_civ);
            event_type_tv = view.findViewById(R.id.event_type_tv);
            event_date_tv = view.findViewById(R.id.event_date_tv);
            event_joined_tv = view.findViewById(R.id.event_joined_tv);
            event_type_icon_iv = view.findViewById(R.id.event_type_icon_iv);
            event_wallpaper = view.findViewById(R.id.event_wallpaper);
            explore_event = view.findViewById(R.id.explore_event);
            card_layout = view.findViewById(R.id.card_layout);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + event_description_tv.getText() + "'";
        }
    }
}