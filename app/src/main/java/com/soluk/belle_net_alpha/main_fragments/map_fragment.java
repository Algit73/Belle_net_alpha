package com.soluk.belle_net_alpha.main_fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.navigation.base.internal.route.RouteUrl;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.core.directions.session.RoutesRequestCallback;
import com.mapbox.navigation.ui.route.NavigationMapRoute;
import com.soluk.belle_net_alpha.main_fragments.map_utils.Map_Event_Creator;
import com.soluk.belle_net_alpha.selected_event.Selected_Event_Activity;
import com.soluk.belle_net_alpha.selected_user_profile_page.User_Profile_Page_Activity;
import com.soluk.belle_net_alpha.utils.Date_Time_Provider;
import com.soluk.belle_net_alpha.HTTP_Provider;
import com.soluk.belle_net_alpha.R;
import com.soluk.belle_net_alpha.event_data_maker.file_maker;
import com.soluk.belle_net_alpha.Main_Activity;
import com.soluk.belle_net_alpha.model.Events_DB_VM;
import com.soluk.belle_net_alpha.ui.login.User_Credentials;
import com.soluk.belle_net_alpha.utils.Image_Provider;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.os.Looper.getMainLooper;
import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.soluk.belle_net_alpha.main_fragments.map_utils.Map_Event_Creator.MARKER_LAYER_ID_CHALLENGE;
import static com.soluk.belle_net_alpha.main_fragments.map_utils.Map_Event_Creator.MARKER_LAYER_ID_ENSEMBLE;
import static com.soluk.belle_net_alpha.main_fragments.map_utils.Map_Event_Creator.PROPERTY_SELECTED;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_DATE;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_DATE_CREATED;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.EVENT_TYPE;
import static com.soluk.belle_net_alpha.model.Events_DB_VM.USER_PIC;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link map_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class map_fragment extends Fragment implements
        OnMapReadyCallback, MapboxMap.OnMapClickListener,MapboxMap.OnMapLongClickListener, PermissionsListener
{

    private static final String TAG = map_fragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public Map_Event_Creator map_event_creator;


    private static  String USER_NAME = "";
    private static  String USER_FAMILY = "";
    private static  String USER_PICTURE = "";
    private static  String USER_ID = "";
    private static  String USER_PASSWORD = "";
    private static  String USER_EMAIL = "";

    private static String file_directory_static = "";
    private static final String FILE_NAME = "geo_json_bellenet";

    private FeatureCollection featureCollection;
    private int number_of_selected_cards;
    private String  last_event_selected;
    private boolean join_status = false;
    private GeoJsonSource source;
    private GeoJsonSource source_ensemble;
    private GeoJsonSource source_challenge;
    private PermissionsManager permissionsManager;

    Button join_btn;
    Button cancel_join_event_btn;
    TextView hint_user_tv;
    TextView selected_user_name;
    CircleImageView selected_user_profile_image;
    TextView selected_event_date;
    TextView selected_event_type;
    TextView selected_event_status;


    /// Mapbox Variables
    private   MapView mapView;
    //TODO: Change mapbox from static to normal
    public static MapboxMap mapboxMap;
    private SymbolManager symbol_manager;
    private NavigationOptions navigation_options;
    private MapboxNavigation mapbox_navigation;
    private LocationComponent location_component;
    private NavigationMapRoute navigation_mapRoute;

    private BottomSheetBehavior bottom_sheet_click_join;
    private BottomSheetBehavior bottom_sheet_Choose_challenge;
    private FloatingActionButton set_cam_on_location_fab;
    private SpeedDialView pin_event_sd;

    private boolean add_postition_mode = false;
    private boolean is_marker_added = false;
    private boolean enable_access_location = false;
    private Symbol user_marker_pinned;

    private TextView event_date_tv;
    private TextView event_time_tv;
    private TextView hint_1;
    private TextView hint_2;

    private final Calendar calendar_time_picker = Calendar.getInstance();
    private ImageButton add_marker_on_map_ib;
    private ImageButton accept_marker_on_map_ib;
    private ImageButton reject_marker_on_map_ib;

    ConstraintLayout join_event_layout_cl;

    private ImageView loading_screen;
    private ProgressBar loading_wheel;


    private final int EVENT_TYPE_ENSEMBLE = 0;
    private final int EVENT_TYPE_CHALLENGE = 1;
    private final int EVENT_TYPE_EXPERIENCE = 2;
    private int event_type;

    private List<Point> list_of_added_points;
    private List<Symbol> list_of_of_added_points_symbol;

    public map_fragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment maps_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static map_fragment newInstance(String param1, String param2)
    {
        map_fragment fragment = new map_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted)
                {
                    // Permission is granted. Continue the action or workflow in your
                    // app.

                    //enable_location_component(style_global);
                    //mapbox_navigation_configuration(style_global);
                    Log.d(TAG,"requestPermissionLauncher: Granted");
                }
                else
                {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    Log.d(TAG,"requestPermissionLauncher: Not Granted");
                }
            });

    private void request_permission()
    {
        if (ContextCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED)
        {
            // You can use the API that requires the permission.
            Log.d(TAG,"request_permission: Granted");

        }
        //else if (shouldShowRequestPermissionRationale(...))
        //{
        // In an educational UI, explain to the user why your app requires this
        // permission for a specific feature to behave as expected. In this UI,
        // include a "cancel" or "no thanks" button that allows the user to
        // continue using your app without granting the permission.

        //}
        else
        {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            Log.d(TAG,"request_permission: Not Granted");
            requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d(TAG,"user_creds: "+User_Credentials.get_items());

        try
        {
            USER_NAME = User_Credentials.get_item(Events_DB_VM.USER_NAME);
            USER_FAMILY = User_Credentials.get_item(Events_DB_VM.USER_FAMILY);
            USER_PICTURE = User_Credentials.get_item(USER_PIC);
            USER_ID = User_Credentials.get_item(Events_DB_VM.USER_ID);
            USER_PASSWORD = User_Credentials.get_item(Events_DB_VM.USER_PASSWORD);
            USER_EMAIL = User_Credentials.get_item(Events_DB_VM.USER_EMAIL);
        }catch (JSONException e){e.printStackTrace();}

        list_of_of_added_points_symbol = new ArrayList<>();
        list_of_added_points = new ArrayList<>();


        file_directory_static = getActivity().getFilesDir().toString();
        Log.d(TAG,"map Fragment dir: "+file_directory_static);
        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));
        request_permission();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        loading_screen = v.findViewById(R.id.loading_screen);
        loading_wheel = v.findViewById(R.id.loading_wheel);

        mapView = v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        View bottom_sheet_click_join_view = v.findViewById(R.id.bottom_sheet_click_to_join);
        bottom_sheet_click_join = BottomSheetBehavior.from(bottom_sheet_click_join_view);
        bottom_sheet_click_join.setState(BottomSheetBehavior.STATE_HIDDEN);




        /// Configuring BottomSheet_Choose_Challenge in the BelleNet
        View bottom_sheet_challenges_veiw = v.findViewById(R.id.bottom_sheet_challenges);
        bottom_sheet_Choose_challenge = BottomSheetBehavior.from(bottom_sheet_challenges_veiw);
        bottom_sheet_Choose_challenge.setState(BottomSheetBehavior.STATE_HIDDEN);


        /// Set camera on the user location
        set_cam_on_location_fab = v.findViewById(R.id.fab_pin_on_maps);
        set_cam_on_location_fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


                if (PermissionsManager.areLocationPermissionsGranted(getActivity()))
                {
                    if(enable_access_location)
                    {
                        LatLng current_position = new LatLng(location_component.getLastKnownLocation().getLatitude()
                                , location_component.getLastKnownLocation().getLongitude());
                        set_camera_position(0, current_position);
                    }
                    else
                    {
                        refresh_map();
                        ((Main_Activity) getActivity()).refresh_db();

                        new Handler(Looper.getMainLooper()).postDelayed(()->
                        {
                            LatLng current_position = new LatLng(location_component.getLastKnownLocation().getLatitude()
                                    , location_component.getLastKnownLocation().getLongitude());
                            set_camera_position(0, current_position);
                            enable_access_location = true;
                        },500);
                    }
                }
            }
        });

        hint_1 = v.findViewById(R.id.marker_explanation_text);
        hint_2 = v.findViewById(R.id.tap_to_add_marker_text);
        button_sheet_add_event_init(v);
        bottom_sheet_join_event_init(v);
        pin_event_sd_init(v);

        return v;
    }

    void button_sheet_add_event_init(View v)
    {

        CircleImageView user_profile_image = v.findViewById(R.id.user_profile_image);
        Bitmap selected_profile_image_bmp = Image_Provider.get_profile_bmp(USER_PICTURE);
        if(selected_profile_image_bmp!=null)
            user_profile_image.setImageBitmap(selected_profile_image_bmp);

        // Configuring Buttons in the BottomSheet of BelleNet
        Button save_challenge_btn = v.findViewById(R.id.save_challenge);
        Button cancel_challenge_btn = v.findViewById(R.id.cancel_challenge);

        cancel_challenge_btn.setOnClickListener(v1 ->
        {
            is_marker_added = false;
            add_postition_mode = false;
            change_add_marker_on_map_ib_mode(true);
            remove_points_routes();
            bottom_sheet_Choose_challenge.setState(BottomSheetBehavior.STATE_HIDDEN);
            ((Main_Activity)getActivity()).bottom_navigation_visibility(true);
        });

        save_challenge_btn.setOnClickListener(v1 ->
        {
            if(list_of_added_points.size()==0)
            {
                Toast.makeText(getActivity(),
                        "Please add your spots on the map",Toast.LENGTH_SHORT).show();
                return;
            }

            String date_value = event_date_tv.getText().toString();

            if(!date_value.matches(".*\\d.*"))
            {
                Toast.makeText(getActivity(),
                        "Please input the event date",Toast.LENGTH_SHORT).show();
                return;
            }

            String time_value = event_time_tv.getText().toString();

            if(!time_value.matches(".*\\d.*"))
            {
                Toast.makeText(getActivity(),
                        "Please input the event time",Toast.LENGTH_SHORT).show();
                return;
            }

            send_user_created_event();
            ((Main_Activity)getActivity()).bottom_navigation_visibility(true);
            bottom_sheet_Choose_challenge.setState(BottomSheetBehavior.STATE_HIDDEN);
            remove_points_routes();
            change_add_marker_on_map_ib_mode(true);
        });

        /// Configuring DatePicker
        event_date_tv = v.findViewById(R.id.input_date_tv);
        event_date_tv.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                Date_Time_Provider date_provider = new Date_Time_Provider(getActivity())
                        .set_date_tv(event_date_tv)
                        .set_date_format(Date_Time_Provider.US);

                date_provider.show_date_dialog();
            }
        });

        /// Configuring TimePicker
        event_time_tv = v.findViewById(R.id.input_time_tv);

        event_time_tv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Date_Time_Provider time_provider = new Date_Time_Provider(getActivity())
                        .set_time_tv(event_time_tv)
                        .set_time_format(Date_Time_Provider.H24);

                time_provider.show_time_dialog();
            }
        });

        /// Configuring pinning on map
        add_marker_on_map_ib = v.findViewById(R.id.add_marker_ib);
        accept_marker_on_map_ib = v.findViewById(R.id.accept_pinned_markers_ib);
        reject_marker_on_map_ib = v.findViewById(R.id.reject_pinned_markers_ib);

        // Configuring Add Marker: 1- Adding points and route 2- Removing points and route
        add_marker_on_map_ib.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Check if any point has been added previously: null if not
                if(list_of_added_points.size()>0)
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
                /// Check if any point has been added, if not toasts a message
                if(list_of_of_added_points_symbol!=null)
                {
                    // Only "Share Experience" accepts one point
                    if(event_type!=EVENT_TYPE_ENSEMBLE
                            ||list_of_of_added_points_symbol.size()>1)
                    {
                        open_bottom_sheet_add_event();
                        ////change_add_marker_on_map_ib_mode(false);
                        change_add_marker_on_map_ib_mode(false);
                    }
                    else
                        Toast.makeText(getActivity(),R.string.user_has_not_pinned_destination,
                                Toast.LENGTH_LONG).show();

                }
                else
                {
                    Toast.makeText(getActivity(),R.string.user_has_not_pinned_location,
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

    void send_user_created_event()
    {
        wait_on_loading_data(false);

        Date current_date = Calendar.getInstance().getTime();
        SimpleDateFormat standard_date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);    // Date Styles may should change to the getDefault()
        SimpleDateFormat time_24_format = new SimpleDateFormat("H:mm", Locale.US);


        JSONObject json = new JSONObject();
        try
        {
            json.put(Events_DB_VM.USER_NAME,USER_NAME);
            json.put(Events_DB_VM.USER_FAMILY,USER_FAMILY);
            json.put(Events_DB_VM.USER_ID,USER_ID);
            json.put(Events_DB_VM.USER_PASSWORD,USER_PASSWORD);
            json.put(Events_DB_VM.NUM_POINTS,String.valueOf(list_of_added_points.size()));
            json.put(EVENT_TYPE,String.valueOf(event_type));
            json.put(EVENT_DATE_CREATED,standard_date_format.format(current_date));
            json.put(EVENT_DATE,Date_Time_Provider.date_to_YMD(event_date_tv.getText().toString()));
            json.put(Events_DB_VM.EVENT_TIME,time_24_format.format(calendar_time_picker.getTime()));
            json.put(Events_DB_VM.USER_TYPE,String.valueOf(1));
            json.put(USER_PIC,USER_PICTURE);

            for(int i=0;i<list_of_added_points.size();i++)
            {
                String longitude_x = "longitude_"+ i;
                String latitude_x = "latitude_"+ i;
                json.put(longitude_x,String.valueOf(list_of_added_points.get(i).longitude()));
                json.put(latitude_x,String.valueOf(list_of_added_points.get(i).latitude()));
            }
        }
        catch (Exception e) {}

        Callback callback = new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {

                Log.d(TAG,"Post Test Code: "+response.code());
                Log.d(TAG,"Post Test Body: "+response.body().string());
                //db_model.refresh_db();
                ((Main_Activity)getActivity()).refresh_db();
            }
        };

        HTTP_Provider.post_json("belle_net_users_info/user_register_new_event.php",json,callback);

    }

    /// Sends Join, Opt out and Remove commands
    void send_join_command(Feature feature, JSONObject json)
    {
        //Log.d(TAG,"Send join: "+params);
        Log.d(TAG,"Send join JSON: "+json);
        Callback callback = new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {

                Log.d(TAG, "Post Body: "+response.body().string());
                send_join_command_response(feature);

            }
        };
        wait_on_loading_data(false);

        HTTP_Provider.post_json(getActivity().getString(R.string.bellenet_join_event_url),json,callback);
        ((Main_Activity)getActivity()).refresh_db();
        remove_points_routes();
    }

    void send_join_command_response(Feature feature)
    {
        number_of_selected_cards--;

        Handler mainHandler = new Handler(getMainLooper());
        Runnable myRunnable = () ->
        {
            map_event_creator.set_feature_select_state(feature, false);
            if(number_of_selected_cards==0)
                set_camera_position(0,null);
            ((Main_Activity)getActivity()).bottom_navigation_visibility(true);
            bottom_sheet_click_join.setState(BottomSheetBehavior.STATE_HIDDEN);
        };
        mainHandler.post(myRunnable);
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
            list_of_of_added_points_symbol.clear();
            list_of_added_points.clear();
            if(navigation_mapRoute!=null)
                navigation_mapRoute.updateRouteVisibilityTo(false);
        }

    }

    /// Initializing Pin_event_Speed_Dial
    void pin_event_sd_init(View v)
    {
        pin_event_sd = v.findViewById(R.id.pin_event_sd);
        pin_event_sd.addActionItem(new SpeedDialActionItem.Builder(R.id.sd_ensemble_cycling,
                R.drawable.bike)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources()
                        ,R.color.teal_500,getActivity().getTheme()))
                .setFabImageTintColor(Color.WHITE)
                .setLabelBackgroundColor(Color.WHITE)
                .setLabel("Ensemble Cycling")
                .create());
        pin_event_sd.addActionItem(new SpeedDialActionItem.Builder(R.id.sd_offer_challenge,
                R.drawable.map)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources()
                        ,R.color.light_blue_500,getActivity().getTheme()))
                .setFabImageTintColor(Color.WHITE)
                .setLabelBackgroundColor(Color.WHITE)
                .setLabel("Offer a Challenge")
                .create());
        pin_event_sd.addActionItem(new SpeedDialActionItem.Builder(R.id.sd_share_experience,
                R.drawable.image)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources()
                        ,R.color.blue_500,getActivity().getTheme()))
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
                ((Main_Activity)getActivity()).bottom_navigation_visibility(false);
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

    void change_add_marker_on_map_ib_mode (boolean add_mode)
    {

        if(add_mode)
        {
            add_marker_on_map_ib.setImageResource(R.drawable.plus);
            add_marker_on_map_ib.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources()
                    ,R.color.teal_200,getActivity().getTheme()));

            hint_1.setText(R.string.hint_user_to_mark_spots_on_map);
            hint_2.setText(R.string.hint_user_to_tap_on_add_icon);
        }
        else
        {
            add_marker_on_map_ib.setImageResource(R.drawable.close);
            add_marker_on_map_ib.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources()
                    , R.color.red_500, getActivity().getTheme()));

            hint_1.setText(R.string.hint_user_the_marks_added);
            hint_2.setText(R.string.hint_user_to_remove_the_marks);
        }
    }

    private void get_route(List<Point> list)
    {

        Log.d(TAG, "Get Route");
        wait_on_loading_data(false);
        //navigation_mapRoute.updateRouteVisibilityTo(false);
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
                wait_on_loading_data(true);

                if (navigation_mapRoute == null)
                {
                    navigation_mapRoute = new NavigationMapRoute.Builder(mapView, mapboxMap, getActivity())
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
                wait_on_loading_data(true);
            }

            @Override
            public void onRoutesRequestCanceled(@NotNull RouteOptions routeOptions)
            {
                Log.d("TAG","Canceled");
                wait_on_loading_data(true);
            }
        });

    }

    private void bottom_sheet_join_event_init(View v)
    {
        join_btn = v.findViewById(R.id.join_event);
        cancel_join_event_btn = v.findViewById(R.id.cancel_join_event);
        hint_user_tv = v.findViewById(R.id.selected_event_user_join_hint);
        selected_user_name = v.findViewById(R.id.selected_user_name);
        selected_user_profile_image = v.findViewById(R.id.selected_user_profile_image);
        selected_event_date = v.findViewById(R.id.selected_event_date);
        selected_event_type = v.findViewById(R.id.selected_event_type);
        selected_event_status = v.findViewById(R.id.selected_event_user_join_status);
        join_event_layout_cl = v.findViewById(R.id.bottom_sheet_click_to_join);
    }

    /// Each time a pin selected, the appropriate bottom_sheet appears
    private void open_selected_pinned_event(Feature feature) throws JSONException
    {
        JSONObject info = new JSONObject(feature.properties().toString());
        ((Main_Activity)getActivity()).bottom_navigation_visibility(false);
        bottom_sheet_click_join.setState(BottomSheetBehavior.STATE_EXPANDED);

        List<Point> list_received_points;
        list_received_points = new ArrayList<>();

        int num_points = Integer.parseInt(info.get("num_points").toString());

        /// Removes former added pins
        if(list_of_of_added_points_symbol.size()>0)
            symbol_manager.delete(list_of_of_added_points_symbol);


        /// Adding pins to the path
        for(int i=0; i<num_points;i++)
        {
            Point point = Point.fromLngLat(Double.parseDouble(info.get("longitude_"+i).toString())
                    ,Double.parseDouble(info.get("latitude_"+i).toString()));
            list_received_points.add(point);


            if(i>0)
            {
                user_marker_pinned = symbol_manager.create(new SymbolOptions()
                        .withLatLng(new LatLng(point.latitude(), point.longitude()))
                        .withIconImage("marker_guide_pin")
                        .withTextAnchor(Property.TEXT_ANCHOR_BOTTOM)
                        .withIconSize(1.0f));
                Log.d(TAG,"Check user_marker_pinned: " + user_marker_pinned.toString());
                list_of_of_added_points_symbol.add(user_marker_pinned);
            }



        }

        /// Drawing the path
        if(num_points>1)
            get_route(list_received_points);



        /// Setting name of the user
        String name_family = (info.get(Events_DB_VM.USER_NAME)+ " " + info.get(Events_DB_VM.USER_FAMILY));
        selected_user_name.setText(name_family);

        /// Setting the profile image of the selected_user
        Bitmap selected_profile_image_bmp = Image_Provider.get_profile_bmp(info.get(USER_PIC).toString());
        if(selected_profile_image_bmp!=null)
            selected_user_profile_image.setImageBitmap(selected_profile_image_bmp);

        /// Setting event date
        selected_event_date.setText(Date_Time_Provider.
                date_to_MDY(info.get(EVENT_DATE).toString()));


        /// Setting event type
        String event_type = info.get(EVENT_TYPE).toString();
        if(event_type.equals("1"))
            selected_event_type.setText("Ensemble Riding");
        else if(event_type.equals("2"))
            selected_event_type.setText("Offering Challenge");
        if(event_type.equals("3"))
            selected_event_type.setText("Sharing Experience");


        join_event_layout_cl.setOnClickListener(v ->
        {
            Intent intent = new Intent(getContext(), Selected_Event_Activity.class);
            getContext().startActivity(intent);
        });

        /// Check if user owns the event or opens someone else's event
        boolean user_owns_event = info.get(Events_DB_VM.USER_ID).toString().equals(USER_ID);

        selected_user_profile_image.setOnClickListener(v ->
        {
                if(!user_owns_event)
                {
                    Log.d(TAG,"check user_ID: Entered");
                    Intent intent = new Intent(getContext(), User_Profile_Page_Activity.class);
                    intent.putExtra("feature", feature.toJson());
                    getActivity().startActivity(intent);
                }
        });


        if(!user_owns_event)
        {
            join_btn.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources()
                    ,R.color.save_button,getActivity().getTheme()));
            if (info.get(Events_DB_VM.IS_USER_JOINED).toString().equals("true"))
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
                    ,R.color.red_500,getActivity().getTheme()));
        }

        join_btn.setOnClickListener(v ->
        {
            JSONObject json = new JSONObject();
            symbol_manager.delete(list_of_of_added_points_symbol);
            list_of_of_added_points_symbol.clear();

            try
            {   json.put(Events_DB_VM.USER_EMAIL, USER_EMAIL);
                json.put(Events_DB_VM.USER_PASSWORD,USER_PASSWORD);
                json.put(Events_DB_VM.USER_ID,USER_ID);
                json.put(Events_DB_VM.EVENT_ID,info.get(Events_DB_VM.EVENT_ID).toString());}
            catch (Exception e) {Log.d(TAG, "event_unique_id catch " + e);}



            if(!user_owns_event)
            {
                if (join_status)
                {
                    try {json.put("request","out");}
                    catch (Exception ignored) {}
                }
                else
                {

                    try {json.put("request","join");}
                    catch (Exception ignored) {}
                }
                send_join_command(feature,json);
            }
            else
            {
                try {json.put("request","remove");}
                catch (Exception ignored) {}
                AlertDialog.Builder alert_dialog_bl = new AlertDialog.Builder(getActivity());
                alert_dialog_bl.setMessage("Do you really want to remove this event?")
                        .setCancelable(true)
                        .setPositiveButton("YES", (dialog, id) -> send_join_command(feature,json))
                        .setNegativeButton("CANCEL", (dialog, id) -> dialog.cancel());


                AlertDialog alertDialog = alert_dialog_bl.create();

                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ResourcesCompat.getColor(getResources()
                        ,R.color.gray_800,getActivity().getTheme()));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ResourcesCompat.getColor(getResources()
                        ,R.color.gray_800,getActivity().getTheme()));

            }

        });

        /// Closing the bottom_sheet_click
        cancel_join_event_btn.setOnClickListener(v ->
        {
            number_of_selected_cards--;
            ((Main_Activity)getActivity()).bottom_navigation_visibility(true);
            bottom_sheet_click_join.setState(BottomSheetBehavior.STATE_HIDDEN);
            if(number_of_selected_cards==0)
                set_camera_position(0,null);

            map_event_creator.set_feature_select_state(feature, false);
            navigation_mapRoute.updateRouteVisibilityTo(false);
            symbol_manager.delete(list_of_of_added_points_symbol);
            list_of_of_added_points_symbol.clear();
        });


        Log.d(TAG,"Feature selected data: "+info.toString());

    }


    void wait_on_loading_data(boolean is_loaded)
    {
        if(is_loaded)
        {
            loading_wheel.setVisibility(View.GONE);
            loading_screen.setVisibility(View.GONE);
        }
        else
        {
            loading_wheel.setVisibility(View.VISIBLE);
            loading_screen.setVisibility(View.VISIBLE);
        }
    }

    public void update_map()
    {
        Log.d(TAG,"update map received");
        new Load_GeoJson_Data_Task(map_fragment.this,0).execute();
        refresh_map();
        wait_on_loading_data(true);
    }


    /// Configuring Map Interactions
    @Override
    public boolean onMapClick(@NonNull LatLng point)
    {
        Log.v(TAG,"onMapClick");
        return on_event_icon_clicked(mapboxMap.getProjection().toScreenLocation(point));
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
                    user_marker_pinned = symbol_manager.create(new SymbolOptions()
                            .withLatLng(new LatLng(point.getLatitude(), point.getLongitude()))
                            .withIconImage("marker_start_flag")
                            .withTextAnchor(Property.TEXT_ANCHOR_BOTTOM)
                            .withIconSize(1.0f));

                    is_marker_added = true;
                }
                else
                {
                    symbol_manager.delete(user_marker_pinned);
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
                if (list_of_added_points.size() == 0)
                {
                    //list_of_added_points = new ArrayList<>();
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
                    get_route(list_of_added_points);

            }
        }
        return false;
    }


    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap)
    {
        map_fragment.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, style ->
        {
            mapboxMap.addOnMapClickListener(map_fragment.this);
            mapboxMap.addOnMapLongClickListener(map_fragment.this);
            enable_location_component(style);
            mapbox_navigation_configuration(style);
            symbol_manager = new SymbolManager(mapView, mapboxMap, style);
            symbol_manager.setIconAllowOverlap(true);
            map_event_creator = new Map_Event_Creator(this.getContext());
        });
    }

    void refresh_map()
    {
        map_fragment.mapboxMap.setStyle(Style.MAPBOX_STREETS, this::mapbox_navigation_configuration);

        }

    void mapbox_navigation_configuration(Style style)
    {
        //Log.d(TAG,"set_mapbox_style 2");
        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(getActivity());
        navigation_options = MapboxNavigation.defaultNavigationOptionsBuilder(getActivity(),
                getString(R.string.mapbox_access_token))
                .locationEngine(locationEngine)
                .build();
        mapbox_navigation = new MapboxNavigation(navigation_options);
        symbol_manager = new SymbolManager(mapView, mapboxMap, style);
        symbol_manager.setIconAllowOverlap(true);
        style.addImage("marker_guide_pin", BitmapFactory.decodeResource(getResources(),R.drawable.blue_marker));
        style.addImage("marker_start_flag", BitmapFactory.decodeResource(getResources(),R.drawable.start_64));
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enable_location_component(@NonNull Style loadedMapStyle)
    {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getActivity()))
        {

            enable_access_location = true;
            LocationComponentOptions locationComponentOptions =
                    LocationComponentOptions.builder(getActivity())
                            .pulseEnabled(true)
                            .pulseColor(Color.CYAN)
                            .pulseAlpha(.4f)
                            .pulseInterpolator(new BounceInterpolator())
                            .build();

            LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions
                    .builder(getActivity(), loadedMapStyle)
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
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    private void set_camera_position(int tilt,LatLng point)
    {
        CameraPosition position;
        if (point != null)
            position = new CameraPosition.Builder()
                    .target(point) // Sets the new camera position
                    .zoom(12) // Sets the zoom
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

    private boolean on_event_icon_clicked(PointF screenPoint)
    {
        Log.v(TAG,"on_event_icon_clicked");
        List<Feature> features = new ArrayList<>();
        //List<Feature> features = mapboxMap.queryRenderedFeatures(screenPoint, MARKER_LAYER_ID_ENSEMBLE);
        features.addAll(mapboxMap.queryRenderedFeatures(screenPoint, MARKER_LAYER_ID_ENSEMBLE));
        features.addAll(mapboxMap.queryRenderedFeatures(screenPoint, MARKER_LAYER_ID_CHALLENGE));
        if (!features.isEmpty())
        {
            String event_id = features.get(0).getStringProperty(Events_DB_VM.EVENT_ID);
            Log.v(TAG,"Feature Name: "+event_id);
            List<Feature> featureList = map_event_creator.get_feature_collection().features();
            if (featureList != null)
            {

                Log.v(TAG,"Feature Size: "+featureList.size());
                for (int i = 0; i < featureList.size(); i++)
                {
                    if (featureList.get(i).getStringProperty(Events_DB_VM.EVENT_ID).equals(event_id))
                    {
                        Log.v(TAG,"featureList.get(i): "+featureList.get(i));
                        Log.d(TAG,"previous_number_of_selected_cards: "+number_of_selected_cards);


                        /// Check if the selected point has been opened before
                        if (map_event_creator.feature_select_status(i))
                        {

                            /// Check if user tapped the same event twice
                            if(featureList.get(i).getStringProperty(Events_DB_VM.EVENT_ID).equals(last_event_selected))
                            {
                                number_of_selected_cards--;
                                navigation_mapRoute.updateRouteVisibilityTo(false);
                                symbol_manager.delete(list_of_of_added_points_symbol);
                                list_of_of_added_points_symbol.clear();

                                /// Check if no event is opened on the screen
                                if(number_of_selected_cards==0)
                                {
                                    set_camera_position(0, null);
                                    ((Main_Activity)getActivity()).bottom_navigation_visibility(true);
                                    bottom_sheet_click_join.setState(BottomSheetBehavior.STATE_HIDDEN);
                                }

                                map_event_creator.set_feature_select_state(featureList.get(i), false);
                                Log.v(TAG,"Item Deselected");
                            }
                            else
                            {
                                last_event_selected = featureList.get(i).getStringProperty(Events_DB_VM.EVENT_ID);
                                try {open_selected_pinned_event(featureList.get(i));}
                                catch (Exception e){Log.d(TAG,"Closing Bottom: "+e.getMessage());}
                            }
                        }
                        /// Open the selected point
                        else
                        {

                            /// Set the last selected point equal to the current selected point
                            last_event_selected = featureList.get(i).getStringProperty(Events_DB_VM.EVENT_ID);
                            number_of_selected_cards++;
                            //if(number_of_selected_cards>0)

                            /// Set camera in the proper position
                            try
                            {
                                JSONObject geometry = new JSONObject(featureList.get(i).geometry().toJson());
                                open_selected_pinned_event(featureList.get(i));
                                JSONArray point = new JSONArray(geometry.get("coordinates").toString());

                                set_camera_position(50,new LatLng(Double.parseDouble(point.get(1).toString())
                                        ,Double.parseDouble(point.get(0).toString())));
                            }
                            catch (Exception e)
                            {
                                Log.d(TAG,"Feature selected Exception: "+e);
                            }

                            map_event_creator.set_active_event_to_selected_point(i);
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





    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain)
    {

    }

    @Override
    public void onPermissionResult(boolean granted)
    {
        if (granted)
        {
            //enable_location_component();
        }
        else
        {
            getActivity().finish();
        }

    }


    private static class Load_GeoJson_Data_Task extends AsyncTask<Void, Void, FeatureCollection>
    {

        private final WeakReference<map_fragment> activityRef;

        Load_GeoJson_Data_Task(map_fragment activity, int type)
        {
            this.activityRef = new WeakReference<>(activity);
            Log.v(TAG,"LoadGeoJsonDataTask: constructor");

        }

        @Override
        protected FeatureCollection doInBackground(Void... params)
        {
            Log.d(TAG,"Load_GeoJson_Data_Task activated");
            map_fragment activity = activityRef.get();

            if (activity == null)
            {
                return null;
            }

            Log.v(TAG,"Feature Collection Returned");

            file_maker geo_json_holder = new file_maker(file_directory_static,FILE_NAME);

            Log.v(TAG,"LoadGeoJsonDataTask: doInBackground");
            return FeatureCollection.fromJson(geo_json_holder.read_json().toString());
        }

        @Override
        protected void onPostExecute(FeatureCollection featureCollection)
        {
            super.onPostExecute(featureCollection);
            Log.v(TAG,"On post execute");
            map_fragment activity = activityRef.get();
            if (featureCollection == null || activity == null)
            {
                return;
            }

            // This example runs on the premise that each GeoJSON Feature has a "selected" property,
            // with a boolean value. If your data's Features don't have this boolean property,
            // add it to the FeatureCollection 's features with the following code:

            FeatureCollection feature_collection_ensemble = null;
            FeatureCollection feature_collection_others = null;
            List<Feature> features_ensemble= new ArrayList<>();
            List<Feature> features_others= new ArrayList<>();

            for (Feature singleFeature : featureCollection.features())
            {

                singleFeature.addBooleanProperty(PROPERTY_SELECTED, false);
                //feature_collection_ensemble.features() = FeatureCollection.fromFeature(singleFeature)
                Log.d(TAG,"singleFeature.properties(): "+singleFeature.properties().toString());
                if(singleFeature.properties().get("event_type").toString().equals("\"0\""))
                {
                    //feature_collection_ensemble.features().add(singleFeature);
                    Log.d(TAG,"event_type = 0");
                    features_ensemble.add(singleFeature);
                }
                else
                {
                    //feature_collection_others.features().add(singleFeature);
                    Log.d(TAG,"event_type = 1");
                    features_others.add(singleFeature);
                }
                Log.v(TAG,"Sing_Feature: "+singleFeature.properties());
                Log.v(TAG,"PROPERTY_SELECTED, False");
            }

            //activity.set_feature_collection(featureCollection);
            activity.map_event_creator.set_feature_collection(featureCollection);
            if(features_ensemble.size()!=0)
            {
                feature_collection_ensemble = FeatureCollection.fromFeatures(features_ensemble);
                activity.map_event_creator.setUpData(feature_collection_ensemble, 0);
            }

            if(features_others.size()!=0)
            {
                feature_collection_others = FeatureCollection.fromFeatures(features_others);
                activity.map_event_creator.setUpData(feature_collection_others, 1);
            }

            Generate_View_Icon_Task generateViewIconTask=null;
            Log.v(TAG,"LoadGeoJsonDataTask: onPostExecute");
            new Generate_View_Icon_Task(activity).execute(featureCollection);

        }

    }

    /**
     * This method handles click events for SymbolLayer symbols.
     * <p>
     * When a SymbolLayer icon is clicked, we moved that feature to the selected state.
     * </p>
     *
     * @param screenPoint the point on screen clicked
     */

    /**
     * AsyncTask to generate Bitmap from Views to be used as iconImage in a SymbolLayer.
     * <p>
     * Call be optionally be called to update the underlying data source after execution.
     * </p>
     * <p>
     * Generating Views on background thread since we are not going to be adding them to the view hierarchy.
     * </p>
     */
    private static class Generate_View_Icon_Task extends AsyncTask<FeatureCollection, Void, HashMap<String, Bitmap>>
    {

        private final HashMap<String, View> viewMap = new HashMap<>();
        private final WeakReference<map_fragment> activityRef;
        //private final boolean refreshSource;
        @SuppressLint("StaticFieldLeak")
        // final Context context;
        private boolean is_user_joined;

        Generate_View_Icon_Task(map_fragment activity, boolean refreshSource)
        {
            this.activityRef = new WeakReference<>(activity);
            //this.refreshSource = refreshSource;
            //this.context = activity.getContext();
            Log.v(TAG,"GenerateViewIconTask: Constructor_1");
        }

        Generate_View_Icon_Task(map_fragment activity)
        {
            this(activity, false);
            Log.v(TAG,"GenerateViewIconTask: Constructor_2");
        }


        @SuppressWarnings("WrongThread")
        @Override
        protected HashMap<String, Bitmap> doInBackground(FeatureCollection... params)
        {
            map_fragment activity = activityRef.get();
            if (activity != null)
            {
                HashMap<String, Bitmap> imagesMap = new HashMap<>();
                LayoutInflater inflater = LayoutInflater.from(activity.getActivity()); //////

                FeatureCollection featureCollection = params[0];

                for (Feature feature : featureCollection.features())
                {

                    @SuppressLint("InflateParams") ConstraintLayout constraint_layout =
                            (ConstraintLayout) inflater.inflate(R.layout.event_id_card, null);

                    /// Name Section

                    String user_name = feature.getStringProperty(Events_DB_VM.USER_NAME);
                    Log.v(TAG,"Events_DB_VM.USER_NAME: "+ user_name);
                    TextView titleTextView = constraint_layout.findViewById(R.id.invitor_name);
                    titleTextView.setText(user_name);


                    /// Created Date Section
                    String event_date_created = feature.getStringProperty(EVENT_DATE_CREATED);
                    Log.v(TAG,"Events_DB_VM.EVENT_DATE_CREATED: "+ event_date_created);
                    TextView event_date_created_tv = constraint_layout.findViewById(R.id.event_date_created_tv);
                    event_date_created_tv.setText(Date_Time_Provider.date_to_MDY(event_date_created));

                    /// Event Date Section
                    String event_date = feature.getStringProperty(EVENT_DATE);
                    Log.v(TAG,"Events_DB_VM.EVENT_DATE: "+event_date);
                    TextView event_date_tv = constraint_layout.findViewById(R.id.event_date_tv);
                    event_date_tv.setText(Date_Time_Provider.date_to_MDY(event_date)); //



                    /// Profile Pix Section
                    String profile_pic_name = feature.getStringProperty(USER_PIC);
                    //Log.v(TAG,"Events_DB_VM.USER_PIC: "+profile_pic_name);
                    CircleImageView profile_image = constraint_layout.findViewById(R.id.profile_image);
                    Bitmap profile_pic = Image_Provider.get_profile_bmp(profile_pic_name);
                    if(profile_pic!=null)
                        profile_image.setImageBitmap(profile_pic);

                    /// Number of Joined User Section
                    try
                    {
                        String num_of_joined = feature.getStringProperty(Events_DB_VM.NUM_OF_JOINED);
                        Log.v(TAG,"Events_DB_VM.NUM_OF_JOINED: "+num_of_joined);
                        TextView number_of_joined_users_tv = constraint_layout.findViewById(R.id.num_of_joined);
                        number_of_joined_users_tv.setText(num_of_joined);
                    }
                    catch (Exception e)
                    {
                        Log.v(TAG,"Events_DB_VM.NUM_OF_JOINED: Failed");
                    }


                    /// Is User Joined Section
                    String is_user_joined = feature.getStringProperty(Events_DB_VM.IS_USER_JOINED);


                    int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    constraint_layout.measure(measureSpec, measureSpec);


                    /// Naming each car identically using event_id
                    String event_id = feature.getStringProperty(Events_DB_VM.EVENT_ID);
                    Bitmap bitmap = map_fragment.SymbolGenerator.generate(constraint_layout);
                    imagesMap.put(event_id, bitmap);
                    viewMap.put(event_id, constraint_layout);
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
            map_fragment activity = activityRef.get();
            if (activity != null && bitmapHashMap != null)
            {
                activity.map_event_creator.setImageGenResults(bitmapHashMap);
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
    public void onStart()
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
    public void onStop()
    {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
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
    public void onDestroy()
    {
        super.onDestroy();
        if (mapboxMap != null)
        {
            mapboxMap.removeOnMapClickListener(this);
        }
        mapView.onDestroy();
        mapbox_navigation.onDestroy();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }
}