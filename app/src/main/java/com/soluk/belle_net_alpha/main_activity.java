package com.soluk.belle_net_alpha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
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
import com.mapbox.navigation.base.internal.route.RouteUrl;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.core.directions.session.RoutesRequestCallback;
import com.mapbox.navigation.ui.route.NavigationMapRoute;
import com.soluk.belle_net_alpha.event_data_maker.file_maker;
import com.soluk.belle_net_alpha.event_data_maker.geo_JSON_maker;
import com.soluk.belle_net_alpha.http_requests.SimpleHttp;
import com.soluk.belle_net_alpha.http_requests.SimpleHttpResponseHandler;
import com.soluk.belle_net_alpha.model.event_db_vm;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        OnMapReadyCallback, MapboxMap.OnMapClickListener,MapboxMap.OnMapLongClickListener, PermissionsListener
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
    private static final String USER_PASSWORD = "alirezalovesbellenet";
    /// Mapbox Variables
    private MapView mapView;
    private MapboxMap mapboxMap;
    private SymbolManager symbol_manager;
    private MapboxNavigation mapbox_navigation;
    private LocationComponent location_component;
    private NavigationMapRoute navigation_mapRoute;
    private GeoJsonSource source;
    private FeatureCollection featureCollection;
    private int number_of_selected_cards;

    private BottomSheetBehavior bottom_sheet_click_join;

    private SpeedDialView pin_event_sd;
    private FloatingActionButton set_cam_on_location_fab;

    /// Variables for sending event
    private BottomSheetBehavior bottom_sheet_Choose_challenge;
    private TextView event_date_tv;
    private TextView event_time_tv;
    //private final Calendar calendar_date_picker = Calendar.getInstance();
    private final Calendar calendar_time_picker = Calendar.getInstance();
    private ImageButton add_marker_on_map_ib;
    private ImageButton accept_marker_on_map_ib;
    private ImageButton reject_marker_on_map_ib;
    private final int EVENT_TYPE_ENSEMBLE = 0;
    private final int EVENT_TYPE_CHALLENGE = 1;
    private final int EVENT_TYPE_EXPERIENCE = 2;
    private int event_type;
    //private enum event_types {EVENT_TYPE_ENSEMBLE,EVENT_TYPE_CHALLENGE,EVENT_TYPE_EXPERIENCE};
    //private event_types event_type;
    private List<Point> list_of_added_points;
    private List<Symbol> list_of_of_added_points_symbol;


    private boolean active_user_pin_mode;
    private static final String FILE_NAME = "geo_json_bellenet";
    private static String file_directory_static = "";
    //private static final File file_dire = file_directory.st;
    private boolean add_postition_mode = false;
    private boolean is_marker_added = false;
    private boolean data_received_correctly = false;
    private Symbol user_marker_pinned;


    private boolean join_status = false;
    private String  last_event_selected;

    private FragmentTransaction fragment_transaction;
    private map_fragment map_fragment_instance;
    private event_list_fragment_parent event_list_parent_holder;

    private BottomNavigationView bottom_navigation_view;

    void get_db_updated()
    {
        number_of_selected_cards = 0;
        //if((mapboxMap!=null)&&(mapView!=null))
          //  set_camera_position(0,null);
        Map<String, String> params = new HashMap<>();
        params.put("user_id",USER_ID);

        SimpleHttp.post(getString(R.string.events_info_url), params, (result_code,response_body)->
        {
            try
            {
                Log.d(TAG, "Post Code: "+result_code); Log.d(TAG, "Post Body: "+response_body);
                if(result_code!=200)
                    return;
                    JSONArray received_events = new JSONArray(response_body);
                    add_received_events(received_events);
                    data_received_correctly=true;
            }
            catch (Exception e) {Log.d(TAG, "on Post Response: "+e.getMessage());}
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);


        map_fragment_instance = new map_fragment();
        event_list_parent_holder = new event_list_fragment_parent();
        //map_fragment_instance.
        //
        if (savedInstanceState == null)
        {
            fragment_transaction =  getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
            //.show(map_fragment_instance);
                    .add(R.id.fragment_map_view, map_fragment_instance, null)
                    //.add(R.id.fragment_event_list_holder, event_list_holder,null)
                    .add(R.id.fragment_event_list_parent, event_list_parent_holder,null)
                    //.hide(event_list_holder);
                    .hide(event_list_parent_holder);
            //fragment_transaction.show(map_fragment_instance);
            fragment_transaction.commit();
        }

        //map_fragment_instance = (map_fragment) getSupportFragmentManager()
          //      .findFragmentById(R.id.fragment_map_view);


        //get_db_updated();

        /// Initializing DB_Model

        String file_directory = main_activity.this.getFilesDir().toString();
        ContextWrapper context_wrapper = new ContextWrapper(getApplicationContext());
        File image_directory = context_wrapper.getDir("Profile_Pictures", Context.MODE_PRIVATE);
        event_db_vm model_db = new ViewModelProvider(this).get(event_db_vm.class);
        model_db.set_db_handler(this::update_map_fragment);
        model_db.init_db(file_directory,image_directory);



        /// Registering MapBox




        /// Initializing and configuring Bottom Navigation
        bottom_navigation_view = findViewById(R.id.bottom_navigation);

        bottom_navigation_view.setOnNavigationItemSelectedListener(item ->
        {
           switch (item.getItemId())
           {

               //assert map_fragment_instance != null;
               case R.id.page_map: //bottom_navigation_view.

                   fragment_transaction = getSupportFragmentManager().beginTransaction();
                   //fragment_transaction.detach(map_fragment_instance);
                   fragment_transaction.show(map_fragment_instance).hide(event_list_parent_holder).commit();
                   break;
               case R.id.page_events: //bottom_navigation_view.setSelectedItemId(R.id.page_events);

                   //fragment_transaction.detach(map_fragment_instance);
                   fragment_transaction = getSupportFragmentManager().beginTransaction();
                   fragment_transaction.show(event_list_parent_holder).hide(map_fragment_instance).commit();
                   break;
               case R.id.page_profile: //bottom_navigation_view.setSelectedItemId(R.id.page_profile);
                   break;
           }
            return true;
        });


        /*
        /// Initialize the map view
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

         */
        /*
        /// Configuring BottomSheet_Choose_Challenge in the BelleNet
        View bottom_sheet_click_join_view = findViewById(R.id.bottom_sheet_click_to_join);
        bottom_sheet_click_join = BottomSheetBehavior.from(bottom_sheet_click_join_view);
        bottom_sheet_click_join.setState(BottomSheetBehavior.STATE_HIDDEN);



        /// Configuring BottomSheet_Choose_Challenge in the BelleNet
        View bottom_sheet_challenges_veiw = findViewById(R.id.bottom_sheet_challenges);
        bottom_sheet_Choose_challenge = BottomSheetBehavior.from(bottom_sheet_challenges_veiw);
        bottom_sheet_Choose_challenge.setState(BottomSheetBehavior.STATE_HIDDEN);

         */

        /// Set camera on the user location
        set_cam_on_location_fab = findViewById(R.id.fab_pin_on_maps);
        set_cam_on_location_fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                    LatLng current_position = new LatLng(location_component.getLastKnownLocation().getLatitude()
                                                    ,location_component.getLastKnownLocation().getLongitude()) ;
                    set_camera_position(0,current_position);

            }
        });


        //button_sheet_add_event_init();
        //pin_event_sd_init();


        /*

        Button send_request = findViewById(R.id.send_request);
        send_request.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                update_map_fragment();
            }
        });

         */

    }

    void update_map_fragment()
    {
        Log.d(TAG,"Request From Activity to Fragment Sent");

        new Handler(Looper.getMainLooper()).postDelayed(()->
        {
            map_fragment_instance.update_map();
            /*
            Log.d(TAG,"After One Second");
            map_fragment fragment = (map_fragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map_view);
            assert fragment != null;
            fragment.update_map();

             */
            //map_fragment_instance.update_map();
            //fragment.
        }, 500);

    }

    void bottom_navigation_visibility(boolean visible)
    {
        if(visible)
            bottom_navigation_view.setVisibility(View.VISIBLE);
        else
            bottom_navigation_view.setVisibility(View.GONE);

    }

    void button_sheet_add_event_init()
    {

        // Configuring Buttons in the BottomSheet of BelleNet
        Button save_challenge_btn = findViewById(R.id.save_challenge);
        Button cancel_challenge_btn = findViewById(R.id.cancel_challenge);

        cancel_challenge_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                is_marker_added = false;
                add_postition_mode = false;
                change_add_marker_on_map_ib_mode(true);
                remove_points_routes();
                bottom_sheet_Choose_challenge.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        save_challenge_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(list_of_added_points==null)
                {
                    Toast.makeText(main_activity.this,
                            "Please add your spots on the map",Toast.LENGTH_SHORT).show();
                    return;
                }

                String date_value = event_date_tv.getText().toString();

                if(!date_value.matches(".*\\d.*"))
                {
                    Toast.makeText(main_activity.this,
                            "Please input the event date",Toast.LENGTH_SHORT).show();
                    return;
                }

                String time_value = event_time_tv.getText().toString();

                if(!time_value.matches(".*\\d.*"))
                {
                    Toast.makeText(main_activity.this,
                            "Please input the event time",Toast.LENGTH_SHORT).show();
                    return;
                }

                send_user_created_event();
                bottom_sheet_Choose_challenge.setState(BottomSheetBehavior.STATE_HIDDEN);
                remove_points_routes();
                change_add_marker_on_map_ib_mode(true);
            }
        });

        /// Configuring DatePicker
        event_date_tv = findViewById(R.id.input_date_tv);
        event_date_tv.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                date_time_provider date_provider = new date_time_provider(main_activity.this)
                                                    .set_date_tv(event_date_tv)
                                                    .set_date_format(date_time_provider.US);

                date_provider.show_date_dialog();
            }
        });

        /// Configuring TimePicker
        event_time_tv = findViewById(R.id.input_time_tv);

        event_time_tv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                date_time_provider time_provider = new date_time_provider(main_activity.this)
                        .set_time_tv(event_time_tv)
                        .set_time_format(date_time_provider.H24);

                time_provider.show_time_dialog();
            }
        });

        /// Configuring pinning on map
        add_marker_on_map_ib = findViewById(R.id.add_marker_ib);
        accept_marker_on_map_ib = findViewById(R.id.accept_pinned_markers_ib);
        reject_marker_on_map_ib = findViewById(R.id.reject_pinned_markers_ib);

        // Configuring Add Marker: 1- Adding points and route 2- Removing points and route
        add_marker_on_map_ib.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Check if any point has been added previously: null if not
                if(list_of_added_points!=null)
                {
                    remove_points_routes();
                    change_add_marker_on_map_ib_mode(true);
                }
                else
                {
                    bottom_sheet_Choose_challenge.setState(BottomSheetBehavior.STATE_HIDDEN);

                    accept_marker_on_map_ib.setVisibility(View.VISIBLE);
                    reject_marker_on_map_ib.setVisibility(View.VISIBLE);

                    set_cam_on_location_fab.setVisibility(View.INVISIBLE);
                    pin_event_sd.setVisibility(View.INVISIBLE);

                    add_postition_mode = true;
                }

            }
        });

        // Configuring accept button: accepts the points if user choses at least one
        accept_marker_on_map_ib.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Check if any point has been added, if not toasts a message
                if(list_of_of_added_points_symbol!=null)
                {
                    // Only "Share Experience" accepts one point
                    //if(event_type!=event_types.EVENT_TYPE_ENSEMBLE
                    if(event_type!=EVENT_TYPE_ENSEMBLE
                            ||list_of_of_added_points_symbol.size()>1)
                    {
                        open_bottom_sheet_add_event();
                        change_add_marker_on_map_ib_mode(false);
                        change_add_marker_on_map_ib_mode(false);
                    }
                    else
                        Toast.makeText(main_activity.this,R.string.user_has_not_pinned_destination,
                                Toast.LENGTH_LONG).show();

                }
                else
                {
                    Toast.makeText(main_activity.this,R.string.user_has_not_pinned_location,
                                    Toast.LENGTH_LONG).show();
                }


            }
        });

        // Configuring reject button: removes points and route, empties the list of points
        reject_marker_on_map_ib.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                open_bottom_sheet_add_event();
                remove_points_routes();
                change_add_marker_on_map_ib_mode(true);
            }
        });
    }


    /// Changing the functionality of the add_marker_on_map_ib -> add or remove
    void change_add_marker_on_map_ib_mode (boolean add_mode)
    {
        TextView hint_1 = findViewById(R.id.marker_explanation_text);
        TextView hint_2 = findViewById(R.id.tap_to_add_marker_text);

        if(add_mode)
        {
            add_marker_on_map_ib.setImageResource(R.drawable.plus);
            add_marker_on_map_ib.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources()
                    ,R.color.teal_200,getTheme()));

            hint_1.setText(R.string.hint_user_to_mark_spots_on_map);
            hint_2.setText(R.string.hint_user_to_tap_on_add_icon);
        }
        else
        {
            add_marker_on_map_ib.setImageResource(R.drawable.close);
            add_marker_on_map_ib.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources()
                    , R.color.red_500, getTheme()));

            hint_1.setText(R.string.hint_user_the_marks_added);
            hint_2.setText(R.string.hint_user_to_remove_the_marks);
        }
    }

    /// Opens bottom_sheet_add_event
    void open_bottom_sheet_add_event()
    {
        bottom_sheet_Choose_challenge.setState(BottomSheetBehavior.STATE_EXPANDED);
        accept_marker_on_map_ib.setVisibility(View.INVISIBLE);
        reject_marker_on_map_ib.setVisibility(View.INVISIBLE);

        set_cam_on_location_fab.setVisibility(View.VISIBLE);
        pin_event_sd.setVisibility(View.VISIBLE);
        add_postition_mode = false;
    }

    /// Removing route and pinned points on the map on demand
    void remove_points_routes()
    {
        if(list_of_of_added_points_symbol!=null)
        {
            symbol_manager.delete(list_of_of_added_points_symbol);
            list_of_of_added_points_symbol = null;
            list_of_added_points = null;

            navigation_mapRoute.updateRouteVisibilityTo(false);
        }

    }

    /// Initializing Pin_event_Speed_Dial
    void pin_event_sd_init()
    {
        pin_event_sd = findViewById(R.id.pin_event_sd);
        pin_event_sd.addActionItem(new SpeedDialActionItem.Builder(R.id.sd_ensemble_cycling,
                R.drawable.bike)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources()
                        ,R.color.teal_500,getTheme()))
                .setFabImageTintColor(Color.WHITE)
                .setLabelBackgroundColor(Color.WHITE)
                .setLabel("Ensemble Cycling")
                .create());
        pin_event_sd.addActionItem(new SpeedDialActionItem.Builder(R.id.sd_offer_challenge,
                R.drawable.map)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources()
                        ,R.color.light_blue_500,getTheme()))
                .setFabImageTintColor(Color.WHITE)
                .setLabelBackgroundColor(Color.WHITE)
                .setLabel("Offer a Challenge")
                .create());
        pin_event_sd.addActionItem(new SpeedDialActionItem.Builder(R.id.sd_share_experience,
                R.drawable.image)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources()
                        ,R.color.blue_500,getTheme()))
                .setFabImageTintColor(Color.WHITE)
                .setLabelBackgroundColor(Color.WHITE)
                .setLabel("Share Experience")
                .create());

        pin_event_sd.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener()
        {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem)
            {
                switch (actionItem.getId())
                {
                    case R.id.sd_ensemble_cycling:
                        bottom_sheet_Choose_challenge.setState(BottomSheetBehavior.STATE_EXPANDED);
                        //event_type = event_types.EVENT_TYPE_ENSEMBLE;
                        event_type = EVENT_TYPE_ENSEMBLE;
                        break;
                    case R.id.sd_offer_challenge:  break;
                    case R.id.sd_share_experience:  break;
                }
                return false;
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

        Log.d(TAG, "onMapLongClick: Entered");

        if (add_postition_mode)  // Prohibit adding marker when the mode was not initiated
        {
            if(list_of_of_added_points_symbol==null)
                list_of_of_added_points_symbol = new ArrayList<>();

            if (event_type != EVENT_TYPE_ENSEMBLE)
            {
                if (!is_marker_added)   // Check if any marker has been added by the user
                {
                    //selected_postion = point;
                    user_marker_pinned = symbol_manager.create(new SymbolOptions()
                            .withLatLng(new LatLng(point.getLatitude(), point.getLongitude()))
                            .withIconImage("marker_start_flag")
                            .withTextAnchor(Property.TEXT_ANCHOR_BOTTOM)
                            .withIconSize(1.0f));

                    is_marker_added = true;
                    //add_postition_mode = false;
                }
                else
                {
                    symbol_manager.delete(user_marker_pinned);
                    //selected_postion = point;
                    user_marker_pinned = symbol_manager.create(new SymbolOptions()
                            .withLatLng(new LatLng(point.getLatitude(), point.getLongitude()))
                            .withIconImage("marker_start_flag")
                            .withTextAnchor(Property.TEXT_ANCHOR_BOTTOM)
                            .withIconSize(1.0f));
                }
            }

            else
            {
                Log.d(TAG, "Ensemble Riding: Entered");
                if (list_of_added_points == null)
                {
                    list_of_added_points = new ArrayList<>();
                    user_marker_pinned = symbol_manager.create(new SymbolOptions()
                            .withLatLng(new LatLng(point.getLatitude(), point.getLongitude()))
                            .withIconImage("marker_start_flag")
                            .withTextAnchor(Property.TEXT_ANCHOR_BOTTOM)
                            .withIconSize(1.0f));
                }
                else
                {
                    user_marker_pinned = symbol_manager.create(new SymbolOptions()
                            .withLatLng(new LatLng(point.getLatitude(), point.getLongitude()))
                            .withIconImage("marker_guide_pin")
                            .withTextAnchor(Property.TEXT_ANCHOR_BOTTOM)
                            .withIconSize(1.0f));
                }

                list_of_of_added_points_symbol.add(user_marker_pinned);

                Point marked_point = Point.fromLngLat(point.getLongitude(), point.getLatitude());
                list_of_added_points.add(marked_point);
                Log.d(TAG, "list Size: "+list_of_added_points.size());
                if(list_of_added_points.size()>1)
                    getRoute(list_of_added_points);

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
                enableLocationComponent(style);
                mapbox_navigation_configuration(style);
                symbol_manager = new SymbolManager(mapView, mapboxMap, style);
                symbol_manager.setIconAllowOverlap(true);
            }
        });

    }

    void mapbox_navigation_configuration(Style style)
    {
        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(main_activity.this);
        NavigationOptions navigation_options = MapboxNavigation.defaultNavigationOptionsBuilder(main_activity.this,
                getString(R.string.mapbox_access_token))
                .locationEngine(locationEngine)
                .build();
        mapbox_navigation = new MapboxNavigation(navigation_options);
        //mapbox_navigation.registerRouteProgressObserver(main_activity.this);
        mapboxMap.addOnMapClickListener(main_activity.this);
        symbol_manager = new SymbolManager(mapView, mapboxMap, style);
        symbol_manager.setIconAllowOverlap(true);
        style.addImage("marker_guide_pin", BitmapFactory.decodeResource(getResources(),R.drawable.blue_marker));
        style.addImage("marker_start_flag", BitmapFactory.decodeResource(getResources(),R.drawable.start_64));
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle)
    {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this))
        {

            LocationComponentOptions locationComponentOptions =
                    LocationComponentOptions.builder(this)
                            .pulseEnabled(true)
                            .pulseColor(Color.CYAN)
                            .pulseAlpha(.4f)
                            .pulseInterpolator(new BounceInterpolator())
                            .build();

            LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions
                    .builder(this, loadedMapStyle)
                    .locationComponentOptions(locationComponentOptions)
                    .build();

            location_component = mapboxMap.getLocationComponent();
            location_component.activateLocationComponent(locationComponentActivationOptions);
            location_component.setLocationComponentEnabled(true);
            location_component.setCameraMode(CameraMode.TRACKING);
            location_component.setRenderMode(RenderMode.COMPASS);
        }
        else
        {
            ///
            PermissionsManager permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }


    private void getRoute(List<Point> list)
    {

        Log.d(TAG, "Get Route");
        mapbox_navigation.requestRoutes(RouteOptions.builder()
                .baseUrl("https://api.mapbox.com")
                .user("mapbox")
                .requestUuid("ck7dtdd2z00yx75plynvtan26")
                .accessToken(getString(R.string.mapbox_access_token))
                .coordinates(list)
                .geometries(RouteUrl.GEOMETRY_POLYLINE6)
                .voiceUnits(RouteUrl.METRIC)
                .profile(RouteUrl.PROFILE_DRIVING)
                .build(), new RoutesRequestCallback()
        {
            @Override
            public void onRoutesReady(@NotNull List<? extends DirectionsRoute> list)
            {
                Log.d("TAG","Received");
                DirectionsRoute currentRoute;
                currentRoute = list.get(0);

                if (navigation_mapRoute == null)
                {
                    navigation_mapRoute = new NavigationMapRoute.Builder(mapView, mapboxMap, main_activity.this)
                            .withVanishRouteLineEnabled(true)
                            .withMapboxNavigation(mapbox_navigation)
                            .build();
                }
                navigation_mapRoute.addRoute(currentRoute);


            }

            @Override
            public void onRoutesRequestFailure(@NotNull Throwable throwable, @NotNull RouteOptions routeOptions)
            {
                Log.d("TAG","Failure");
            }

            @Override
            public void onRoutesRequestCanceled(@NotNull RouteOptions routeOptions)
            {
                Log.d("TAG","Canceled");
            }
        });

    }

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

     */

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain)
    {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted)
    {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    void send_user_created_event()
    {
        Date current_date = Calendar.getInstance().getTime();
        SimpleDateFormat standard_date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);    // Date Styles may should change to the getDefault()
        SimpleDateFormat time_24_format = new SimpleDateFormat("H:mm", Locale.US);

        //symbol_manager.delete(user_marker_pinned);
        Map<String, String> params = new HashMap<>();
        params.put("user_name",USER_NAME);
        params.put("user_family",USER_FAMILY);
        params.put("user_id",USER_ID);
        params.put("user_password",USER_PASSWORD);
        params.put("num_points",String.valueOf(list_of_added_points.size()));
        params.put("event_type",String.valueOf(event_type));
        params.put("date_created",standard_date_format.format(current_date));
        params.put("date_of_event",standard_date_format.format(current_date));
        params.put("event_time",time_24_format.format(calendar_time_picker.getTime()));
        params.put("user_type",String.valueOf(1));
        params.put("user_picture",USER_PIC);

        for(int i=0;i<list_of_added_points.size();i++)
        {
            String longitude_x = "longitude_"+ i;
            String latitude_x = "latitude_"+ i;
            params.put(longitude_x,String.valueOf(list_of_added_points.get(i).longitude()));
            params.put(latitude_x,String.valueOf(list_of_added_points.get(i).latitude()));
        }

        SimpleHttp.post(getString(R.string.register_event), params, new SimpleHttpResponseHandler()
        {
            @Override
            public void onResponse(int responseCode, String responseBody)
            {
                Log.d(TAG,"Post Test Code: "+responseCode);
                Log.d(TAG,"Post Test Body: "+responseBody);
                get_db_updated();
            }
        });

    }

    private void add_received_events(JSONArray received_events)
    {

        String file_directory = main_activity.this.getFilesDir().toString();
        file_directory_static = file_directory;
        geo_JSON_maker feature_maker = new geo_JSON_maker();
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

        file_maker geo_json_holder = new file_maker(file_directory, FILE_NAME);
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

                //mapboxMap.addOnMapClickListener(main_activity.this);
                //mapboxMap.addOnMapLongClickListener(main_activity.this);
                symbol_manager = new SymbolManager(mapView, mapboxMap, style);
                symbol_manager.setIconAllowOverlap(true);
                style.addImage("marker_guide_pin", BitmapFactory.decodeResource(getResources(),R.drawable.blue_marker));
                style.addImage("marker_start_flag", BitmapFactory.decodeResource(getResources(),R.drawable.start_64));

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
                        Log.v(TAG,"featureList.get(i): "+featureList.get(i));
                        Log.d(TAG,"number_of_selected_cards: "+number_of_selected_cards);

                        if (featureSelectStatus(i))
                        {

                            if(featureList.get(i).getStringProperty(PROPERTY_EVENT_ID).equals(last_event_selected))
                            {
                                number_of_selected_cards--;
                                if(number_of_selected_cards==0)
                                {
                                    set_camera_position(0, null);
                                    bottom_sheet_click_join.setState(BottomSheetBehavior.STATE_HIDDEN);
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
                                    Log.d(TAG,"Closing Bottom: "+e.getMessage());
                                }
                            }
                        }
                        else
                        {
                            last_event_selected = featureList.get(i).getStringProperty(PROPERTY_EVENT_ID);
                            number_of_selected_cards++;
                            if(number_of_selected_cards>0)
                            {

                                try
                                {
                                    JSONObject geometry = new JSONObject(featureList.get(i).geometry().toJson());
                                    update_bottom_sheet_click(featureList.get(i));
                                    JSONArray point = new JSONArray(geometry.get("coordinates").toString());

                                    set_camera_position(50,new LatLng(Double.parseDouble(point.get(1).toString())
                                                                        ,Double.parseDouble(point.get(0).toString())));
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

    private void set_camera_position(int tilt,LatLng point)
    {
        CameraPosition position = null;
        if (point != null)
            position = new CameraPosition.Builder()
                    .target(point) // Sets the new camera position
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

        List<Point> list_received_points  = null;
        list_received_points = new ArrayList<>();

        int num_points = Integer.parseInt(info.get("num_points").toString());

        for(int i=0; i<num_points;i++)
        {
            Point point = Point.fromLngLat(Double.parseDouble(info.get("longitude_"+i).toString())
                                            ,Double.parseDouble(info.get("latitude_"+i).toString()));
            list_received_points.add(point);
        }
        if(num_points>1)
            getRoute(list_received_points);

        Button join_btn = findViewById(R.id.join_event);
        Button cancel_join_event_btn = findViewById(R.id.cancel_join_event);
        TextView hint_user_tv = findViewById(R.id.selected_event_user_join_hint);

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

        boolean user_owns_event = info.get("user_id").toString().equals(USER_ID);
        if(!user_owns_event)
        {
            join_btn.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources()
                    ,R.color.save_button,getTheme()));
            if (info.get("is_user_joined").toString().equals("true"))
            {
                selected_event_status.setText("You have joined this event");
                hint_user_tv.setText("*Click opt out if you don't want to participate");
                join_btn.setText("Opt out");
                join_status = true;

            }
            else
            {
                selected_event_status.setText("You have not joined this event");
                hint_user_tv.setText("*Click join to join this event");
                join_btn.setText("Join");
                join_status = false;
            }
        }
        else
        {
            selected_event_status.setText("You own this event");
            hint_user_tv.setText("*Click remove to revoke the event");
            join_btn.setText("Remove");
            join_btn.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources()
                    ,R.color.red_500,getTheme()));
        }

        join_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Date current_date = Calendar.getInstance().getTime();
                SimpleDateFormat standard_date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

                Log.d(TAG, "Button Pressed");
                Map<String, String> params = new HashMap<>();

                try {params.put("event_unique_id",info.get("event_id").toString());}
                catch (JSONException e) { e.printStackTrace(); }
                params.put("joined_user_id",USER_ID);

                if(!user_owns_event)
                {
                    if (join_status)
                    {
                        params.put("request", "out");
                        send_join_command(params, feature);
                    }
                    else
                    {
                        params.put("request", "join");
                        params.put("user_date_of_join", standard_date_format.format(current_date));
                        send_join_command(params,feature);
                    }
                }
                else
                {
                    params.put("request", "remove");
                    AlertDialog.Builder alert_dialog_bl = new AlertDialog.Builder(main_activity.this);
                    alert_dialog_bl.setMessage("Do you really want to remove this event?")
                            .setCancelable(true)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener()
                            {public void onClick(DialogInterface dialog, int id) {send_join_command(params, feature);}})
                            .setNegativeButton("CANCEL",new DialogInterface.OnClickListener()
                            {public void onClick(DialogInterface dialog, int id) {dialog.cancel();}});


                    AlertDialog alertDialog = alert_dialog_bl.create();

                    alertDialog.show();
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ResourcesCompat.getColor(getResources()
                            ,R.color.gray_800,getTheme()));
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ResourcesCompat.getColor(getResources()
                            ,R.color.gray_800,getTheme()));

                }
                //setFeatureSelectState(feature, false);

                //symbol_manager.delete(li);
                navigation_mapRoute.updateRouteVisibilityTo(false);
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
                    set_camera_position(0,null);


                setFeatureSelectState(feature, false);

                //symbol_manager.delete(li);
                navigation_mapRoute.updateRouteVisibilityTo(false);
            }
        });


        Log.d(TAG,"Feature selected data: "+info.toString());

    }

    void send_join_command(Map<String, String> params, Feature feature)
    {
        Log.d(TAG,"Send join: "+params.get("request"));
        SimpleHttp.post(getString(R.string.event_join_status), params, new SimpleHttpResponseHandler()
        {
            @Override
            public void onResponse(int responseCode, String responseBody)
            {
                Log.d(TAG, "Post Body: "+responseBody);
                //number_of_selected_cards--;
                bottom_sheet_click_join.setState(BottomSheetBehavior.STATE_HIDDEN);
                if(number_of_selected_cards==0)
                    set_camera_position(0,null);


                setFeatureSelectState(feature, false);
            }
        });
        get_db_updated();
        remove_points_routes();
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
                    try
                    {
                        String num_of_joined = feature.getStringProperty(PROPERTY_NUM_OF_JOINED);
                        TextView number_of_joined_users_tv = constraint_layout.findViewById(R.id.num_of_joined);
                        number_of_joined_users_tv.setText(num_of_joined);
                    }
                    catch (Exception e)
                    {

                    }


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
        //mapView.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //mapView.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        //mapView.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        //mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        //mapView.onLowMemory();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        /*
        if (mapboxMap != null)
        {
            mapboxMap.removeOnMapClickListener(this);
        }
        mapView.onDestroy();
        mapbox_navigation.onDestroy();

         */
    }
}