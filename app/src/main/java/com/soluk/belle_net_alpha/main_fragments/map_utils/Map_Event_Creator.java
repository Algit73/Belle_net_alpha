package com.soluk.belle_net_alpha.main_fragments.map_utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
//import com.mapbox.mapboxsdk.maps.map_fragment.mapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.main_fragments.Map_Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_BOTTOM;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_TYPE;

public class Map_Event_Creator
{
    private final String TAG = "Map_Event_Creator";
    public static final String PROPERTY_SELECTED = "selected";
    public final int EVENT_TYPE_ENSEMBLE = 0;
    public final int EVENT_TYPE_CHALLENGE = 1;
    public final int EVENT_TYPE_EXPERIENCE = 2;
    public static final String GEOJSON_SOURCE_ID_ENSEMBLE = "GEOJSON_SOURCE_ID_ENSEMBLE";
    public static final String GEOJSON_SOURCE_ID_CHALLENGE = "GEOJSON_SOURCE_ID_CHALLENGE";
    public static final String MARKER_IMAGE_ID_ENSEMBLE = "MARKER_IMAGE_ID_ENSEMBLE";
    public static final String MARKER_IMAGE_ID_CHALLENGE = "MARKER_IMAGE_ID_CHALLENGE";
    public static final String CALLOUT_LAYER_ID_ENSEMBLE = "CALLOUT_LAYER_ID_ENSEMBLE";
    public static final String CALLOUT_LAYER_ID_CHALLENGE = "CALLOUT_LAYER_ID_CHALLENGE";
    public static final String MARKER_LAYER_ID_ENSEMBLE = "MARKER_LAYER_ID_ENSEMBLE";
    public static final String MARKER_LAYER_ID_CHALLENGE = "MARKER_LAYER_ID_CHALLENGE";


    private FeatureCollection feature_collection;
    private GeoJsonSource source_ensemble;
    private GeoJsonSource source_challenge;
    private GeoJsonSource source;

    //private map_fragment.mapboxMap map_fragment.mapboxMap;
    private final Context context;

    //public Map_Event_Creator(map_fragment.mapboxMap map_fragment.mapboxMap, Context context)
    public Map_Event_Creator(Context context)
    {
        //this.map_fragment.mapboxMap = map_fragment.mapboxMap;
        this.context = context;
    }

    public FeatureCollection get_feature_collection()
    {
        return feature_collection;
    }


    public void set_feature_collection(final FeatureCollection collection)
    {
        feature_collection = collection;
    }

    public void set_active_event_to_selected_point(int index)
    {
        if (feature_collection.features() != null)
        {
            Feature feature = feature_collection.features().get(index);
            set_feature_select_state(feature, true);
            refreshSource();
        }
    }

    public boolean feature_select_status(int index)
    {
        if (feature_collection == null)
        {
            return false;
        }
        return feature_collection.features().get(index).getBooleanProperty(PROPERTY_SELECTED);
    }

    public void set_feature_select_state(Feature feature, boolean selectedState)
    {
        Log.d(TAG,"set_feature_select_state: "+ selectedState);
        if (feature.properties() != null)
        {
            feature.properties().addProperty(PROPERTY_SELECTED, selectedState);
            refreshSource();
        }
    }

    /**
     * Updates the display of data on the map after the FeatureCollection has been modified
     */
    private void refreshSource()
    {

        if(feature_collection != null)
        {

            FeatureCollection feature_collection_ensemble;
            FeatureCollection feature_collection_others;
            List<Feature> features_ensemble = new ArrayList<>();
            List<Feature> features_others = new ArrayList<>();

            for (Feature singleFeature : feature_collection.features())
            {
                if (singleFeature.properties().get(EVENT_TYPE).toString().equals("\"0\""))
                    features_ensemble.add(singleFeature);
                else
                    features_others.add(singleFeature);
                Log.d(TAG, "Single_Feature: " + singleFeature.properties());
            }


            if (source_ensemble != null && (features_ensemble.size() != 0))
            {
                feature_collection_ensemble = FeatureCollection.fromFeatures(features_ensemble);
                source_ensemble.setGeoJson(feature_collection_ensemble);
            }

            if (source_challenge != null && (features_others.size() != 0))
            {
                feature_collection_others = FeatureCollection.fromFeatures(features_others);
                source_challenge.setGeoJson(feature_collection_others);
            }

        }

    }

    /**
     * Sets up all of the sources and layers needed for this example
     *
     * @param collection the FeatureCollection to set equal to the globally-declared FeatureCollection
     */
    public void setUpData(final FeatureCollection collection, int type)
    {
        if (Map_Fragment.mapboxMap != null)
        {
            Log.d(TAG,"setUpData: map_fragment.mapboxMap != null");
            if(type==EVENT_TYPE_ENSEMBLE)
            {
                Log.d(TAG,"setUpData: EVENT_TYPE_ENSEMBLE");
                Map_Fragment.mapboxMap.getStyle(style ->
                {
                    style.removeSource(GEOJSON_SOURCE_ID_ENSEMBLE);
                    style.removeLayer(GEOJSON_SOURCE_ID_ENSEMBLE);
                    setupSource(style, collection, type);
                    setUpImage(style, type);
                    setUpMarkerLayer(style, type);
                    setUpInfoWindowLayer(style, type);
                });
            }
            else
            {
                Log.d(TAG,"setUpData: Others");
                Map_Fragment.mapboxMap.getStyle(style ->
                {
                    style.removeSource(GEOJSON_SOURCE_ID_CHALLENGE);
                    style.removeLayer(GEOJSON_SOURCE_ID_CHALLENGE);
                    setupSource(style, collection, type);
                    setUpImage(style, type);
                    setUpMarkerLayer(style, type);
                    setUpInfoWindowLayer(style, type);
                });
            }
        }

    }

