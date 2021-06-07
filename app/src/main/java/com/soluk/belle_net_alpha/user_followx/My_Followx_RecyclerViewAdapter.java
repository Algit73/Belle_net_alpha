package com.soluk.belle_net_alpha.user_followx;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.model.Events_DB_VM;
import com.soluk.belle_net_alpha.user_followx.dummy.DummyContent.DummyItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class My_Followx_RecyclerViewAdapter extends RecyclerView.Adapter<My_Followx_RecyclerViewAdapter.ViewHolder>
{

    private final List<FollowX_Object> followx_objects;

    public My_Followx_RecyclerViewAdapter(List<FollowX_Object> items)
    {
        followx_objects = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_followx, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        holder.followx = followx_objects.get(position);
        String user_name_family = holder.followx.getUser_name()+" "+holder.followx.getUser_family();
        holder.user_name_family_tv.setText(user_name_family);
        String path = Events_DB_VM.profile_pic_url + holder.followx.getUser_pic() + ".jpg";
        Picasso.get().load(path).into(holder.user_profile_image_civ);
    }

    @Override
    public int getItemCount()
    {
        return followx_objects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final TextView user_name_family_tv;
        public final CircleImageView user_profile_image_civ;
        public FollowX_Object followx;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            user_name_family_tv = view.findViewById(R.id.user_name_family_tv);
            user_profile_image_civ = view.findViewById(R.id.user_profile_image_civ);


        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + user_name_family_tv.getText() + "'";
        }
    }
}