package com.soluk.belle_net_alpha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
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
import com.google.gson.JsonObject;
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
import com.soluk.belle_net_alpha.event_data_maker.file_maker;
import com.soluk.belle_net_alpha.event_data_maker.geo_JSON_maker;
import com.soluk.belle_net_alpha.http_requests.SimpleHttp;
import com.soluk.belle_net_alpha.http_requests.SimpleHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private static final String MARKER_IMAGE_ID = "MARKER_IMAGE_ID";
    private static final String MARKER_LAYER_ID = "MARKER_LAYER_ID";
    private static final String CALLOUT_LAYER_ID = "CALLOUT_LAYER_ID";
    private static final String PROPERTY_SELECTED = "selected";
    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_FAMILY = "family";
    private static final String PROPERTY_DATE_CREATED = "date_created";
    private static final String PROPERTY_EVENT_DATE = "event_date";
    private static final String PROPERTY_EVENT_ID = "event_id";
    private static final String PROPERTY_IS_USER_JOINED = "is_user_joined";
    private static final String PROPERTY_PIC = "profile_pic";
    private static final String PROPERTY_NUM_OF_JOINED = "count";
    private static final String USER_NAME = "Alireza";
    private static final String USER_FAMILY = "Alikhani";
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
    private BottomSheetBehavior bottom_sheet_click_join;
    private Button save_challenge;
    private Button cancel_challenge;
    private EditText edit_date_time;
    private final Calendar calendar_date_picker = Calendar.getInstance();
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

    private boolean join_status = false;
    private String  last_event_selected;

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
        View bottom_sheet_click_join_view = findViewById(R.id.bottom_sheet_click_to_join);
        save_challenge = findViewById(R.id.save_challenge);
        cancel_challenge = findViewById(R.id.cancel_challenge);
        edit_date_time = findViewById(R.id.edit_date);


        /// Configuring BottomSheet_Choose_Challenge in the BelleNet
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

        /// Configuring BottomSheet_Choose_Challenge in the BelleNet
        bottom_sheet_click_join = BottomSheetBehavior.from(bottom_sheet_click_join_view);
        bottom_sheet_click_join.setState(BottomSheetBehavior.STATE_HIDDEN);





        // Configuring Date and Time

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener()
        {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth)
            {
                // TODO Auto-generated method stub
                calendar_date_picker.set(Calendar.YEAR, year);
                calendar_date_picker.set(Calendar.MONTH, monthOfYear);
                calendar_date_picker.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                update_edit_box();
            }

        };

        edit_date_time.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                new DatePickerDialog(main_activity.this, date, calendar_date_picker
                        .get(Calendar.YEAR), calendar_date_picker.get(Calendar.MONTH),
                        calendar_date_picker.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }


    private void update_edit_box()
    {
        SimpleDateFormat us_date_format = new SimpleDateFormat("MMMM,dd,yyyy", Locale.US);
        edit_date_time.setText(us_date_format.format(calendar_date_picker.getTime()));
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

        Date current_date = Calendar.getInstance().getTime();
        SimpleDateFormat us_date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);    // Date Styles may should change to the getDefault()

        JSONObject feature = new JSONObject();

        try
        {
            feature.put("name",USER_NAME);
            feature.put("family",USER_FAMILY);
            feature.put("event_unique_id","123");   // It should be changed
            feature.put("date_created",us_date_format.format(current_date));
            feature.put("date_of_event",us_date_format.format(calendar_date_picker.getTime()));
            feature.put("user_picture",USER_PIC);
            feature.put("longitude_pinned",selected_postion.getLongitude());
            feature.put("latitude_pinned",selected_postion.getLatitude());
            Log.v(TAG, "user created event: OK");

        }
        catch (Exception e)
        {
            Log.v(TAG, "user failed to create event: "+e.getMessage());
        }


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
                catch_profile_images(event.get("user_picture").toString().substring(1));
                Log.d(TAG,"profile prefix: "+event.get("user_picture").toString().substring(1));
                feature_maker.add_feature(event);
            }
            catch (Exception e)
            {
                Log.d(TAG,"data received: "+e.getMessage());
            }

        }

        geo_json_holder = new file_maker(file_directory,FILE_NAME);
        geo_json_holder.write(feature_maker.get_features_object());

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

    private String saveToInternalStorage(Bitmap bitmapImage, String name)
    {
        ContextWrapper context_wrapper = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = context_wrapper.getDir("Profile_Pictures", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,name);
        Log.d(TAG,"Profile pic address "+mypath);

        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, fos);
        }
        catch (Exception e)
        {
            Log.d(TAG,"Int Strge Create File failed: "+e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            try
            {
                fos.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        Log.d(TAG,"Directory: "+ directory.getAbsolutePath());
        return directory.getAbsolutePath();
    }

    private void catch_profile_images(String postfix)
    {

        Log.d(TAG,"catch_profile_images: Entered");
        Target target = new Target()
        {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
            {

                Log.d(TAG,"BMP Received");
                saveToInternalStorage(bitmap,postfix);
                //store_image(bitmap,postfix);

                //retrieve_image(postfix);

            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable)
            {
                Log.d(TAG,"BMP Failed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable)
            {
                Log.d(TAG,"BMP onPrepareLoad");
            }
        };
        String path = getString(R.string.profile_pic_url)+postfix+".jpg";
        Log.d(TAG,"profile path: "+path);
        Picasso.get().load(path).into(target);
        Log.d(TAG,"catch_profile_images: Exited");
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
            String event_id = features.get(0).getStringProperty(PROPERTY_EVENT_ID);
            Log.v(TAG,"Feature Name: "+event_id);
            List<Feature> featureList = featureCollection.features();
            if (featureList != null)
            {
                Log.v(TAG,"Feature Size: "+featureList.size());
                for (int i = 0; i < featureList.size(); i++)
                {
                    if (featureList.get(i).getStringProperty(PROPERTY_EVENT_ID).equals(event_id))
                    {
                        Log.v(TAG,"featureList.get(i): "+featureList.get(i).getStringProperty(PROPERTY_NAME));
                        Log.d(TAG,"number_of_selected_cards: "+number_of_selected_cards);

                        if (featureSelectStatus(i))
                        {

                            if(featureList.get(i).getStringProperty(PROPERTY_EVENT_ID).equals(last_event_selected))
                            {
                                number_of_selected_cards--;
                                if(number_of_selected_cards==0)
                                    try
                                    {
                                        set_camera_position(0,null);
                                        bottom_sheet_click_join.setState(BottomSheetBehavior.STATE_HIDDEN);
                                    }
                                    catch (Exception e)
                                    {

                                    }

                                setFeatureSelectState(featureList.get(i), false);
                                Log.v(TAG,"Item Deselected");
                            }
                            else
                            {
                                last_event_selected = featureList.get(i).getStringProperty(PROPERTY_EVENT_ID);
                                try
                                {
                                    update_bottom_sheet_click(featureList.get(i));
                                }
                                catch (Exception e)
                                {

                                }

                            }


                        }
                        else
                        {
                            //Timber.tag(TAG).v("Item Selected");
                            last_event_selected = featureList.get(i).getStringProperty(PROPERTY_EVENT_ID);
                            number_of_selected_cards++;
                            if(number_of_selected_cards>0)
                            {

                                try
                                {
                                    JSONObject geometry = new JSONObject(featureList.get(i).geometry().toJson());
                                    update_bottom_sheet_click(featureList.get(i));
                                    //Log.d(TAG,"Feature selected data: "+featureList.get(i).properties());
                                    JSONArray point = new JSONArray(geometry.get("coordinates").toString());
                                    set_camera_position(50,point);
                                    //point.getJSONObject().ge
                                    //Log.d(TAG,"Feature selected data: "+point.get(0));
                                }
                                catch (Exception e)
                                {
                                    Log.d(TAG,"Feature selected Exception: "+e.getMessage());
                                }

                            }
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

    private void set_camera_position(int tilt,JSONArray point) throws JSONException
    {

        CameraPosition position = null;
        if(point!=null)
        position = new CameraPosition.Builder()
                    .target(new LatLng( (double)point.get(1), (double)point.get(0))) // Sets the new camera position
                    //.zoom(12) // Sets the zoom
                    //.bearing(degree) // Rotate the camera
                    .tilt(tilt) // Set the camera tilt
                    .build();
        else
            position = new CameraPosition.Builder()
                    .tilt(tilt) // Set the camera tilt
                    .build();

        mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 2000);
    }

    /// Each time a pin selected, the appropriate bottom_sheet appears
    private void update_bottom_sheet_click(Feature feature) throws JSONException
    {
        JSONObject info = new JSONObject(feature.properties().toString());
        bottom_sheet_click_join.setState(BottomSheetBehavior.STATE_EXPANDED);

        Button join_btn = findViewById(R.id.join_event);
        Button cancel_join_event_btn = findViewById(R.id.cancel_join_event);

        /// Setting name of the user
        TextView selected_user_name = findViewById(R.id.selected_user_name);
        String name_family = (String) (info.get("name")+ " " + info.get("family"));
        selected_user_name.setText(name_family);

        /// Setting the profile image of the selected_user
        CircleImageView selected_user_profile_image = findViewById(R.id.selected_user_profile_image);
        Bitmap selected_profile_image_bmp = get_profile_bmp(info.get("profile_pic").toString().substring(1));
        if(selected_profile_image_bmp!=null)
            selected_user_profile_image.setImageBitmap(selected_profile_image_bmp);

        /// Setting event date
        TextView selected_event_date = findViewById(R.id.selected_event_date);
        selected_event_date.setText(date_reformat(info.get("event_date").toString()));

        /// Setting event type
        TextView selected_event_type = findViewById(R.id.selected_event_type);
        String event_type = info.get("event_type").toString();
        if(event_type.equals("1"))
            selected_event_type.setText("Ensemble Riding");
        else if(event_type.equals("2"))
            selected_event_type.setText("Offering Challenge");
        if(event_type.equals("3"))
            selected_event_type.setText("Sharing Experience");

        /// Check if user is joined
        TextView selected_event_status = findViewById(R.id.selected_event_user_join_status);

        if(info.get("is_user_joined").toString().equals("true"))
        {
            selected_event_status.setText("You have joined this event");
            join_btn.setText("Opt out");
            join_status = true;

        }
        else
        {
            selected_event_status.setText("You have not joined this event");
            join_btn.setText("Join");
            join_status = false;
        }

        join_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(join_status)
                {

                }
                else
                {

                }
            }
        });

        /// Closing the bottom_sheet_click
        cancel_join_event_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                number_of_selected_cards--;
                bottom_sheet_click_join.setState(BottomSheetBehavior.STATE_HIDDEN);
                if(number_of_selected_cards==0)
                    try
                    {
                        set_camera_position(0,null);
                    }
                    catch (Exception e)
                    {

                    }

                setFeatureSelectState(feature, false);
            }
        });


        Log.d(TAG,"Feature selected data: "+info.toString());

    }

    private Bitmap get_profile_bmp(String name)
    {

        ContextWrapper cw = new ContextWrapper(this);
        File directory = cw.getDir("Profile_Pictures", Context.MODE_PRIVATE);
        try
        {
            File file =new File(directory, name);
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

            Log.v(TAG,"Feature Collection Returned");

            file_maker geo_json_holder = new file_maker(file_directory_static,FILE_NAME);
            //geo_json_holder.read();

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
        @SuppressLint("StaticFieldLeak")
        private final Context context;
        private boolean is_user_joined;

        GenerateViewIconTask(main_activity activity, boolean refreshSource)
        {
            this.activityRef = new WeakReference<>(activity);
            this.refreshSource = refreshSource;
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


        private Bitmap get_profile_bmp(String name)
        {

            ContextWrapper cw = new ContextWrapper(context);
            File directory = cw.getDir("Profile_Pictures", Context.MODE_PRIVATE);
            try
            {
                File file =new File(directory, name);
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                return bitmap;

            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
                return null;
            }

        }



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

                    /// Name Section
                    String name = feature.getStringProperty(PROPERTY_NAME);
                    TextView titleTextView = constraint_layout.findViewById(R.id.invitor_name);
                    titleTextView.setText(name);

                    /// Created Date Section
                    String date_creation = feature.getStringProperty(PROPERTY_DATE_CREATED);
                    TextView created_date = constraint_layout.findViewById(R.id.created_date);
                    created_date.setText(date_reformat(date_creation));

                    /// Event Date Section
                    String date_of_event = feature.getStringProperty(PROPERTY_EVENT_DATE);
                    TextView event_date = constraint_layout.findViewById(R.id.event_date);
                    event_date.setText(date_reformat(date_of_event)); //

                    /// Profile Pix Section
                    String profile_pic_name = feature.getStringProperty(PROPERTY_PIC).substring(1);
                    CircleImageView profile_image = constraint_layout.findViewById(R.id.profile_image);
                    Bitmap profile_pic = get_profile_bmp(profile_pic_name);
                    if(profile_pic!=null)
                        profile_image.setImageBitmap(profile_pic);

                    /// Number of Joined User Section
                    String num_of_joined = feature.getStringProperty(PROPERTY_NUM_OF_JOINED);
                    TextView number_of_joined_users_tv = constraint_layout.findViewById(R.id.num_of_joined);
                    number_of_joined_users_tv.setText(num_of_joined);

                    /// Is User Joined Section
                    String is_user_joined = feature.getStringProperty(PROPERTY_IS_USER_JOINED);





                    int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    constraint_layout.measure(measureSpec, measureSpec);


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