    /**
     * Adds the GeoJSON source to the map
     */
    private void setupSource(@NonNull Style loadedStyle, final FeatureCollection collection, int type)
    {
        Log.v(TAG,"source was not null");
        if(type==EVENT_TYPE_ENSEMBLE)
        {
            //source = new GeoJsonSource(GEOJSON_SOURCE_ID_ENSEMBLE, collection);
            source_ensemble = new GeoJsonSource(GEOJSON_SOURCE_ID_ENSEMBLE, collection);
            loadedStyle.addSource(source_ensemble);
        }
        else if(type==EVENT_TYPE_CHALLENGE)
        {
            source = new GeoJsonSource(GEOJSON_SOURCE_ID_CHALLENGE, collection);
            source_challenge = new GeoJsonSource(GEOJSON_SOURCE_ID_CHALLENGE, collection);
            loadedStyle.addSource(source_challenge);
        }
    }

    /**
     * Adds the marker image to the map for use as a SymbolLayer icon
     */
    private void setUpImage(@NonNull Style loadedStyle, int type)
    {
        Log.d(TAG,"setUpImage called");
        if(type==EVENT_TYPE_ENSEMBLE)
            loadedStyle.addImage(MARKER_IMAGE_ID_ENSEMBLE, BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.bicycle_32));
        else if(type==EVENT_TYPE_CHALLENGE)
            loadedStyle.addImage(MARKER_IMAGE_ID_CHALLENGE, BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.goal_32));
    }

    /**
     * Setup a layer with maki icons, eg. west coast city.
     */
    private void setUpMarkerLayer(@NonNull Style loadedStyle, int type)
    {
        if(type==EVENT_TYPE_ENSEMBLE)
            loadedStyle.addLayer(new SymbolLayer(MARKER_LAYER_ID_ENSEMBLE, GEOJSON_SOURCE_ID_ENSEMBLE)
                    .withProperties(
                            iconImage(MARKER_IMAGE_ID_ENSEMBLE),
                            iconAllowOverlap(true),
                            iconOffset(new Float[] {0f, -8f})
                    ));
        else if(type==EVENT_TYPE_CHALLENGE)
            loadedStyle.addLayer(new SymbolLayer(MARKER_LAYER_ID_CHALLENGE, GEOJSON_SOURCE_ID_CHALLENGE)
                    .withProperties(
                            iconImage(MARKER_IMAGE_ID_CHALLENGE),
                            iconAllowOverlap(true),
                            iconOffset(new Float[] {0f, -8f})
                    ));

    }

    /**
     * Invoked when the bitmaps have been generated from a view.
     */
    public void setImageGenResults(HashMap<String, Bitmap> imageMap)
    {
        if (Map_Fragment.mapboxMap != null)
        {
            Map_Fragment.mapboxMap.getStyle(style ->
            {
                // calling addImages is faster as separate addImage calls for each bitmap.
                style.addImages(imageMap);
            });
        }
    }

    /**
     * Setup a layer with Android SDK call-outs
     * <p>
     * name of the feature is used as key for the iconImage
     * </p>
     */
    private void setUpInfoWindowLayer(@NonNull Style loadedStyle, int type)
    {
        if(type==EVENT_TYPE_ENSEMBLE)
            loadedStyle.addLayer(new SymbolLayer(CALLOUT_LAYER_ID_ENSEMBLE, GEOJSON_SOURCE_ID_ENSEMBLE)
                    .withProperties(
                            /* show image with id title based on the value of the event_id feature property */
                            iconImage("{event_id}"),

                            /* set anchor of icon to bottom */
                            iconAnchor(ICON_ANCHOR_BOTTOM),

                            /* all info window and marker image to appear at the same time*/
                            iconAllowOverlap(true),

                            /* offset the info window to be above the marker */
                            iconOffset(new Float[] {-2f, -28f})
                    )
                    /* add a filter to show only when selected feature property is true */
                    .withFilter(eq((get(PROPERTY_SELECTED)), literal(true))));

        else if(type==EVENT_TYPE_CHALLENGE)
            loadedStyle.addLayer(new SymbolLayer(CALLOUT_LAYER_ID_CHALLENGE, GEOJSON_SOURCE_ID_CHALLENGE)
                    .withProperties(
                            /* show image with id title based on the value of the event_id feature property */
                            iconImage("{event_id}"),

                            /* set anchor of icon to bottom */
                            iconAnchor(ICON_ANCHOR_BOTTOM),

                            /* all info window and marker image to appear at the same time*/
                            iconAllowOverlap(true),

                            /* offset the info window to be above the marker */
                            iconOffset(new Float[] {-2f, -28f})
                    )
                    /* add a filter to show only when selected feature property is true */
                    .withFilter(eq((get(PROPERTY_SELECTED)), literal(true))));
    }
}
