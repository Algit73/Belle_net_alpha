package com.soluk.belle_net_alpha.event_data_maker;

import com.mapbox.geojson.Feature;
import com.soluk.belle_net_alpha.Main_Activity;
import com.soluk.belle_net_alpha.model.Events_DB_VM;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class geo_JSON_maker
{
    private JSONObject features_object;
    private JSONArray features_child_array;
    private static final String TAG = Main_Activity.class.getSimpleName();




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
            properties.put(Events_DB_VM.USER_ID, feature.get(Events_DB_VM.USER_ID));
            properties.put(Events_DB_VM.USER_NAME, feature.get(Events_DB_VM.USER_NAME));
            properties.put(Events_DB_VM.USER_FAMILY, feature.get(Events_DB_VM.USER_FAMILY));
            properties.put(Events_DB_VM.EVENT_NAME, feature.get(Events_DB_VM.EVENT_NAME));
            properties.put(Events_DB_VM.EVENT_ID, feature.get(Events_DB_VM.EVENT_ID));
            properties.put(Events_DB_VM.EVENT_CREATION_DATE, feature.get(Events_DB_VM.EVENT_CREATION_DATE));
            properties.put(Events_DB_VM.EVENT_DATE, feature.get(Events_DB_VM.EVENT_DATE));
            properties.put(Events_DB_VM.EVENT_DATE_END, feature.get(Events_DB_VM.EVENT_DATE_END));
            properties.put(Events_DB_VM.EVENT_TIME, feature.get(Events_DB_VM.EVENT_TIME));
            properties.put(Events_DB_VM.EVENT_TIME_END, feature.get(Events_DB_VM.EVENT_TIME_END));
            properties.put(Events_DB_VM.USER_PIC, feature.get(Events_DB_VM.USER_PIC));
            properties.put(Events_DB_VM.IS_USER_JOINED, feature.get(Events_DB_VM.IS_USER_JOINED));
            properties.put(Events_DB_VM.NUM_OF_JOINED, feature.get(Events_DB_VM.NUM_OF_JOINED));
            properties.put(Events_DB_VM.EVENT_TYPE, feature.get(Events_DB_VM.EVENT_TYPE));
            //properties.put("num_points", feature.get("num_points"));

            //int num_points = Integer.parseInt(feature.get("num_points").toString());
            for(int i=0;i<2;i++)
            {
                properties.put("longitude_"+i,feature.get("longitude_"+i));
                properties.put("latitude_"+i,feature.get("latitude_"+i));
            }
        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Feature feature1;


        ////// GeoJson Geometry ///////

        JSONObject geometry = new JSONObject();
        JSONArray geo_lat_long = new JSONArray();


        try
        {


            geo_lat_long.put(feature.get("longitude_0"));
            geo_lat_long.put(feature.get("latitude_0"));


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
