package com.soluk.belle_net_alpha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.soluk.belle_net_alpha.http_requests.SimpleHttp;
import com.soluk.belle_net_alpha.http_requests.SimpleHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_BOTTOM;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class main_activity extends AppCompatActivity implements
        OnMapReadyCallback, MapboxMap.OnMapClickListener,MapboxMap.OnMapLongClickListener
{

    private static final String TAG = main_activity.class.getSimpleName();
    private static final String GEOJSON_SOURCE_ID = "GEOJSON_SOURCE_ID";
    private static final String GEOJSON_SOURCE_ID_2 = "GEOJSON_SOURCE_ID_2";
    private static final String MARKER_IMAGE_ID = "MARKER_IMAGE_ID";
    private static final String MARKER_LAYER_ID = "MARKER_LAYER_ID";
    private static final String CALLOUT_LAYER_ID = "CALLOUT_LAYER_ID";
    private static final String PROPERTY_SELECTED = "selected";
    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_FAMILY = "family";
    private static final String PROPERTY_DATE_CREATED = "date_created";
    private static final String PROPERTY_EVENT_DATE = "event_date";
    private static final String PROPERTY_IS_USER_JOINED = "is_user_joined";
    private static final String PROPERTY_PIC = "profile_pic";
    private static final String USER_NAME = "Alireza Alikhani";
    private static final String USER_PIC = "#loncle";
    private static final String USER_ID = "#ahdx98!s5kjxsp";
    private MapView mapView;
    private MapboxMap mapboxMap;
    private SymbolManager symbol_manager;
    private GeoJsonSource source;
    private FeatureCollection featureCollection;
    private int number_of_selected_cards;
    private FloatingActionButton add_pin_on_map;
    private Spinner list_of_challenges;
    private BottomSheetBehavior bottom_sheet_Choose_challenge;
    private Button save_challenge;
    private Button cancel_challenge;
    private EditText edit_date_time;
    private final Calendar myCalendar = Calendar.getInstance();
    private boolean active_user_pin_mode;
    private static final String FILE_NAME = "geo_json_bellenet";
    private static String file_directory_static = "";
    private geo_JSON_maker feature_maker = null;
    private file_maker geo_json_holder = null;
    //private static final File file_dire = file_directory.st;
    private boolean add_postition_mode = false;
    private boolean is_marker_added = false;
    private boolean data_received_correctly = false;
    private Symbol user_marker_pinned;
    private LatLng selected_postion;

    private CircleImageView profile_test;
    private ImageView profile_test_2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Map<String, String> params = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Typ", "application/json; utf-8");
        params.put("user_id",USER_ID);


        SimpleHttp.post(getString(R.string.events_info_url), params, new SimpleHttpResponseHandler()
        {
            @Override
            public void onResponse(int result_code, String response_body)
            {

                try
                {
                    Log.d(TAG, "Post Code: "+result_code);
                    Log.d(TAG, "Post Body: "+response_body);
                    if(result_code==200)
                    {
                        JSONArray received_events = new JSONArray(response_body);
                        add_received_events(received_events);
                        data_received_correctly=true;
                    }

                }
                catch (Exception e)
                {
                    Log.d(TAG, "on Post Response: "+e.getMessage());
                }
            }
        });

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);

        // Initialize the map view
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        add_pin_on_map = findViewById(R.id.fab_pin_on_maps);
        list_of_challenges = findViewById(R.id.list_of_challanges);
        View bottom_sheet_challenges_veiw = findViewById(R.id.bottom_sheet_challenges);
        save_challenge = findViewById(R.id.save_challenge);
        cancel_challenge = findViewById(R.id.cancel_challenge);
        edit_date_time = findViewById(R.id.edit_date);


        // Configuring BottomSheet in the BelleNet
        bottom_sheet_Choose_challenge = BottomSheetBehavior.from(bottom_sheet_challenges_veiw);
        ArrayAdapter<CharSequence> list_of_challenges_adapter = ArrayAdapter.createFromResource(this,
                R.array.list_of_challenges_items, android.R.layout.simple_spinner_item);
        list_of_challenges_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        list_of_challenges.setAdapter(list_of_challenges_adapter);
        bottom_sheet_Choose_challenge.setState(BottomSheetBehavior.STATE_HIDDEN);

        add_pin_on_map.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                bottom_sheet_Choose_challenge.setState(BottomSheetBehavior.STATE_EXPANDED);
                add_postition_mode = true;
            }
        });

        // Configuring Buttons in the BottomSheet of BelleNet
        cancel_challenge.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                is_marker_added = false;
                add_postition_mode = false;
                bottom_sheet_Choose_challenge.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        save_challenge.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(is_marker_added)
                {
                    is_marker_added = false;
                    add_postition_mode = false;
                    save_user_created_event();
                    bottom_sheet_Choose_challenge.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
                else
                    Toast.makeText(main_activity.this,
                            "Please pin a location on map",Toast.LENGTH_SHORT).show();
            }
        });


        // Configuring Date and Time

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener()
        {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth)
            {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                update_edit_box();
            }

        };

        edit_date_time.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                new DatePickerDialog(main_activity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



    }

    @Override
    public boolean onMapClick(@NonNull LatLng point)
    {
        return handleClickIcon(mapboxMap.getProjection().toScreenLocation(point));
    }

    @Override
    public boolean onMapLongClick(@NonNull LatLng point)
    {
        if(add_postition_mode)  // Prohibit adding marker when the mode was not initiated
        {
            if (!is_marker_added)   // Check if any marker has been added by the user
            {
                selected_postion = point;
                user_marker_pinned = symbol_manager.create(new SymbolOptions()
                        .withLatLng(new LatLng(point.getLatitude(), point.getLongitude()))
                        .withIconImage("myMarker")
                        .withTextAnchor(Property.TEXT_ANCHOR_BOTTOM)
                        .withIconSize(1.0f));

                is_marker_added = true;
                //add_postition_mode = false;
            }
            else
            {
                symbol_manager.delete(user_marker_pinned);
                selected_postion = point;
                user_marker_pinned = symbol_manager.create(new SymbolOptions()
                        .withLatLng(new LatLng(point.getLatitude(), point.getLongitude()))
                        .withIconImage("myMarker")
                        .withTextAnchor(Property.TEXT_ANCHOR_BOTTOM)
                        .withIconSize(1.0f));

            }
        }
        return false;
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap)
    {
        this.mapboxMap = mapboxMap;



        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded()
        {
            @Override
            public void onStyleLoaded(@NonNull Style style)
            {
                if(data_received_correctly)
                  new LoadGeoJsonDataTask(main_activity.this).execute();
                mapboxMap.addOnMapClickListener(main_activity.this);
                mapboxMap.addOnMapLongClickListener(main_activity.this);
                symbol_manager = new SymbolManager(mapView, mapboxMap, style);
                symbol_manager.setIconAllowOverlap(true);
                style.addImage("myMarker", BitmapFactory.decodeResource(getResources(),R.drawable.blue_marker));
                //style.removeImage("myMarker");
            }
        });

    }

    void save_user_created_event()
    {
        symbol_manager.delete(user_marker_pinned);

        Date c = Calendar.getInstance().getTime();
        //System.out.println("Current time => " + c);
        Log.d(TAG, "Current Date Before " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = df.format(c);
        Log.d(TAG, "Current Date after " + formattedDate);

        JSONObject feature = new JSONObject();
        try
        {
            feature.put("name",USER_NAME);
            feature.put("date_created",formattedDate);
            feature.put("user_picture",USER_PIC);
            feature.put("longitude_pinned",selected_postion.getLongitude());
            feature.put("latitude_pinned",selected_postion.getLatitude());

        }
        catch (Exception e)
        {

        }

        //feature_maker.add_feature(USER_NAME,"February, 9",
        //      String.valueOf(selected_postion.getLongitude()), String.valueOf(selected_postion.getLatitude()),USER_PIC);
        //"-123","42");
        feature_maker.add_feature(feature);
        geo_json_holder.write(feature_maker.get_features_object());
        update_map();

    }

    private void add_received_events(JSONArray received_events)
    {
        String file_directory = main_activity.this.getFilesDir().toString();
        file_directory_static = file_directory;
        feature_maker = new geo_JSON_maker();
        for (int i=0;i<received_events.length();i++)
        {
            try
            {
                JSONObject event = new JSONObject(received_events.get(i).toString());
                //catch_profile_images(event.get("user_picture").toString().substring(1));
                Log.d(TAG,"profile prefix: "+event.get("user_picture").toString().substring(1));
                feature_maker.add_feature(event);
            }
            catch (Exception e)
            {
                Log.d(TAG,"data received: "+e.getMessage());
            }

        }



        geo_json_holder = new file_maker(file_directory,FILE_NAME);
        geo_json_holder.read();
        geo_json_holder.write(feature_maker.get_features_object());
        geo_json_holder.read();
        Log.v(TAG,"Feature: "+geo_json_holder.read().toString());
        FeatureCollection.fromJson(geo_json_holder.read().toString());
        update_map();

    }

    void update_map()
    {
        new LoadGeoJsonDataTask(main_activity.this).execute();
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded()
        {
            @Override
            public void onStyleLoaded(@NonNull Style style)
            {

                mapboxMap.addOnMapClickListener(main_activity.this);
                mapboxMap.addOnMapLongClickListener(main_activity.this);
                symbol_manager = new SymbolManager(mapView, mapboxMap, style);
                symbol_manager.setIconAllowOverlap(true);
                style.addImage("myMarker", BitmapFactory.decodeResource(getResources(),R.drawable.blue_marker));



            }
        });
    }

    private void update_edit_box()
    {
        String myFormat = "MMMM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edit_date_time.setText(sdf.format(myCalendar.getTime()));
    }

    /**
     * Sets up all of the sources and layers needed for this example
     *
     * @param collection the FeatureCollection to set equal to the globally-declared FeatureCollection
     */
    public void setUpData(final FeatureCollection collection)
    {
        featureCollection = collection;
        if (mapboxMap != null)
        {
            mapboxMap.getStyle(style ->
            {
                style.removeSource(GEOJSON_SOURCE_ID);
                style.removeLayer(GEOJSON_SOURCE_ID);
                setupSource(style);
                setUpImage(style);
                setUpMarkerLayer(style);
                setUpInfoWindowLayer(style);
            });

        }
    }

    /**
     * Adds the GeoJSON source to the map
     */
    private void setupSource(@NonNull Style loadedStyle)
    {
        Log.v(TAG,"source was not null");
        source = new GeoJsonSource(GEOJSON_SOURCE_ID, featureCollection);
        loadedStyle.addSource(source);

    }

    /**
     * Adds the marker image to the map for use as a SymbolLayer icon
     */
    private void setUpImage(@NonNull Style loadedStyle)
    {
        loadedStyle.addImage(MARKER_IMAGE_ID, BitmapFactory.decodeResource(
                this.getResources(), R.drawable.red_marker));
    }

    /**
     * Updates the display of data on the map after the FeatureCollection has been modified
     */
    private void refreshSource()
    {
        if (source != null && featureCollection != null)
        {
            source.setGeoJson(featureCollection);
        }
    }

    /**
     * Setup a layer with maki icons, eg. west coast city.
     */
    private void setUpMarkerLayer(@NonNull Style loadedStyle)
    {
        loadedStyle.addLayer(new SymbolLayer(MARKER_LAYER_ID, GEOJSON_SOURCE_ID)
                .withProperties(
                        iconImage(MARKER_IMAGE_ID),
                        iconAllowOverlap(true),
                        iconOffset(new Float[] {0f, -8f})
                ));
    }

    /**
     * Setup a layer with Android SDK call-outs
     * <p>
     * name of the feature is used as key for the iconImage
     * </p>
     */
    private void setUpInfoWindowLayer(@NonNull Style loadedStyle)
    {
        loadedStyle.addLayer(new SymbolLayer(CALLOUT_LAYER_ID, GEOJSON_SOURCE_ID)
                .withProperties(
                        /* show image with id title based on the value of the name feature property */
                        iconImage("{name}"),

                        /* set anchor of icon to bottom-left */
                        iconAnchor(ICON_ANCHOR_BOTTOM),

                        /* all info window and marker image to appear at the same time*/
                        iconAllowOverlap(true),

                        /* offset the info window to be above the marker */
                        iconOffset(new Float[] {-2f, -28f})
                )
                /* add a filter to show only when selected feature property is true */
                .withFilter(eq((get(PROPERTY_SELECTED)), literal(true))));
    }

    /**
     * This method handles click events for SymbolLayer symbols.
     * <p>
     * When a SymbolLayer icon is clicked, we moved that feature to the selected state.
     * </p>
     *
     * @param screenPoint the point on screen clicked
     */
    private boolean handleClickIcon(PointF screenPoint)
    {
        List<Feature> features = mapboxMap.queryRenderedFeatures(screenPoint, MARKER_LAYER_ID);
        if (!features.isEmpty())
        {
            String name = features.get(0).getStringProperty(PROPERTY_NAME);
            Log.v(TAG,"Feature Name: "+name);
            List<Feature> featureList = featureCollection.features();
            if (featureList != null)
            {
                Log.v(TAG,"Feature Size: "+featureList.size());
                for (int i = 0; i < featureList.size(); i++)
                {
                    if (featureList.get(i).getStringProperty(PROPERTY_NAME).equals(name))
                    {
                        Log.v(TAG,"featureList.get(i): "+featureList.get(i).getStringProperty(PROPERTY_NAME));
                        if (featureSelectStatus(i))
                        {
                            number_of_selected_cards--;
                            if(number_of_selected_cards==0)
                                set_camera_position(0);
                            setFeatureSelectState(featureList.get(i), false);
                            Log.v("TAG","Item Deselected");
                        }
                        else
                        {
                            //Timber.tag(TAG).v("Item Selected");
                            number_of_selected_cards++;
                            if(number_of_selected_cards>0)
                                set_camera_position(50);
                            setSelected(i);
                        }
                    }
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    private void set_camera_position(int tilt)
    {
        CameraPosition position = new CameraPosition.Builder()
                //.target(new LatLng(location.getLatitude(), location.getLongitude())) // Sets the new camera position
                //.zoom(12) // Sets the zoom
                //.bearing(degree) // Rotate the camera
                .tilt(tilt) // Set the camera tilt
                .build();

        mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 2000);
    }

    /**
     * Set a feature selected state.
     *
     * @param index the index of selected feature
     */
    private void setSelected(int index)
    {
        if (featureCollection.features() != null)
        {
            Feature feature = featureCollection.features().get(index);
            setFeatureSelectState(feature, true);
            refreshSource();
        }
    }

    /**
     * Selects the state of a feature
     *
     * @param feature the feature to be selected.
     */
    private void setFeatureSelectState(Feature feature, boolean selectedState)
    {
        if (feature.properties() != null)
        {
            feature.properties().addProperty(PROPERTY_SELECTED, selectedState);
            refreshSource();
        }
    }

    /**
     * Checks whether a Feature's boolean "selected" property is true or false
     *
     * @param index the specific Feature's index position in the FeatureCollection's list of Features.
     * @return true if "selected" is true. False if the boolean property is false.
     */
    private boolean featureSelectStatus(int index)
    {
        if (featureCollection == null)
        {
            return false;
        }
        return featureCollection.features().get(index).getBooleanProperty(PROPERTY_SELECTED);
    }

    /**
     * Invoked when the bitmaps have been generated from a view.
     */
    public void setImageGenResults(HashMap<String, Bitmap> imageMap)
    {
        if (mapboxMap != null)
        {
            mapboxMap.getStyle(style ->
            {
                // calling addImages is faster as separate addImage calls for each bitmap.
                style.addImages(imageMap);
            });
        }
    }


    private static class LoadGeoJsonDataTask extends AsyncTask<Void, Void, FeatureCollection>
    {

        private final WeakReference<main_activity> activityRef;

        LoadGeoJsonDataTask(main_activity activity)
        {
            this.activityRef = new WeakReference<>(activity);
            Log.v(TAG,"LoadGeoJsonDataTask: constructor");

        }

        @Override
        protected FeatureCollection doInBackground(Void... params)
        {
            main_activity activity = activityRef.get();

            if (activity == null)
            {
                return null;
            }

            //String geoJson = loadGeoJsonFromAsset(activity, "bikers_pinned_events.geojson");
            Log.v(TAG,"Feature Collection Returned");
            //File file = null;
            file_maker geo_json_holder = new file_maker(file_directory_static,FILE_NAME);
            geo_json_holder.read();

            //return FeatureCollection.fromJson(geoJson);
            Log.v(TAG,"LoadGeoJsonDataTask: doInBackground");
            return FeatureCollection.fromJson(geo_json_holder.read().toString());
        }

        @Override
        protected void onPostExecute(FeatureCollection featureCollection)
        {
            super.onPostExecute(featureCollection);
            Log.v(TAG,"On post execute");
            main_activity activity = activityRef.get();
            if (featureCollection == null || activity == null)
            {
                return;
            }

            // This example runs on the premise that each GeoJSON Feature has a "selected" property,
            // with a boolean value. If your data's Features don't have this boolean property,
            // add it to the FeatureCollection 's features with the following code:
            for (Feature singleFeature : featureCollection.features())
            {
                singleFeature.addBooleanProperty(PROPERTY_SELECTED, false);
                Log.v(TAG,"PROPERTY_SELECTED, False");
            }

            activity.setUpData(featureCollection);
            GenerateViewIconTask generateViewIconTask=null;
            Log.v(TAG,"LoadGeoJsonDataTask: onPostExecute");
            new GenerateViewIconTask(activity).execute(featureCollection);
            //generateViewIconTask = (GenerateViewIconTask) new GenerateViewIconTask(activity).execute(featureCollection);
            //generateViewIconTask.get().clear();
        }

        static String loadGeoJsonFromAsset(Context context, String filename)
        {
            try
            {
                // Load GeoJSON file from local asset folder
                InputStream is = context.getAssets().open(filename);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                return new String(buffer, Charset.forName("UTF-8"));
            }
            catch (Exception exception)
            {
                throw new RuntimeException(exception);
            }
        }
    }

    /**
     * AsyncTask to generate Bitmap from Views to be used as iconImage in a SymbolLayer.
     * <p>
     * Call be optionally be called to update the underlying data source after execution.
     * </p>
     * <p>
     * Generating Views on background thread since we are not going to be adding them to the view hierarchy.
     * </p>
     */
    private static class GenerateViewIconTask extends AsyncTask<FeatureCollection, Void, HashMap<String, Bitmap>>
    {

        private final HashMap<String, View> viewMap = new HashMap<>();
        private final WeakReference<main_activity> activityRef;
        private final boolean refreshSource;
        private String path;
        private Context context;

        GenerateViewIconTask(main_activity activity, boolean refreshSource)
        {
            this.activityRef = new WeakReference<>(activity);
            this.refreshSource = refreshSource;
            //this.path = activity.getString(R.string.profile_pic_url);
            this.context = activity.getApplicationContext();
            Log.v(TAG,"GenerateViewIconTask: Constructor_1");
        }

        GenerateViewIconTask(main_activity activity)
        {
            this(activity, false);
            Log.v(TAG,"GenerateViewIconTask: Constructor_2");
        }

        private String date_reformat(String time)
        {
            String inputPattern = "yyyy-MM-dd";
            String outputPattern = "MMMM,dd,yy";
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

        /*
        private  File getOutputMediaFile(String postfix)
        {

            File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                    + "/Android/data/"
                    + "com.soluk.bellenet_series_1"
                    + "/profiles");
            Log.d(TAG, "Address: " + mediaStorageDir);



            // Create the storage directory if it does not exist
            if (! mediaStorageDir.exists())
            {
                if (! mediaStorageDir.mkdirs())
                {
                    return null;
                }
            }

            // Create a media file name
            File mediaFile;
            String mImageName = postfix;
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
            return mediaFile;
        }

         */

        @SuppressWarnings("WrongThread")
        @Override
        protected HashMap<String, Bitmap> doInBackground(FeatureCollection... params)
        {
            main_activity activity = activityRef.get();
            if (activity != null)
            {
                HashMap<String, Bitmap> imagesMap = new HashMap<>();
                LayoutInflater inflater = LayoutInflater.from(activity);

                FeatureCollection featureCollection = params[0];

                for (Feature feature : featureCollection.features())
                {

                    @SuppressLint("InflateParams") ConstraintLayout constraint_layout =
                            (ConstraintLayout) inflater.inflate(R.layout.event_id_card, null);
                    //BubbleLayout bubbleLayout = (BubbleLayout)
                    //      inflater.inflate(R.layout.symbol_layer_info_window_layout_callout, null);

                    String name = feature.getStringProperty(PROPERTY_NAME);
                    //TextView titleTextView = bubbleLayout.findViewById(R.id.info_window_title);
                    TextView titleTextView = constraint_layout.findViewById(R.id.invitor_name);
                    titleTextView.setText(name);

                    String date_creation = feature.getStringProperty(PROPERTY_DATE_CREATED);
                    //TextView descriptionTextView = bubbleLayout.findViewById(R.id.info_window_description);
                    TextView created_date = constraint_layout.findViewById(R.id.created_date);
                    //descriptionTextView.setText(
                    //      String.format(activity.getString(R.string.capital), date));
                    created_date.setText(date_reformat(date_creation));

                    String date_of_event = feature.getStringProperty(PROPERTY_EVENT_DATE);
                    TextView event_date = constraint_layout.findViewById(R.id.event_date);
                    //descriptionTextView.setText(
                    //      String.format(activity.getString(R.string.capital), date));
                    Log.d(TAG, "Jakesh e Sag " + name+" va:"+ date_of_event);
                    event_date.setText(date_reformat(date_of_event)); //
                    //


                    //String path = this.path + feature.getStringProperty(PROPERTY_PIC).substring(1)+".jpg";
                    //Log.v(TAG,"Profile Pic Path: "+path);


                    /*
                    File file = getOutputMediaFile(feature.getStringProperty(PROPERTY_PIC).substring(1));
                    if(file.exists())
                        Log.d(TAG,"File Exists Final: " + "Yes");
                    else
                        Log.d(TAG,"File Exists Final: " + "No");
                    Bitmap profile_bitmap = BitmapFactory.decodeFile(String.valueOf(file));
                    if(profile_bitmap!=null)
                        Log.d(TAG,"bitmap is not null ");
                    else
                        Log.d(TAG,"bitmap is null ");

                     */


                    //CircleImageView profile_image_2 = constraint_layout.findViewById(R.id.profile_image_2);
                    //profile_image_2.setImageBitmap(profile_bitmap);

                    int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    //bubbleLayout.measure(measureSpec, measureSpec);
                    constraint_layout.measure(measureSpec, measureSpec);

                    //float measuredWidth = bubbleLayout.getMeasuredWidth();

                    //bubbleLayout.setArrowPosition(measuredWidth / 2 - 5);
                    //constraint_layout.setArrowPosition(measuredWidth / 2 - 5);

                    //Bitmap bitmap = SymbolGenerator.generate(bubbleLayout);
                    Bitmap bitmap = SymbolGenerator.generate(constraint_layout);
                    imagesMap.put(name, bitmap);
                    viewMap.put(name, constraint_layout);
                }
                Log.v(TAG,"GenerateViewIconTask: doInBackground");
                return imagesMap;
            }
            else
            {
                Log.v(TAG,"GenerateViewIconTask: doInBackground Null");
                return null;
            }
        }

        @Override
        protected void onPostExecute(HashMap<String, Bitmap> bitmapHashMap)
        {
            super.onPostExecute(bitmapHashMap);
            main_activity activity = activityRef.get();
            if (activity != null && bitmapHashMap != null)
            {
                activity.setImageGenResults(bitmapHashMap);
                if (refreshSource)
                {
                    activity.refreshSource();
                }
            }
            Log.v(TAG,"GenerateViewIconTask: onPostExecute");
            //Toast.makeText(activity, R.string.tap_on_marker_instruction, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Utility class to generate Bitmaps for Symbol.
     */
    private static class SymbolGenerator
    {

        /**
         * Generate a Bitmap from an Android SDK View.
         *
         * @param view the View to be drawn to a Bitmap
         * @return the generated bitmap
         */
        static Bitmap generate(@NonNull View view)
        {
            int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            view.measure(measureSpec, measureSpec);

            int measuredWidth = view.getMeasuredWidth();
            int measuredHeight = view.getMeasuredHeight();

            view.layout(0, 0, measuredWidth, measuredHeight);
            Bitmap bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.TRANSPARENT);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            return bitmap;
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mapboxMap != null)
        {
            mapboxMap.removeOnMapClickListener(this);
        }
        mapView.onDestroy();
    }
}