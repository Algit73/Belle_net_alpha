package com.soluk.belle_net_alpha.event_data_maker;

import com.google.gson.JsonObject;
import com.soluk.belle_net_alpha.main_activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class geo_JSON_maker
{
    private JSONObject features_object;
    private JSONArray features_child_array;
    private static final String TAG = main_activity.class.getSimpleName();




    public geo_JSON_maker()
    {
        features_object = new JSONObject();
        features_child_array = new JSONArray();
        try
        {
            features_object.put("type","FeatureCollection");

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    public JSONObject get_features_object()
    {
        return features_object;
    }

    public void add_feature(JSONObject feature)
    {
        ///// GeoJson Properties /////
        JSONObject properties = new JSONObject();
        try
        {
            properties.put("marker-color", "#D81B60");
            properties.put("marker-size", "medium");
            properties.put("marker-symbol", "");
            properties.put("name", feature.get("name"));
            properties.put("family", feature.get("family"));
            properties.put("event_id", feature.get("event_unique_id"));
            properties.put("date_created", feature.get("date_created"));
            properties.put("event_date", feature.get("date_of_event"));
            properties.put("profile_pic", feature.get("user_picture"));
            properties.put("is_user_joined", feature.get("is_user_joined"));
            properties.put("count", feature.get("count "));

        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ////// GeoJson Geometry ///////

        JSONObject geometry = new JSONObject();
        JSONArray geo_lat_long = new JSONArray();

        try
        {
            geo_lat_long.put(feature.get("longitude_pinned"));
            geo_lat_long.put(feature.get("latitude_pinned"));
            geometry.put("type", "Point");
            geometry.put("coordinates",geo_lat_long);

        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        ////// GeoJson Features' Child ///////


        JSONObject features_child_object = new JSONObject();
        try
        {
            features_child_object.put("type", "Feature");
            features_child_object.put("properties",properties);
            features_child_object.put("geometry",geometry);
        }
        catch (JSONException e)
        {
            e.printStackTrace();

        }


        features_child_array.put(features_child_object);



        try
        {
            features_object.put("type","FeatureCollection");
            features_object.put("features",features_child_array);
            //return features_object;

        }
        catch (JSONException e)
        {
            e.printStackTrace();
            //return null;
        }

        /*


         */
    }
}
