package com.soluk.belle_net_alpha.search_users;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.model.Events_DB_VM;
import com.soluk.belle_net_alpha.search_users.dummy.DummyContent.DummyItem;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class Searched_Users_Recycler_View_Adapter extends RecyclerView.Adapter<Searched_Users_Recycler_View_Adapter.ViewHolder>
{

    private final List<Returned_User_Info_Object> mValues;
    String TAG = Searched_Users_Recycler_View_Adapter.class.getSimpleName();

    public Searched_Users_Recycler_View_Adapter(List<Returned_User_Info_Object> items)
    {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_search_users, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        holder.user_info = mValues.get(position);
        Log.d(TAG,holder.user_info.toString());

        String user_name_family = holder.user_info.getUser_name()+" "+holder.user_info.getUser_family();
        holder.user_name_family_tv.setText(user_name_family);

        String path = Events_DB_VM.profile_pic_url + holder.user_info.getUser_picture() + ".jpg";
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
                holder.user_profile_image_civ.setImageDrawable(AppCompatResources
                        .getDrawable(holder.user_profile_image_civ.getContext(),R.drawable.default_profile_pic));
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
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        //public final View mView;
        public final TextView user_name_family_tv;
        public final CircleImageView user_profile_image_civ;
        public Returned_User_Info_Object user_info;

        public ViewHolder(View view)
        {
            super(view);
            //mView = view;
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